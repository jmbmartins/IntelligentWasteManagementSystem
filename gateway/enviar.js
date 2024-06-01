const http = require('http');
const { SerialPort } = require('serialport');
const { ReadlineParser } = require('@serialport/parser-readline');

const MAX_RECORDS = 13;
let bufferedData = [];

const comPort1 = new SerialPort({
    path: "/dev/ttyS3",
    baudRate: 9600,
    dataBits: 8,
    stopBits: 1,
    parity: 'none',
});

const parser = comPort1.pipe(new ReadlineParser({ delimiter: '\n' }));

parser.on("data", function (data) {
    const regex = /^(\d+):(\d+):(\d+\.\d+):(\d+\.\d+):(\d+\.\d+)$/;
    const match = data.match(regex);

    if (match) {
        const id = match[1];
        const position = parseFloat(match[2]);
        const sensor1 = parseFloat(match[3]);
        const sensor2 = parseFloat(match[4]);
        const sensor3 = parseFloat(match[5]);

        // Criar objeto com os dados extraídos
        const extractedData = {
            ID_Container: id, 
            position: position,
            s1_r: sensor1,        
            s2_r: sensor2,
            s3_r: sensor3,
        };
		
        if (position != 0 && bufferedData.length===0){
			bufferedData= []
		}else{
		    bufferedData.push(extractedData);
		}
		

        if (bufferedData.length === MAX_RECORDS) {
            sendDataToAPI(bufferedData);
            bufferedData = [];
        }
    } else {
        console.error("Failed to parse data:", data);
		bufferedData = [];
    }
});

async function sendDataToAPI(data) {
    const options = {
        hostname: 'snf-63590.vm.okeanos-global.grnet.gr',
        port: 3000,
        path: '/api/sensorData',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    for (const item of data) {
        await new Promise((resolve, reject) => {
            const req = http.request(options, (res) => {
                let responseData = '';
                res.on('data', (chunk) => {
                    responseData += chunk;
                });
                res.on('end', () => {
                    console.log('Response from API:', responseData);
                    resolve(); // Resolve a promessa quando a requisição for concluída
                });
            });

            req.on('error', (e) => {
                console.error(`Problem with request: ${e.message}`);
                reject(e); // Rejeita a promessa em caso de erro
            });

            // Tratamento de erro para casos em que a requisição HTTP falha
            req.on('error', (error) => {
                console.error('Error with HTTP request:', error);
                reject(error); // Rejeita a promessa em caso de erro
            });

            req.write(JSON.stringify(item));
            req.end();
        });
    }
}
