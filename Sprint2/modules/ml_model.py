import pandas as pd

def prever_status(temperatura):
    """
    Simula um modelo de ML treinado.
    Recebe a temperatura e classifica o risco.
    
    Lógica (Simulando uma Árvore de Decisão):
    - Menor que 24°C: "Confortável"
    - Entre 24°C e 28°C: "Atenção"
    - Maior que 28°C: "Crítico" (Ar condicionado falhando?)
    """
    if temperatura < 24.0:
        return "Confortável", "🟢" # Retorna texto e cor/ícone
    elif 24.0 <= temperatura <= 28.0:
        return "Atenção", "🟡"
    else:
        return "Crítico", "🔴"

def gerar_relatorio_ml(df):
    """
    Aplica o modelo em todo o histórico de dados para gerar estatísticas.
    """
    # Aplica a função linha por linha
    df['status_ml'] = df['temperatura'].apply(lambda x: prever_status(x)[0])
    
    # Conta quantos de cada tipo (ex: 10 Confortáveis, 2 Críticos)
    contagem = df['status_ml'].value_counts()
    return contagem