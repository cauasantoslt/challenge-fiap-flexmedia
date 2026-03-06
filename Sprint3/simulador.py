import requests
import random
import time

# A URL da sua API rodando no próprio PC
URL = "http://127.0.0.1:5000/api/dados"

print("🤖 Iniciando simulação de interações no Totem...")

interacoes_geradas = 0
for _ in range(50):
    # Simula alguém passando ou parando na frente do totem (entre 10cm e 100cm)
    distancia = round(random.uniform(10.0, 100.0), 2)
    
    # Regra de negócio: Só registra se a pessoa chegou a menos de 60cm
    if distancia <= 60:
        payload = {
            "sensor": "presenca_ultrassonico",
            "distancia_cm": distancia
        }
        try:
            resposta = requests.post(URL, json=payload)
            if resposta.status_code == 201:
                print(f"✅ Interação registrada: {distancia}cm")
                interacoes_geradas += 1
        except Exception as e:
            print("❌ Erro de conexão. A API Flask (app.py) está rodando?")
            break
            
    # Pausa curta para simular o tempo real
    time.sleep(0.3)

print(f"\n🎯 Simulação concluída! {interacoes_geradas} registros inseridos no banco de dados.")