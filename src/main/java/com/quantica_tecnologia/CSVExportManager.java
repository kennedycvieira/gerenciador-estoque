package com.quantica_tecnologia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVExportManager {
    private static final String EXPORT_DIRECTORY = "exports";

    // Criar diretório de exportações se não existir
    private static void ensureExportDirectoryExists() {
        File exportDir = new File(EXPORT_DIRECTORY);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
    }

    // Gerar nome de arquivo de exportação com timestamp
    private static String generateExportFileName(String prefixo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        return EXPORT_DIRECTORY + "/" + prefixo + "_" + timestamp + ".csv";
    }

    // Exportar tabela de produtos para CSV
    public static boolean exportarProdutosParaCSV() {
        ensureExportDirectoryExists();
        String fileName = generateExportFileName("produtos");

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM produtos");
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(fileName)) {

            // Escrever cabeçalho
            csvWriter.append("ID,Identificador,Nome,Quantidade,Unidade\n");

            // Escrever dados
            while (rs.next()) {
                csvWriter.append(String.format("%d,%s,%s,%.2f,%s\n", 
                    rs.getInt("id"),
                    rs.getString("identificador"),
                    rs.getString("nome"),
                    rs.getDouble("quantidade"),
                    rs.getString("unidade")
                ));
            }

            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Listar arquivos CSV exportados
    public static File[] listarExportacoes() {
        File exportDir = new File(EXPORT_DIRECTORY);
        return exportDir.listFiles((dir, name) -> name.endsWith(".csv"));
    }
    public static boolean exportarLogAlteracoesParaCSV() {
        ensureExportDirectoryExists();
        String fileName = generateExportFileName("log_alteracoes");
    
        try (Connection conn = DatabaseManager.connect();
             // Modified query to join with usuarios table and get the username
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT l.id, l.tipo_operacao, l.produto_id, p.identificador, p.nome, l.quantidade, l.usuario, l.timestamp " +
                 "FROM log_alteracoes l " +
                 "LEFT JOIN produtos p ON l.produto_id = p.id");
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(fileName)) {
    
            // Updated header to include product identifier and name
            csvWriter.append("ID,Tipo Operação,Produto ID,Identificador,Nome,Quantidade,Usuário,Timestamp\n");
    
            // Updated data writing to include the new fields
            while (rs.next()) {
                csvWriter.append(String.format("%d,%s,%d,%s,%s,%.2f,%s,%s\n", 
                    rs.getInt("id"),
                    rs.getString("tipo_operacao"),
                    rs.getInt("produto_id"),
                    rs.getString("identificador"),
                    rs.getString("nome"),
                    rs.getDouble("quantidade"),
                    rs.getString("usuario"),
                    rs.getString("timestamp")
                ));
            }
    
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Add to the CSVExportManager.java
public static boolean exportarSaidasProdutoParaCSV() {
    ensureExportDirectoryExists();
    String fileName = generateExportFileName("saidas_produto");

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT s.id, p.identificador, p.nome, s.quantidade, p.unidade, " +
             "po.nome AS portador, d.nome AS destino, s.data_saida " +
             "FROM saida_produto s " +
             "JOIN produtos p ON s.produto_id = p.id " +
             "JOIN portador po ON s.portador_id = po.id " +
             "JOIN destino d ON s.destino_id = d.id " +
             "ORDER BY s.data_saida DESC");
         ResultSet rs = pstmt.executeQuery();
         FileWriter csvWriter = new FileWriter(fileName)) {

        // Write header
        csvWriter.append("ID,Identificador,Nome,Quantidade,Unidade,Portador,Destino,Data Saída\n");

        // Write data
        while (rs.next()) {
            csvWriter.append(String.format("%d,%s,%s,%.2f,%s,%s,%s,%s\n", 
                rs.getInt("id"),
                rs.getString("identificador"),
                rs.getString("nome"),
                rs.getDouble("quantidade"),
                rs.getString("unidade"),
                rs.getString("portador"),
                rs.getString("destino"),
                rs.getString("data_saida")
            ));
        }

        return true;
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        return false;
    }
}

public static boolean exportarPortadoresParaCSV() {
    ensureExportDirectoryExists();
    String fileName = generateExportFileName("portadores");

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM portador");
         ResultSet rs = pstmt.executeQuery();
         FileWriter csvWriter = new FileWriter(fileName)) {

        // Write header
        csvWriter.append("ID,Nome\n");

        // Write data
        while (rs.next()) {
            csvWriter.append(String.format("%d,%s\n", 
                rs.getInt("id"),
                rs.getString("nome")
            ));
        }

        return true;
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        return false;
    }
}

public static boolean exportarDestinosParaCSV() {
    ensureExportDirectoryExists();
    String fileName = generateExportFileName("destinos");

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM destino");
         ResultSet rs = pstmt.executeQuery();
         FileWriter csvWriter = new FileWriter(fileName)) {

        // Write header
        csvWriter.append("ID,Nome\n");

        // Write data
        while (rs.next()) {
            csvWriter.append(String.format("%d,%s\n", 
                rs.getInt("id"),
                rs.getString("nome")
            ));
        }

        return true;
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        return false;
    }
}
}