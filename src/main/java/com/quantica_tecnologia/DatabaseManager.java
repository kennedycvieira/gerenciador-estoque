package com.quantica_tecnologia;
import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:estoque.db";

    // Método para criar a conexão com o banco de dados
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Método para inicializar o banco de dados
    // In DatabaseManager.java - modify initializeDatabase method
public static void initializeDatabase() {
    try (Connection conn = connect();
         Statement stmt = conn.createStatement()) {
        
        // Create original tables
        stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL)");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "quantidade REAL NOT NULL," +
                "unidade TEXT NOT NULL)");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS log_alteracoes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo_operacao TEXT NOT NULL," +
                "produto_id INTEGER NOT NULL," +
                "quantidade REAL NOT NULL," +
                "usuario TEXT NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
        
        // Create new tables for portador, destino, and saida_produto
        stmt.execute("CREATE TABLE IF NOT EXISTS portador (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL)");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS destino (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL)");
        
        stmt.execute("CREATE TABLE IF NOT EXISTS saida_produto (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "produto_id INTEGER NOT NULL," +
                "portador_id INTEGER NOT NULL," +
                "destino_id INTEGER NOT NULL," +
                "quantidade REAL NOT NULL," +
                "data_saida DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (produto_id) REFERENCES produtos(id)," +
                "FOREIGN KEY (portador_id) REFERENCES portador(id)," +
                "FOREIGN KEY (destino_id) REFERENCES destino(id))");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
