package com.quantica_tecnologia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortadorManager {
    
    // Method to add a new portador
    public static boolean adicionarPortador(String nome) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO portador (nome) VALUES (?)")) {
            
            pstmt.setString(1, nome);
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to get all portadores
    public static List<Portador> listarPortadores() {
        List<Portador> portadores = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM portador ORDER BY nome")) {
            
            while (rs.next()) {
                Portador portador = new Portador(
                    rs.getInt("id"),
                    rs.getString("nome")
                );
                portadores.add(portador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return portadores;
    }
    
    // Method to get a portador by ID
    public static Portador buscarPortadorPorId(int id) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM portador WHERE id = ?")) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Portador(
                        rs.getInt("id"),
                        rs.getString("nome")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Method to search portadores by name
    public static List<Portador> buscarPortadoresPorNome(String termo) {
        List<Portador> portadores = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM portador WHERE nome LIKE ? ORDER BY nome")) {
            
            pstmt.setString(1, "%" + termo + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Portador portador = new Portador(
                        rs.getInt("id"),
                        rs.getString("nome")
                    );
                    portadores.add(portador);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return portadores;
    }
}