package com.quantica_tecnologia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoManager {
    // Método para adicionar produto
    public static boolean adicionarProduto(String usuario, String identificador, String nome, 
                                           double quantidade, String unidade) {
        Connection conn = null;
        try {
            conn = DatabaseManager.connect();
            conn.setAutoCommit(false);

            // Inserir produto
            PreparedStatement pstmtProduto = conn.prepareStatement(
                    "INSERT INTO produtos (identificador, nome, quantidade, unidade) VALUES (?, ?, ?, ?)", 
                    Statement.RETURN_GENERATED_KEYS);
            
            pstmtProduto.setString(1, identificador);
            pstmtProduto.setString(2, nome);
            pstmtProduto.setDouble(3, quantidade);
            pstmtProduto.setString(4, unidade);
            
            pstmtProduto.executeUpdate();

            // Recuperar o ID do produto inserido
            ResultSet rs = pstmtProduto.getGeneratedKeys();
            int produtoId = -1;
            if (rs.next()) {
                produtoId = rs.getInt(1);
            }

            // Inserir log de alteração
            PreparedStatement pstmtLog = conn.prepareStatement(
                    "INSERT INTO log_alteracoes (tipo_operacao, produto_id, quantidade, usuario) VALUES (?, ?, ?, ?)");
            
            pstmtLog.setString(1, "INSERÇÃO");
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

    // Método para remover produto
    public static boolean removerProduto(String usuario, String identificador, double quantidadeRemover) {
        Connection conn = null;
        try {
            conn = DatabaseManager.connect();
            conn.setAutoCommit(false);

            // Primeiro, verificar a quantidade atual do produto
            PreparedStatement pstmtVerificar = conn.prepareStatement(
                    "SELECT id, quantidade FROM produtos WHERE identificador = ?");
            pstmtVerificar.setString(1, identificador);
            ResultSet rs = pstmtVerificar.executeQuery();

            if (!rs.next()) {
                return false; // Produto não encontrado
            }

            int produtoId = rs.getInt("id");
            double quantidadeAtual = rs.getDouble("quantidade");

            if (quantidadeRemover > quantidadeAtual) {
                return false; // Tentativa de remover mais do que existe
            }

            double novaQuantidade = quantidadeAtual - quantidadeRemover;

            // Atualizar quantidade do produto
            PreparedStatement pstmtUpdate = conn.prepareStatement(
                    "UPDATE produtos SET quantidade = ? WHERE id = ?");
            pstmtUpdate.setDouble(1, novaQuantidade);
            pstmtUpdate.setInt(2, produtoId);
            pstmtUpdate.executeUpdate();

            // Inserir log de alteração
            PreparedStatement pstmtLog = conn.prepareStatement(
                    "INSERT INTO log_alteracoes (tipo_operacao, produto_id, quantidade, usuario) VALUES (?, ?, ?, ?)");
            
            pstmtLog.setString(1, "REMOÇÃO");
            pstmtLog.setInt(2, produtoId);
            pstmtLog.setDouble(3, quantidadeRemover);
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

    // Método para buscar produtos (sem alterações)
    public static List<Produto> buscarProdutos(String termo) {
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM produtos WHERE nome LIKE ? OR identificador LIKE ?")) {
            
            String searchTerm = "%" + termo + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto(
                        rs.getString("identificador"),
                        rs.getString("nome"),
                        rs.getDouble("quantidade"),
                        rs.getString("unidade")
                    );
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return produtos;
    }
}