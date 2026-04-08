#include <WiFi.h>
#include <WebServer.h>

// --- CONFIGURAÇÕES DE REDE ---
const char* ssid = "NOME_DA_SUA_REDE";
const char* password = "SENHA_DA_REDE";

// Inicializa o servidor web na porta 80
WebServer server(80);

// --- PINOS DOS LEDs ---
// Substitua pelos pinos que você usará no ESP32.
// Se for usar um emissor IR para fita de LED, use o pino do IR aqui.
const int pinR = 13;
const int pinG = 12;
const int pinB = 14;

void setup() {
  Serial.begin(115200);
  
  // Configura os pinos como saída
  pinMode(pinR, OUTPUT);
  pinMode(pinG, OUTPUT);
  pinMode(pinB, OUTPUT);

  // Inicia com LEDs apagados
  definirCor(0, 0, 0);

  // Conecta ao Wi-Fi
  Serial.println("Conectando ao Wi-Fi...");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("\nWi-Fi Conectado!");
  Serial.print("Endereço IP do ESP32: ");
  Serial.println(WiFi.localIP());

  // --- MAPEAMENTO DAS ROTAS DA API ---
  
  // Quando o Totem está na tela de descanso (Preta com logo)
  server.on("/standby", HTTP_GET, []() {
    // Ex: Fita de LED Verde suave
    definirCor(0, 50, 0); 
    server.send(200, "text/plain", "Modo Standby Ativado");
    Serial.println("Estado: STANDBY");
  });

  // Quando o Android acorda e vai para a Home
  server.on("/acordado", HTTP_GET, []() {
    // Ex: Fita de LED Branca ou cor oficial da Flexmedia
    definirCor(100, 100, 100); 
    server.send(200, "text/plain", "Modo Acordado Ativado");
    Serial.println("Estado: ACORDADO");
  });

  // Quando a IA do Python está processando a resposta
  server.on("/pensando", HTTP_GET, []() {
    // Ex: Fita de LED Azul Tecnológico
    definirCor(0, 0, 255); 
    server.send(200, "text/plain", "Modo Pensando Ativado");
    Serial.println("Estado: PENSANDO");
  });

  // Inicia o servidor
  server.begin();
  Serial.println("Servidor HTTP Iniciado.");
  
  // Pisca verde indicando que ligou com sucesso
  definirCor(0, 255, 0); delay(500); definirCor(0,0,0);
}

void loop() {
  // Mantém o servidor escutando as requisições
  server.handleClient();
}

// Função auxiliar para mudar a cor via PWM
void definirCor(int r, int g, int b) {
  analogWrite(pinR, r);
  analogWrite(pinG, g);
  analogWrite(pinB, b);
}