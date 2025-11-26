import sqlite3
import time
import random
from datetime import datetime
import os

# Caminho do banco de dados (certifique-se que a pasta 'database' existe)
DB_PATH = 'database/dados_totem.db'

def criar_tabela():
    import os  # <--- Adicione isso lá no topo junto com os outros imports!

def criar_tabela():
    """
    Cria a tabela logs_totem se ela não existir.
    """
    print("--- Configuração Inicial ---")
    
    # 1. Garante que a pasta 'database' existe
    # Se não existir, o Python cria. Isso evita erro de "Diretório não encontrado".
    pasta_banco = os.path.dirname(DB_PATH)
    os.makedirs(pasta_banco, exist_ok=True)
    
    # 2. Conecta ao banco (cria o arquivo .db se não existir)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor() # O cursor é quem "executa" os comandos
    
    # 3. Define a estrutura da tabela (O SQL da Giovanna entra aqui)
    # id: Identificador único automático
    # temperatura: Número real (float)
    # presenca: Inteiro (0 ou 1)
    # data_hora: Texto (SQLite guarda datas como texto)
    sql_query = """
    CREATE TABLE IF NOT EXISTS logs_totem (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        temperatura REAL,
        presenca INTEGER,
        data_hora TEXT
    )
    """
    
    # 4. Executa o comando no banco
    cursor.execute(sql_query)
    
    # 5. Salva (Commit) e Fecha a conexão
    conn.commit()
    conn.close()
    
    print(f"Banco verificado em: {DB_PATH}")
    print("Tabela 'logs_totem' pronta para uso.")

    print("Verificando tabela...")
    # TODO: Conectar ao banco
    # TODO: Executar SQL "CREATE TABLE IF NOT EXISTS..."
    # TODO: Fechar conexão

def gerar_dados_sensor():
    """
    Simula a leitura do hardware (ESP32).
    """
    # Gera temperatura entre 20.0 e 32.0 graus, arredondado para 2 casas decimais
    temperatura = round(random.uniform(20.0, 32.0), 2)
    
    # Escolhe aleatoriamente entre 0 (vazio) e 1 (pessoa detectada)
    presenca = random.choice([0, 1])
    
    # Pega a hora atual formatada como string (padrão SQL: AAAA-MM-DD HH:MM:SS)
    data_hora = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    return temperatura, presenca, data_hora

def salvar_no_banco(temperatura, presenca, data_hora):
    """
    Recebe os dados e faz o INSERT no SQLite.
    """
    try:
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        
        # Query SQL Segura (usando ? para passar os valores)
        sql_insert = "INSERT INTO logs_totem (temperatura, presenca, data_hora) VALUES (?, ?, ?)"
        
        cursor.execute(sql_insert, (temperatura, presenca, data_hora))
        
        conn.commit() # Salva de verdade
        conn.close()  # Fecha a porta para não travar o banco
        
        print(f"[SALVO] {data_hora} | Temp: {temperatura}°C | Presença: {presenca}")
        
    except Exception as e:
        print(f"Erro ao salvar no banco: {e}")


# --- O LOOP PRINCIPAL (O Coração do Script) ---
if __name__ == "__main__":
    print("Iniciando Simulador do Totem...")
    
    # 1. Garante que a tabela existe antes de começar
    criar_tabela()
    
    # 2. Loop Infinito
    while True:
        try:
            # A. Gerar dados (Descomentado!)
            dados = gerar_dados_sensor()
            
            # Separa os dados retornados em variáveis (Desempacotamento)
            temp, pres, hora = dados
            
            # B. Salvar dados (Descomentado!)
            salvar_no_banco(temp, pres, hora)
            
            # C. Esperar um pouco antes da próxima leitura (2 segundos)
            time.sleep(2) 
            
        except KeyboardInterrupt:
            print("\nSimulador parou!")
            break
        except Exception as e:
            print(f"Erro no loop: {e}")