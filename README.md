# FIAP - Faculdade de Informática e Administração Paulista

<p align="center">
<a href= "https://www.fiap.com.br/"><img src="./Sprint1/assets/logo-fiap.png" alt="FIAP - Faculdade de Informática e Admnistração Paulista" border="0" width=40% height=40%></a>
</p>

<br>

# Enterprise Challenge - FlexMedia x FIAP

_Uma proposta de totem inteligente com IA, capaz de integrar diferentes tecnologias, promover personalização e enriquecer a interação dos usuários em ambientes de lazer e comércio._

### 📺 Demonstração Funcional (Sprint 4 - IA e Interação)
Confira o vídeo final demonstrando o aplicativo Android nativo, reconhecimento de voz, integração com a IA Gemini e o Dashboard rodando via Docker:

[**CLIQUE AQUI PARA ASSISTIR AO VÍDEO NO YOUTUBE**](INSERIR_LINK_DO_YOUTUBE_AQUI)

## Grupo 

## 👨‍🎓 Integrantes:

- <a href="https://www.linkedin.com/in/amanda-damasceno-martins/">566598 - Amanda Damasceno Martins</a>
- <a href="https://www.linkedin.com/in/cauasantoslt">566599 - Cauã Santos</a>
- <a href="https://www.linkedin.com/in/fabio-baldo-7959a22a/">567851 - Fabio Baldo</a>
- <a href="https://www.linkedin.com/in/giovanna-gomes-82b993372/">567169 - Giovanna Gomes Oliveira</a>
- <a href="https://www.linkedin.com/in/roberto-alvares-785059215/">568265 - Roberto Almeida Alvares</a>

## 👩‍🏫 Professores:

### Tutor(a)

- <a href="https://www.linkedin.com/in/sabrina-otoni-22525519b/">Sabrina Otoni</a>

### Coordenador(a)

- <a href="https://www.linkedin.com/in/andregodoichiovato/">André Godoi</a>

---

## 📜 Descrição da Sprint 4 (Entrega Final)
Nesta etapa final, o protótipo evoluiu para uma solução digital interativa completa, incorporando Inteligência Artificial Generativa, processamento de linguagem natural e infraestrutura em nuvem, garantindo acessibilidade, resiliência e geração de métricas analíticas.

### 1. Inteligência Artificial Generativa (Gemini)
Integramos a API do **Google Gemini 2.5 Flash** ao nosso backend (Flask). A IA foi instruída com um *system prompt* específico para atuar como assistente corporativa do Totem, garantindo respostas concisas, educadas e sem formatações incompatíveis com a leitura por voz.

### 2. Acessibilidade e Interação Multimodal (App Android)
Desenvolvemos um aplicativo cliente nativo em **Kotlin** que substitui a interface simulada:
* **Voz para Texto (STT):** Uso do `SpeechRecognizer` para captar comandos de voz do visitante.
* **Texto para Voz (TTS):** Uso do `TextToSpeech` nativo com ajustes de pitch e velocidade (buscando vozes Premium de rede) para responder ao usuário com naturalidade.
* **Visão Computacional:** Implementação do **Google ML Kit** para detecção facial via câmera frontal, tirando o totem do modo *Standby* automaticamente ao detectar um visitante.
* **Resiliência (Offline Fallback):** Verificação de status de rede (`ConnectivityManager`). Caso o Wi-Fi caia, o totem responde de forma autônoma (informando a falha) sem travar a aplicação.

### 3. Infraestrutura e Persistência (Docker)
O servidor Flask foi refatorado para operar em um ambiente conteinerizado:
* **Docker:** Criação de `Dockerfile` e `requirements.txt`, isolando o ambiente (Python 3.11-slim) e deixando a API pronta para *deploy* em nuvem (OCI, GCP ou AWS).
* **Memória Permanente:** Implementação de persistência em arquivo (`metricas.json`), garantindo que interações diárias e métricas de latência não sejam perdidas caso o servidor ou contêiner reinicie.

### 4. Relatório Analítico e Machine Learning Clássico
Atendendo aos requisitos de dados estruturados e avaliação de desempenho, desenvolvemos um Jupyter Notebook (`analise_totem_ia.ipynb`):
* **Dataset Simulado:** Geração de 500 registros de visitantes (idade, idioma, período, tempo de interação).
* **Modelo Preditivo:** Treinamento de um classificador **Random Forest** (Scikit-Learn) com divisão de Treino (80%) e Teste (20%) para classificar e recomendar atrações.
* **Métricas e Gráficos:** Geração de relatórios de Acurácia, *Classification Report*, Matriz de Confusão e visualizações analíticas (distribuição de idiomas, perfil de idade, tempo de uso) geradas via Seaborn/Matplotlib.

### 🔌 Integração IoT: Suporte a Hardware e Sensores (ESP32)

O ecossistema do **TotemFlexmedia** foi projetado com uma arquitetura *IoT-ready*. Embora a aplicação Android e o processamento de IA operem de forma independente e autônoma, o backend (Flask/Docker) possui endpoints dedicados e escaláveis para comunicação direta com módulos de hardware externo, especificamente o **ESP32**.

Essa integração permite que o totem transcenda a interação digital e reaja ativamente ao ambiente físico:

* **Detecção de Presença Externa (PIR / HC-SR04):** O ESP32 pode ser posicionado estrategicamente para detectar a aproximação de visitantes a metros de distância. Ao detectar movimento, o microcontrolador envia um payload (JSON) via requisição `POST` para a API, que pode acionar gatilhos para tirar o Totem do modo *Standby* de forma proativa.
* **Telemetria Ambiental (DHT22 / BME280):** Coleta de dados físicos do ambiente (temperatura, umidade e luminosidade). Esses dados são repassados ao nosso servidor, armazenados no banco de dados e podem ser utilizados para atualizar widgets climáticos na tela do Android ou gerar relatórios para a gestão do local.
* **Comunicação Desacoplada:** O ESP32 atua como um nó de borda (*Edge Computing*). Ele não interfere no ciclo de vida do aplicativo Android; ambos se comunicam através da nossa API centralizada, garantindo que o sistema continue resiliente mesmo em caso de falhas de hardware ou desconexão do sensor.

Essa infraestrutura garante que a solução atenda não apenas aos requisitos de software e Inteligência Artificial, mas também atue como um dispositivo sensoriado plenamente integrado ao mundo físico.


## 1. Arquitetura Consolidada da Solução
O sistema respeita um pipeline de dados profissional: **Sensor (IoT) $\rightarrow$ API (Backend) $\rightarrow$ Banco de Dados (SQL) $\rightarrow$ Inteligência (ML) $\rightarrow$ Visualização (Dashboard).**

#### 1.1 Coleta de Dados (Hardware Simulado)
Utilizamos o simulador **Wokwi** para validar a lógica do sensor ultrassônico **HC-SR04** conectado a um **ESP32**. O sistema detecta a presença de usuários a menos de 60cm do totem e dispara uma requisição HTTP POST contendo os dados da interação.

#### 1.2 Backend e Armazenamento (Flask & SQLite)
Desenvolvemos uma API em **Python (Flask)** hospedada localmente no VS Code. 
* **Validação:** A API valida a integridade dos dados recebidos.
* **Persistência:** Os dados são salvos em um banco de dados **SQLite**, garantindo a integridade relacional com chaves primárias e registros temporais (timestamps).

#### 1.3 Inteligência Artificial e Machine Learning
O sistema consome os dados do banco para alimentar um modelo de **Random Forest (Scikit-Learn)**. 
* **Objetivo:** Prever o nível de engajamento do usuário com base na proximidade da interação.
* **Métrica:** O dashboard exibe a **Acurácia** do modelo e o **Classification Report**, garantindo a interpretabilidade dos resultados exigida pelo Challenge.

#### 1.4 Dashboard Interativo (Streamlit)
A interface final apresenta:
* Fluxo de dados em tempo real.
* Distribuição estatística de distâncias (Seaborn/Matplotlib).
* Monitoramento de volume de interações por período.
* Painel de performance do modelo de Machine Learning.

### 2. Estratégia de Segurança e Integridade
* **Validação de Entrada:** A API descarta pacotes malformados ou incompletos.
* **Segurança Conceitual:** Implementação de headers customizados (`User-Agent` e `ngrok-skip-browser-warning`) para controle de acesso e bypass de segurança em túneis de desenvolvimento.
* **Integridade:** Uso de SQLAlchemy para garantir que apenas dados válidos sejam persistidos no SQLite.

### 3. Estratégia de Acessibilidade e Equidade

* **Ponto de Acesso Universal:** A interface terá um "Menu de Acessibilidade" sempre visível (ícone fixo) para ativar Leitor de Tela, Alto Contraste, Controle por Voz e Multi-idioma.
* **Interação Multimodal:** Suporte completo via Toque (Touchscreen), Voz (Microfone MTM-3201 + Google Speech-to-Text) e Leitor de Tela (TalkBack + Alto-falantes).
* **Equidade de Acesso:** O "Modo Público Anônimo" (detalhado na Seção 6) garante que o totem é útil para todos, mesmo sem celular ou QR Code.

### 4. Arquitetura da Solução (Hardware e Software)

Esta seção detalha a arquitetura técnica, tecnologias e o fluxo de dados da solução.

#### 4.1. Esboço da Arquitetura (Diagrama)

O diagrama abaixo representa o fluxo técnico do Totem Inteligente FlexMedia – "Modelo Tango", desde a interação do visitante até o processamento em nuvem e geração de insights.

A solução integra hardware (Totem MTM-3201 e sensores ESP32), aplicação Android (Kotlin) e serviços na Google Cloud Platform, com o Vertex AI (Gemini) atuando como núcleo de IA conversacional.

Todos os dados são transmitidos com segurança via Cloud Run (API Backend), armazenados em Firestore e BigQuery, e exibidos em dashboards de Looker Studio.

A arquitetura é modular, acessível e totalmente aderente à LGPD, garantindo equidade, anonimização local e governança de dados em tempo real.

*Para ver o fluxograma (diagrama) em tamanho real, [clique neste link](https://drive.google.com/file/d/1gXG1C9nCD_qj7AXG-Ya2hkVu7tJmJ37D/view?usp=sharing).*

### 🔵🟢 Legenda de Fluxos (Color Code)

As setas no diagrama seguem um código de cores que representa o fluxo de dados e processamento dentro da arquitetura:

* **🔵 Fluxo principal (ida):** Representa o envio de informações do usuário e sensores — desde a interação no Totem até o processamento na nuvem (Cloud Run e Vertex AI).
* **🟢 Fluxo de retorno (volta):** Indica as respostas da IA (Gemini) e os dados retornados ao Totem ou dashboards (ex: recomendações, textos, voz, BI).
* **⚫ Cinza Pontilhado (Contexto):** Elementos referenciais (como o ambiente físico), que não trocam dados diretamente, mas contextualizam a operação do sistema.

#### 4.2. Hardware (Borda / IoT)

A nossa estratégia de hardware é centrada em uma unidade principal robusta, complementada por um módulo de sensores IoT flexível.

##### Hardware Principal: O Totem

A solução é construída ao redor do **`Totem Tomate MTM-3201`**. A escolha é estratégica por ele ser uma unidade "all-in-one" que já inclui os componentes essenciais para as *features* que definimos:

* **Sistema Android Nativo:** Permite rodar nosso aplicativo (desenvolvido em Kotlin/Android Studio) de forma otimizada e facilita a comunicação com o Google Cloud e nosso assistente "Modelo Tango".
* **Câmera Integrada:** Fundamental para o "Modo Pessoal", permitindo a leitura do **QR Code** para autenticação.
* **Microfone e Alto-falantes:** Componentes chave da nossa estratégia de **Acessibilidade**, permitindo os Comandos de Voz e o Leitor de Tela (TalkBack).
* **Design Robusto:** O design integrado é ideal para o ambiente de alto fluxo de um cruzeiro, facilitando a manutenção.

##### Módulo Auxiliar: Sensores IoT (ESP32)

Para complementar o totem, utilizamos um **módulo auxiliar de IoT**, baseado no `ESP32` e conectado aos seguintes sensores:

* **`ESP32`:** Atua como o "mini-cérebro" dos sensores. Escolhido por seu baixo consumo de energia e conectividade Wi-Fi nativa, ele coleta os dados e os envia para a nuvem.
* **Sensor `PIR` (Presença):** Usado para detectar a aproximação de um usuário. Isso permite que o totem "acorde" (saia da tela de descanso) e inicie a interação proativamente (ex: "Olá, posso ajudar?"), como visto no `SENSOR_PAYLOAD.JSON`.
* **Sensor `DHT22/BME280` (Ambiente):** Mede a temperatura e umidade exatas do local. Esses dados são usados para alimentar o "Widget de Clima" no totem e para gerar relatórios de ambiente para a gestão do navio.

Este módulo (ESP32) envia seus dados de sensor diretamente para a nossa API no Cloud Run, que por sua vez alimenta os dashboards de BI, conforme detalhado por completo nas seções `4.4` e `5`.

#### 4.3. Software (Aplicação)

* **IDE:** `Android Studio`
* **Linguagem:** `Kotlin`
* **Justificativa:** O desenvolvimento será nativo Android (usando Kotlin, a linguagem moderna preferida pelo Google) para garantir performance máxima, acesso direto à Câmera e Microfone do totem MTM-3201, e integração otimizada com o ecossistema Google Cloud.

#### 4.4. Nuvem (Backend e IA)
Nossa solução utiliza os serviços da Google Cloud Platform (GCP) para garantir alta disponibilidade, segurança e inteligência. A arquitetura conecta o totem MTM-3201 (App Android) e os sensores (`ESP32`) à nuvem através de uma API central.

O fluxo principal é: O usuário interage com o totem, o app Android manda essa informação para a API (hospedada no `Cloud Run`). Essa API conversa com o nosso assistente "Modelo Tango" (rodando no `Vertex AI Gemini`), que processa o pedido e retorna a resposta inteligente. Em paralelo, os dados da interação são salvos no `Firestore` (para uso imediato) e no `BigQuery` (para análises futuras).

Os serviços utilizados são:

* **Cloud Run:** Hospeda nossa API (backend) principal. Permite escalabilidade automática e garante alta disponibilidade sem gerenciamento de servidores.

* **Vertex AI (Gemini):** É o cérebro do "Modelo Tango". Entende voz e texto, responde de forma personalizada e fornece suporte multi-idioma, garantindo a acessibilidade que definimos na Seção 3.

* **Firestore:** Banco de dados NoSQL usado para guardar dados rápidos da aplicação, como sessões de usuário, preferências de idioma e reservas ativas.

* **BigQuery:** Nosso Data Warehouse. Analisa todos os dados históricos de interação e dos sensores, permitindo à gestão do navio (o cliente) visualizar os dashboards de BI (no Looker Studio).

* **Cloud Storage:** Armazena os arquivos estáticos que o totem precisa exibir, como as imagens da "Galeria de Fotos", vídeos e ícones da interface.

* **Firebase Authentication:** Cuida da segurança e autenticação, gerenciando tanto os administradores do sistema quanto o "Modo Pessoal" (autenticado via QR Code).

### 5. Estratégia de Coleta de Dados
Nossa estratégia de coleta de dados é projetada para respeitar a privacidade do usuário (LGPD) e, ao mesmo tempo, gerar valor analítico para a gestão.

Existem dois modos de coleta:

1. **Modo Pessoal (Autenticado):** Quando o usuário escaneia o QR Code, ele ativa um perfil temporário. O sistema coleta dados de interação (buscas, reservas, preferências) ligados a esse perfil para personalizar a experiência.

2. **Modo Anônimo (Padrão):** Não coleta nenhuma informação pessoal identificável. Apenas salva estatísticas gerais e anônimas (ex: "o botão 'mapa' foi clicado 100x hoje") para alimentar os dashboards de BI.

Para simular o pipeline de dados nesta Sprint 1, os blocos de código JSON abaixo representam os dados que o App Android e os sensores IoT (ESP32) enviarão para a nossa API no Cloud Run.

#### Exemplo 1: `INTERACAO_PAYLOAD.JSON`
(Simula o que o **App Android** envia após uma interação de voz)

```json 
  {
  "session_id": "sess_10294",
  "mode": "anonymous",
  "input_type": "voice",
  "input_text": "Quais eventos vão acontecer hoje à noite?",
  "tango_response": "Hoje teremos show de jazz no Deck 3 às 21h.",
  "timestamp": "2025-10-29T19:42:00Z",
  "context": {
    "device": "Totem MTM-3201",
    "language": "pt-BR",
    "location": "Deck 5"
    }
  }
```
#### Exemplo 2: `SENSOR_PAYLOAD.JSON`
(Simula o que o **Módulo ESP32** envia. Note que o ID do sensor é separado e aponta para o totem que ele serve)

```json
{
  "device_id": "ESP32-SENSOR-004",
  "linked_totem_id": "MTM3201-CRZ-01",
  "timestamp": "2025-10-29T19:43:00Z",
  "sensor_data": {
    "temperature": 23.8,
    "humidity": 71.4,
    "presence_detected": true
  },
  "location": "Deck 5 - Área de Lazer"
}
```

### 6. Estratégia de Segurança e Privacidade

A solução "Modelo Tango" foi estruturada com foco total em segurança, privacidade e transparência, alinhando-se às diretrizes da Lei Geral de Proteção de Dados (LGPD).

#### 6.1. Proteção de Dados (em Trânsito e Repouso)
* **Dados em Trânsito:** Toda a comunicação entre o totem (App Android e ESP32) e os serviços em nuvem (API no Cloud Run) é feita **exclusivamente por conexões criptografadas (HTTPS/TLS 1.3)**, evitando interceptações.
* **Dados em Repouso:** Os dados pessoais (como as preferências do Modo Pessoal) são **armazenados criptografados no Firestore**. Os serviços de nuvem (GCP) e backups também são criptografados por padrão.
* **Autenticação:** O acesso é protegido pelo **Firebase Authentication**, que gerencia a autenticação segura de administradores (com RBAC) e dos usuários no Modo Pessoal.

#### 6.2. Modos de Operação e Minimização de Dados (LGPD)
O sistema foi desenhado com base no princípio da minimização de dados, operando em dois modos distintos:

* **Modo Anônimo (Padrão):** Não coleta dados pessoais. São registradas apenas informações técnicas e estatísticas, como número de interações, comandos mais utilizados e dados de sensores (temperatura, umidade e presença).
* **Modo Pessoal (Opt-in):** Ativado quando o usuário escaneia um QR Code, criando um perfil temporário. Os dados são armazenados criptografados e excluídos automaticamente após o término da sessão. O usuário pode encerrar o modo pessoal a qualquer momento, preservando total controle sobre suas informações.

#### 6.3. Governança e Transparência
Para garantir total conformidade com a LGPD, o sistema segue práticas rigorosas de governança:
* Consentimento explícito e informado para uso do modo pessoal;
* Política de privacidade acessível diretamente no totem;
* Controle de acesso baseado em papéis (RBAC) para administradores;
* Logs de segurança e auditoria monitorados em tempo real via Cloud Logging.

### 7. Plano de Desenvolvimento (Divisão de Tarefas - Sprint 1)

* **Cauã (PM):** Definição do produto (Seções 1 e 2), priorização de Acessibilidade e integração do `README.md`.
* **Fabio (Arquiteto):** Desenho do diagrama de arquitetura da solução (Seção 4.1).
* **Giovanna (Cloud/Dados):** Detalhamento da arquitetura de nuvem (4.4) e estratégia de dados simulados (5).
* **Amanda (Hardware/IoT):** Detalhamento do hardware (4.2) e suas justificativas de acessibilidade.
* **Roberto (Segurança):** Detalhamento da estratégia de Segurança, Privacidade e Equidade (6).

---

## 📁 Estrutura de pastas

```sh
├── Android_App
│   └── TotemFlexmedia
│       └── app
│           └── src
│               └── main
│                   └── java
│                       └── com
│                           └── fiap
│                               └── totemflexmedia
│                                   ├── MainActivity.java
│                                   ├── ApiClient.java
│                                   ├── SpeechRecognizer.java
│                                   └── ...
│   
├── assets
│   ├── diagrama-arquitetura.jpg
│   ├── logo-fiap.png
│   └── logo-flexmedia.png
│
├── Backend_Docker
│   ├── app.py
│   ├── Dockerfile
│   ├── metricas.json
│   └── requirements.txt
│
├── Data_Science
│   └── analise_totem_ia.ipynb
│    
├── SystemESP32
│   └── SystemESP32.ino
│    
└── README.md
```

## 🚀 Instruções de Execução (Sprint 4 - IA e Docker)

### 1. Backend API (Docker)
Navegue até a pasta do backend (`Backend_Docker`) e certifique-se de ter o Docker instalado:
1. Construa a imagem:
   ```bash
   docker build -t totem-backend .

2. Rode o contêiner em segundo plano:

```bash
docker run -d -p 5000:5000 --name totem-server totem-backend
```

(O servidor estará escutando na porta 5000 local. Para parar o serviço, utilize o comando `docker stop totem-server`).

2. App Android
Abra a pasta `Android_App` no Android Studio, aguarde a sincronização do Gradle e execute o projeto em um emulador ou dispositivo físico conectado (com permissões de câmera e microfone habilitadas).

3. Relatório Analítico de Machine Learning
Navegue até a pasta `Data_Science` e abra o arquivo Jupyter:

```Bash
jupyter notebook analise_totem_ia.ipynb
```
Execute as células sequencialmente para gerar o dataset simulado, treinar o modelo Random Forest e visualizar as métricas de acurácia e gráficos do Relatório Analítico.

### 📋 Pré-requisitos
* Python 3.8 ou superior instalado.
* Bibliotecas listadas no `requirements.txt`.

### 🔧 Instalação
1. Clone este repositório.
2. Navegue até a pasta do projeto.
3. Instale as dependências:
   ```bash
   pip install -r Sprint2/requirements.txt
   ```


## 🗃 Histórico de lançamentos
* 0.4.0 - 07/04/2026
    * Implementação do MVP de IA com Docker, App Android e Relatório Analítico de Machine Learning.
* 0.3.0 - 26/11/2025
    * Garantir robustez, segurança e integração de todos os módulos para entrega final.

* 0.2.0 - 26/11/2025
    * Implementação do MVP de dados com simulador, banco SQL e Dashboard com ML.

* 0.0.1 - 30/10/2025
    * Criação do documento, definição de escopo (MVP), arquitetura e plano de desenvolvimento para a Sprint 1.

## 📋 Licença

<img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/cc.svg?ref=chooser-v1"><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/by.svg?ref=chooser-v1"><p xmlns:cc="http://creativecommons.org/ns#" xmlns:dct="http://purl.org/dc/terms/"><a property="dct:title" rel="cc:attributionURL" href="https://github.com/agodoi/template">MODELO GIT FIAP</a> por <a rel="cc:attributionURL dct:creator" property="cc:attributionName" href="https://fiap.com.br">Fiap</a> está licenciado sobre <a href="http://creativecommons.org/licenses/by/4.0/?ref=chooser-v1" target="_blank" rel="license noopener noreferrer" style="display:inline-block;">Attribution 4.0 International</a>.</p>