package com.quantica_tecnologia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:estoque.db";

    // Método para criar a conexão com o banco de dados
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Método para inicializar o banco de dados
    public static void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Criar tabela de usuários
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)");
            
            // Criar tabela de produtos
            stmt.execute("CREATE TABLE IF NOT EXISTS produtos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "identificador TEXT UNIQUE NOT NULL," +
                    "nome TEXT NOT NULL," +
                    "quantidade REAL NOT NULL," +
                    "unidade TEXT NOT NULL)");
            
            // Criar tabela de log de alterações
            stmt.execute("CREATE TABLE IF NOT EXISTS log_alteracoes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tipo_operacao TEXT NOT NULL," +
                    "produto_id INTEGER NOT NULL," +
                    "quantidade REAL NOT NULL," +
                    "usuario TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}