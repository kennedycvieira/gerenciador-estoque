package com.quantica_tecnologia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinoManager {
    
    // Method to add a new destino
    public static boolean adicionarDestino(String nome) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO destino (nome) VALUES (?)")) {
            
            pstmt.setString(1, nome);
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to get all destinos
    public static List<Destino> listarDestinos() {
        List<Destino> destinos = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM destino ORDER BY nome")) {
            
            while (rs.next()) {
                Destino destino = new Destino(
                    rs.getInt("id"),
                    rs.getString("nome")
                );
                destinos.add(destino);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return destinos;
    }
    
    // Method to get a destino by ID
    public static Destino buscarDestinoPorId(int id) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM destino WHERE id = ?")) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Destino(
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
    
    // Method to search destinos by name
    public static List<Destino> buscarDestinosPorNome(String termo) {
        List<Destino> destinos = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM destino WHERE nome LIKE ? ORDER BY nome")) {
            
            pstmt.setString(1, "%" + termo + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Destino destino = new Destino(
                        rs.getInt("id"),
                        rs.getString("nome")
                    );
                    destinos.add(destino);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return destinos;
    }
}