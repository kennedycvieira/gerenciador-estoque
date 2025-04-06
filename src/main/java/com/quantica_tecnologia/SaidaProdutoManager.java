package com.quantica_tecnologia;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaidaProdutoManager {
    
    // Method to register a product output
    public static boolean registrarSaidaProduto(String usuario, int produtoId, double quantidade, int portadorId, int destinoId) {
        Connection conn = null;
        try {
            conn = DatabaseManager.connect();
            conn.setAutoCommit(false);
    
            // Verify product exists and get its current quantity
            PreparedStatement pstmtVerificar = conn.prepareStatement(
                    "SELECT quantidade FROM produtos WHERE id = ?");
            pstmtVerificar.setInt(1, produtoId);
            ResultSet rs = pstmtVerificar.executeQuery();
    
            if (!rs.next()) {
                return false; // Product not found
            }
    
            double quantidadeAtual = rs.getDouble("quantidade");
    
            if (quantidade > quantidadeAtual) {
                return false; // Trying to remove more than available
            }
    
            // Update product quantity
            double novaQuantidade = quantidadeAtual - quantidade;
            PreparedStatement pstmtUpdate = conn.prepareStatement(
                    "UPDATE produtos SET quantidade = ? WHERE id = ?");
            pstmtUpdate.setDouble(1, novaQuantidade);
            pstmtUpdate.setInt(2, produtoId);
            pstmtUpdate.executeUpdate();
    
            // Insert into saida_produto
            PreparedStatement pstmtSaida = conn.prepareStatement(
                    "INSERT INTO saida_produto (produto_id, portador_id, destino_id, quantidade) VALUES (?, ?, ?, ?)");
            pstmtSaida.setInt(1, produtoId);
            pstmtSaida.setInt(2, portadorId);
            pstmtSaida.setInt(3, destinoId);
            pstmtSaida.setDouble(4, quantidade);
            pstmtSaida.executeUpdate();
    
            // Insert log entry
            PreparedStatement pstmtLog = conn.prepareStatement(
                    "INSERT INTO log_alteracoes (tipo_operacao, produto_id, quantidade, usuario) VALUES (?, ?, ?, ?)");
            pstmtLog.setString(1, "SAÍDA");
            pstmtLog.setInt(2, produtoId);
            pstmtLog.setDouble(3, quantidade);
            pstmtLog.setString(4, usuario);
            pstmtLog.executeUpdate();
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Method to get output records with product, portador and destino info
    public static List<SaidaProduto> listarSaidasCompletas() {
        List<SaidaProduto> saidas = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT s.id, s.produto_id, p.id, p.nome, s.quantidade, p.unidade, " +
                     "s.portador_id, po.nome AS portador_nome, s.destino_id, d.nome AS destino_nome, " +
                     "s.data_saida " +
                     "FROM saida_produto s " +
                     "JOIN produtos p ON s.produto_id = p.id " +
                     "JOIN portador po ON s.portador_id = po.id " +
                     "JOIN destino d ON s.destino_id = d.id " +
                     "ORDER BY s.data_saida DESC")) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SaidaProduto saida = new SaidaProduto(
                        rs.getInt("id"),
                        rs.getInt("produto_id"),
                        rs.getString("nome"),
                        rs.getDouble("quantidade"),
                        rs.getString("unidade"),
                        rs.getInt("portador_id"),
                        rs.getString("portador_nome"),
                        rs.getInt("destino_id"),
                        rs.getString("destino_nome"),
                        rs.getTimestamp("data_saida")
                    );
                    saidas.add(saida);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return saidas;
    }
    /*
    // Inner class to contain the complete output information
    public static class SaidaProdutoInfo {
        private int id;
        private int produtoId;
        private String produtoNome;
        private double quantidade;
        private String unidade;
        private int portadorId;
        private String portadorNome;
        private int destinoId;
        private String destinoNome;
        private Date dataSaida;
        
        public SaidaProdutoInfo(int id, int produtoId, String produtoNome, 
                               double quantidade, String unidade, int portadorId, String portadorNome, 
                               int destinoId, String destinoNome, Date dataSaida) {
            this.id = id;
            this.produtoId = produtoId;
            this.produtoNome = produtoNome;
            this.quantidade = quantidade;
            this.unidade = unidade;
            this.portadorId = portadorId;
            this.portadorNome = portadorNome;
            this.destinoId = destinoId;
            this.destinoNome = destinoNome;
            this.dataSaida = dataSaida;
        }
        
        // Getters
        public int getId() { return id; }
        public int getProdutoId() { return produtoId; }
        public String getProdutoNome() { return produtoNome; }
        public double getQuantidade() { return quantidade; }
        public String getUnidade() { return unidade; }
        public int getPortadorId() { return portadorId; }
        public String getPortadorNome() { return portadorNome; }
        public int getDestinoId() { return destinoId; }
        public String getDestinoNome() { return destinoNome; }
        public Date getDataSaida() { return dataSaida; }
    }
        */
}
