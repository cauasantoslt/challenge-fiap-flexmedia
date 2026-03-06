import streamlit as st
import pandas as pd
import sqlite3
import os
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report

st.set_page_config(page_title="Dashboard Totem Flexmedia", layout="wide")
st.title("📊 Dashboard Analítico - Totem Inteligente Flexmedia")

# --- DEBUG: Onde o Streamlit está procurando o banco? ---
BASE_DIR = os.path.abspath(os.path.dirname(__file__))
db_path = os.path.join(BASE_DIR, 'totem_data.db')

st.info(f"🔍 Dedo-duro do Diretório: O Streamlit está lendo o arquivo em:\n{db_path}")

# Removi o @st.cache_data para forçar a leitura em tempo real
def carregar_dados():
    try:
        conn = sqlite3.connect(db_path)
        df = pd.read_sql("SELECT * FROM interacao", conn)
        conn.close()
        
        if not df.empty:
            df['data_hora'] = pd.to_datetime(df['data_hora'])
        return df, None # Retorna o dataframe e nenhum erro
    except Exception as e:
        return pd.DataFrame(), str(e) # Retorna dataframe vazio e o erro real para investigarmos

# Executa a função
df, erro = carregar_dados()

# --- VERIFICAÇÃO DE ERROS ---
if erro:
    st.error(f"🚨 Erro interno ao ler o banco: {erro}")
elif df.empty:
    st.warning("⚠️ O banco existe no caminho acima, mas a tabela está 100% vazia.")
else:
    st.success(f"✅ Sucesso! {len(df)} linhas carregadas do banco.")
    
    # --- ETAPA 2: VISUALIZAÇÃO DE DADOS ---
    st.markdown("### 📡 Fluxo de Dados em Tempo Real")
    st.dataframe(df.tail(5), use_container_width=True)

    col1, col2 = st.columns(2)

    with col1:
        st.markdown("### Distribuição de Distâncias")
        fig, ax = plt.subplots(figsize=(8, 4))
        sns.histplot(df['distancia_cm'], bins=10, kde=True, color='#1f77b4', ax=ax)
        st.pyplot(fig)

    with col2:
        st.markdown("### Volume de Interações")
        df['minuto_interacao'] = df['data_hora'].dt.floor('Min')
        volume_tempo = df.groupby('minuto_interacao').size()
        fig2, ax2 = plt.subplots(figsize=(8, 4))
        volume_tempo.plot(kind='line', marker='o', color='#ff7f0e', ax=ax2)
        st.pyplot(fig2)

    st.markdown("---")

    # --- ETAPA 3: MACHINE LEARNING ---
    st.markdown("### 🤖 Modelo de Machine Learning (Engajamento)")
    
    df['engajamento_alto'] = (df['distancia_cm'] < 30).astype(int)
    X = df[['distancia_cm']] 
    y = df['engajamento_alto']

    if len(y.unique()) > 1:
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
        modelo = RandomForestClassifier(random_state=42)
        modelo.fit(X_train, y_train)
        previsoes = modelo.predict(X_test)
        acuracia = accuracy_score(y_test, previsoes)

        col3, col4 = st.columns(2)
        with col3:
            st.metric(label="Acurácia do Modelo", value=f"{acuracia * 100:.2f}%")
        with col4:
            st.text(classification_report(y_test, previsoes))
    else:
        st.info("Aguardando mais variação de dados para treinar o algoritmo.")