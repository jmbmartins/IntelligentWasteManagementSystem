const port = 3000;
const http = require('http');
const { SerialPort } = require('serialport');
const { ReadlineParser } = require('@serialport/parser-readline');

const comPort1 = new SerialPort({
    path: "/dev/ttyS4",
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

        // Enviar dados para a API
        sendDataToAPI(extractedData);
    } else {
        console.error("Failed to parse data:", data);
        deletePreviousRecords(); 
        console.warn("Previous records with a close timestamp have been deleted due to a parsing error.");
    }
});

function sendDataToAPI(data) {
    const options = {
        hostname: 'snf-63590.vm.okeanos-global.grnet.gr',
        port: 3000,
        path: '/api/sensorData',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    };

	const req = http.request(options, (res) => {
    
	});

    req.write(JSON.stringify(data));
    req.end();
}

function deletePreviousRecords(timestamp) {
    const thirtySecondsAgo = new Date(timestamp - 30 * 1000);
  
    const query = 'DELETE FROM SensorData WHERE timestamp <= ? AND timestamp >= ?';
  
    connection.query(query, [timestamp, thirtySecondsAgo], function (error, results, fields) {
      if (error) {
        console.error('Failed to delete records:', error);
      } else {
        console.log('Deleted records with timestamps within 30 seconds of:', timestamp);
      }
    });
  }
