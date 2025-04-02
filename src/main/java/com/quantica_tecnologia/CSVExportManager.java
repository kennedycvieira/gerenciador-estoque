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

    // Exportar tabela de log de alterações para CSV
    public static boolean exportarLogAlteracoesParaCSV() {
        ensureExportDirectoryExists();
        String fileName = generateExportFileName("log_alteracoes");

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM log_alteracoes");
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(fileName)) {

            // Escrever cabeçalho
            csvWriter.append("ID,Tipo Operação,Produto ID,Quantidade,Usuário,Timestamp\n");

            // Escrever dados
            while (rs.next()) {
                csvWriter.append(String.format("%d,%s,%d,%.2f,%s,%s\n", 
                    rs.getInt("id"),
                    rs.getString("tipo_operacao"),
                    rs.getInt("produto_id"),
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

    // Listar arquivos CSV exportados
    public static File[] listarExportacoes() {
        File exportDir = new File(EXPORT_DIRECTORY);
        return exportDir.listFiles((dir, name) -> name.endsWith(".csv"));
    }
}