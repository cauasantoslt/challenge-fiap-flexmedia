from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
import os

app = Flask(__name__)

# --- O SEGREDO DA INTEGRAÇÃO ---
# Força a API a criar o banco exatamente na mesma pasta (Sprint3) onde ela está salva
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(BASE_DIR, 'totem_data.db')

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + db_path
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# --- MODELO DO BANCO DE DADOS ---
class Interacao(db.Model):
    __tablename__ = 'interacao' # Nome explícito da tabela para o Streamlit achar
    id = db.Column(db.Integer, primary_key=True)
    sensor = db.Column(db.String(50), nullable=False)
    distancia_cm = db.Column(db.Float, nullable=False)
    data_hora = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "sensor": self.sensor,
            "distancia_cm": self.distancia_cm,
            "data_hora": self.data_hora.strftime("%Y-%m-%d %H:%M:%S")
        }

# Cria as tabelas antes da primeira requisição
with app.app_context():
    db.create_all()

# --- ROTAS DA API ---
@app.route('/api/dados', methods=['POST'])
def receber_dados():
    dados = request.get_json()
    
    if not dados or 'sensor' not in dados or 'distancia_cm' not in dados:
        return jsonify({"erro": "Dados inválidos. Faltam parâmetros."}), 400
    
    try:
        nova_interacao = Interacao(
            sensor=dados['sensor'],
            distancia_cm=dados['distancia_cm']
        )
        db.session.add(nova_interacao)
        db.session.commit()
        return jsonify({"mensagem": "Dado registrado com sucesso!"}), 201
        
    except Exception as e:
        db.session.rollback()
        return jsonify({"erro": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, port=5000)