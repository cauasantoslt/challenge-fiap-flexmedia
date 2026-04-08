from flask import Flask, request, jsonify
import google.generativeai as genai
import requests, os, json, time
from dotenv import load_dotenv

app = Flask(__name__)
load_dotenv()

genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

instrucoes_sistema = """Você é a assistente virtual corporativa do Totem Flexmedia.
Suas respostas serão lidas em voz alta por um sintetizador de voz (TTS), portanto:
1. Seja extremamente concisa, direta e educada (máximo de 2 a 3 frases).
2. NUNCA use formatações como asteriscos, negritos, emojis ou markdown.
3. Foque em ajudar o usuário com informações do local, clima ou suporte."""

modelo = genai.GenerativeModel(
    'gemini-2.5-flash',
    system_instruction=instrucoes_sistema,
    generation_config={"response_mime_type": "application/json"}
)

# ================= MEMÓRIA PERMANENTE (JSON) =================
ARQUIVO_METRICAS = 'metricas.json'

def carregar_metricas():
    if os.path.exists(ARQUIVO_METRICAS):
        try:
            with open(ARQUIVO_METRICAS, 'r') as f:
                return json.load(f)
        except:
            pass
    return {"interacoes_hoje": 0, "tempo_total_segundos": 0.0, "status_ia": "ONLINE"}

def salvar_metricas(metricas_atuais):
    try:
        with open(ARQUIVO_METRICAS, 'w') as f:
            json.dump(metricas_atuais, f)
    except Exception as e:
        print(f"Erro ao salvar métricas: {e}")

# Carrega a memória assim que o servidor liga
metricas = carregar_metricas()
# ==============================================================

@app.route('/api/clima', methods=['GET'])
def obter_clima():
    try:
        r = requests.get("https://api.open-meteo.com/v1/forecast?latitude=-23.54&longitude=-46.63&current_weather=true", timeout=5)
        temp = r.json()['current_weather']['temperature']
        return jsonify({"clima": f"{temp}°C"}), 200
    except Exception as e:
        print(f"Erro clima: {e}")
        return jsonify({"clima": "--°C"}), 500

@app.route('/api/interacao', methods=['POST'])
def interacao():
    try:
        dados = request.get_json()
        texto_usuario = dados.get('texto', '') 

        prompt = f"""
        Responda à pergunta do usuário abaixo.
        Sua saída DEVE ser estritamente em JSON com esta exata chave:
        "respostaIa": "Sua resposta falada em texto limpo"

        Pergunta: {texto_usuario}
        """

        inicio = time.time()
        
        res = modelo.generate_content(prompt)
        resposta_json = json.loads(res.text)
        
        fim = time.time()
        latencia = fim - inicio
        
        # Atualiza os dados e salva fisicamente no arquivo
        metricas["interacoes_hoje"] += 1
        metricas["tempo_total_segundos"] += latencia
        salvar_metricas(metricas)

        print(f"MENSAGEM PARA O ANDROID (Levou {latencia:.1f}s):", resposta_json)
        return jsonify(resposta_json), 200

    except Exception as e:
        print(f"Erro IA: {e}")
        return jsonify({"respostaIa": "Erro interno no servidor."}), 500

@app.route('/api/metricas', methods=['GET'])
def obter_metricas():
    latencia_media = 0
    if metricas["interacoes_hoje"] > 0:
        latencia_media = metricas["tempo_total_segundos"] / metricas["interacoes_hoje"]
        
    return jsonify({
        "status": metricas["status_ia"],
        "interacoes": metricas["interacoes_hoje"],
        "latenciaMedia": f"{latencia_media:.1f}s"
    }), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)