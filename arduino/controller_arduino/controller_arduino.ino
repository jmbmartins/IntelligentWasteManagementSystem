#include <Servo.h>

const int id=1;
// Define os pinos dos sensores ultrassônicos
const int trigPin1 = 3; // Pino de trigger do sensor 1
const int echoPin1 = 4; // Pino de eco do sensor 1
const int trigPin2 = 8; // Pino de trigger do sensor 2
const int echoPin2 = 9; // Pino de eco do sensor 2
const int trigPin3 = 11; // Pino de trigger do sensor 3
const int echoPin3 = 12; // Pino de eco do sensor 3

// Define o pino ao qual o servo está conectado
const int servoPin = 6;

// Cria um objeto Servo para controlar o servo motor
Servo servo;
 

void setup() {
  // Inicializa o objeto Servo
  servo.attach(servoPin);
  int id=1;
  // Inicializa os pinos dos sensores ultrassônicos
  pinMode(trigPin1, OUTPUT);
  pinMode(echoPin1, INPUT);
  pinMode(trigPin2, OUTPUT);
  pinMode(echoPin2, INPUT);
  pinMode(trigPin3, OUTPUT);
  pinMode(echoPin3, INPUT);

  // Inicia a comunicação serial para depuração (opcional)
  Serial.begin(9600);
}

void loop() {
  //Obrigar o servo a iniciar/voltar a posicao inicial
  int posInicial = 0;
  int posFinal = 180;
  // Move o servo do posInicial para posFinal em incrementos de 15 graus
  for(int pos = posInicial; pos <= posFinal; pos += 15) {
  servo.write(pos);

  // Realiza a leitura dos três sensores ultrassônicos
    float distance1 = lerSensor(trigPin1, echoPin1);
    float distance2 = lerSensor(trigPin2, echoPin2);
    float distance3 = lerSensor(trigPin3, echoPin3);
    
    printResults(pos, distance1, distance2, distance3,id);
    wait(1);
  }

  for(int pos = posFinal; pos >= posInicial; pos -= 15) {
    servo.write(pos);
    wait(1);
  }

  //Esperar 15min antes de ler again o caixote de lixo
  wait(5);
}

// Função para realizar as leituras dos sensores ultrassônicos
float lerSensor(int trigPin, int echoPin) {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  float duration = pulseIn(echoPin, HIGH);
  float distance = (duration * 0.0343) / 2;
  if (distance>1000){
    distance=0;
  }
  return distance;
}

// Função para dar output aos resultados 
void printResults(int pos, float distance1, float distance2, float distance3,int id) {
String output = String(id) + ":" + String(pos) + ":" + String(distance1) + ":" + String(distance2) + ":" + String(distance3) + "\n";
Serial.print(output);

} 

// Função para adicionar o delay da movimentação 
void wait(int waitTime) {
  waitTime = waitTime * 1000; // Converter para segundos
  delay(waitTime); // Aguarda o tempo especificado
}
