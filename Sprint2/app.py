import streamlit as st
import pandas as pd
import sqlite3
import time
# ... outros imports ...
from modules.ml_model import prever_status, gerar_relatorio_ml # <--- NOVO
# Configuração da Página
st.set_page_config(
    page_title="Dashboard Totem Tango",
    layout="wide",
    page_icon="🤖"
)

st.title("🤖 Dashboard em Tempo Real - Modelo Tango")

# Função para ler os dados do banco
def get_data():
    # Conecta no banco (modo leitura)
    conn = sqlite3.connect('database/dados_totem.db')
    
    # Lê tudo da tabela logs_totem e joga num DataFrame (tabela do Pandas)
    df = pd.read_sql("SELECT * FROM logs_totem ORDER BY id DESC LIMIT 50", conn)
    
    conn.close()
    return df

# Container que vai atualizar sozinho
placeholder = st.empty()

# Loop do Dashboard (para atualizar a tela)
while True:
    with placeholder.container():
        # 1. Pega os dados novos
        df = get_data()

        # 2. Mostra métricas (KPIs)
        # Pega a última linha (o dado mais recente)
        if not df.empty:
            ultimo_dado = df.iloc[0]
            
            col1, col2, col3 = st.columns(3)
            col1.metric("🌡️ Temperatura Atual", f"{ultimo_dado['temperatura']} °C")
            
            status = "Pessoa Detectada" if ultimo_dado['presenca'] == 1 else "Vazio"
            col2.metric("👀 Sensor de Presença", status)
            
            col3.metric("🕒 Última Leitura", ultimo_dado['data_hora'].split(' ')[1])

            # ... código das métricas col1, col2, col3 ...
            
            # --- SEÇÃO DE INTELIGÊNCIA (ML) ---
            st.markdown("---")
            st.subheader("🧠 Análise de Inteligência Artificial")
            
            # 1. Classificação em Tempo Real
            status_desc, status_icon = prever_status(ultimo_dado['temperatura'])
            
            col_ml_1, col_ml_2 = st.columns([1, 2])
            
            with col_ml_1:
                st.info(f"Status do Ambiente: **{status_desc}** {status_icon}")
                
            with col_ml_2:
                # 2. Gráfico de Classificação (O Modelo aplicado ao histórico)
                contagem_ml = gerar_relatorio_ml(df)
                st.bar_chart(contagem_ml)
                st.caption("Distribuição de status nas últimas 50 leituras")

            # 3. Mostra a Tabela bruta
            st.subheader("Histórico Recente")
            st.dataframe(df)
            
        else:
            st.warning("Aguardando dados do simulador...")

    # Espera 2 segundos antes de atualizar a tela de novo
    time.sleep(2)