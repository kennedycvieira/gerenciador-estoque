package com.quantica_tecnologia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupManager {
    private static final String DB_PATH = "estoque.db";
    private static final String BACKUP_DIRECTORY = "backups";

    // Criar diretório de backups se não existir
    private static void ensureBackupDirectoryExists() {
        File backupDir = new File(BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    // Gerar nome de arquivo de backup com timestamp
    private static String generateBackupFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        return BACKUP_DIRECTORY + "/estoque_backup_" + timestamp + ".db";
    }

    // Realizar backup do banco de dados
    public static boolean criarBackup() {
        try {
            // Garantir que o diretório de backups exista
            ensureBackupDirectoryExists();

            // Gerar nome para o arquivo de backup
            String backupFileName = generateBackupFileName();

            // Copiar o arquivo do banco de dados
            Path source = Path.of(DB_PATH);
            Path destination = Path.of(backupFileName);
            
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Restaurar backup
    public static boolean restaurarBackup(String caminhoBackup) {
        try {

            Path source = Path.of(caminhoBackup);
            Path destination = Path.of(DB_PATH);
            
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar backups disponíveis
    public static File[] listarBackups() {
        File backupDir = new File(BACKUP_DIRECTORY);
        return backupDir.listFiles((dir, name) -> name.startsWith("estoque_backup_") && name.endsWith(".db"));
    }
}