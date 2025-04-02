package com.quantica_tecnologia;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class App extends Application {
    private static final String BACKUP_DIRECTORY = "backups";
    private Stage primaryStage;
    private Stage loginStage;
    private String currentUser;

    private VBox criarTelaBackup() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
    
        // Seção de Backup
        TitledPane backupPane = new TitledPane();
        backupPane.setText("Quântica Tecnologia");
        VBox backupContent = new VBox(10);
    
        // Botão para criar backup
        Button criarBackupButton = new Button("Criar Backup do Banco de Dados");
        criarBackupButton.setOnAction(e -> {
            if (BackupManager.criarBackup()) {
                mostrarAlerta("Backup criado com sucesso!");
            } else {
                mostrarAlerta("Erro ao criar backup");
            }
        });
    
        // Lista de backups existentes
        ListView<String> listaBackups = new ListView<>();
        atualizarListaBackups(listaBackups);
    
        // Botão para restaurar backup
        Button restaurarBackupButton = new Button("Restaurar Backup Selecionado");
        restaurarBackupButton.setOnAction(e -> {
            String backupSelecionado = listaBackups.getSelectionModel().getSelectedItem();
            if (backupSelecionado != null) {
                // Confirmar restauração
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacao.setTitle("Confirmação de Restauração");
                confirmacao.setHeaderText("Tem certeza que deseja restaurar este backup?");
                confirmacao.setContentText("Todos os dados atuais serão substituídos.");
    
                Optional<ButtonType> resultado = confirmacao.showAndWait();
                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    String caminhoBackup = "backups/" + backupSelecionado;
                    if (BackupManager.restaurarBackup(caminhoBackup)) {
                        mostrarAlerta("Backup restaurado com sucesso!");
                        // Reiniciar a aplicação ou fazer logout
                        primaryStage.close();
                        mostrarTelaLogin();
                    } else {
                        mostrarAlerta("Erro ao restaurar backup");
                    }
                }
            } else {
                mostrarAlerta("Selecione um backup para restaurar");
            }
        });
    
        // Botão para atualizar lista de backups
        Button atualizarListaBackupsButton = new Button("Atualizar Lista de Backups");
        atualizarListaBackupsButton.setOnAction(e -> atualizarListaBackups(listaBackups));
    
        backupContent.getChildren().addAll(
            criarBackupButton,
            new Label("Backups Disponíveis:"),
            listaBackups,
            restaurarBackupButton,
            atualizarListaBackupsButton
        );
        backupPane.setContent(backupContent);
    
        // Seção de Exportação CSV
        TitledPane exportPane = new TitledPane();
        exportPane.setText("Exportação de Dados");
        VBox exportContent = new VBox(10);
    
        // Botão para exportar produtos
        Button exportarProdutosButton = new Button("Exportar Estoque para CSV");
        exportarProdutosButton.setOnAction(e -> {
            if (CSVExportManager.exportarProdutosParaCSV()) {
                mostrarAlerta("Estoque exportado com sucesso!");
            } else {
                mostrarAlerta("Erro ao exportar estoque");
            }
        });
    
        // Botão para exportar log de alterações
        Button exportarLogButton = new Button("Exportar Log de Alterações para CSV");
        exportarLogButton.setOnAction(e -> {
            if (CSVExportManager.exportarLogAlteracoesParaCSV()) {
                mostrarAlerta("Log de alterações exportado com sucesso!");
            } else {
                mostrarAlerta("Erro ao exportar log de alterações");
            }
        });
    
        // Lista de exportações
        ListView<String> listaExportacoes = new ListView<>();
        atualizarListaExportacoes(listaExportacoes);
    
        // Botão para atualizar lista de exportações
        Button atualizarListaExportacoesButton = new Button("Atualizar Lista de Exportações");
        atualizarListaExportacoesButton.setOnAction(e -> atualizarListaExportacoes(listaExportacoes));
    
        exportContent.getChildren().addAll(
            exportarProdutosButton,
            exportarLogButton,
            new Label("Exportações Disponíveis:"),
            listaExportacoes,
            atualizarListaExportacoesButton
        );
        exportPane.setContent(exportContent);
    
        // Adicionar seções ao layout principal
        layout.getChildren().addAll(backupPane, exportPane);
    
        return layout;
    }
    
    // Método para atualizar lista de backups
    private void atualizarListaBackups(ListView<String> listaBackups) {
        File[] backups = BackupManager.listarBackups();
        
        // Limpar lista atual
        listaBackups.getItems().clear();
        
        // Adicionar nomes dos backups à lista
        if (backups != null && backups.length > 0) {
            for (File backup : backups) {
                listaBackups.getItems().add(backup.getName());
            }
        } else {
            listaBackups.getItems().add("Nenhum backup encontrado");
        }
    }
    
    // Método para atualizar lista de exportações
    private void atualizarListaExportacoes(ListView<String> listaExportacoes) {
        File[] exportacoes = CSVExportManager.listarExportacoes();
        
        // Limpar lista atual
        listaExportacoes.getItems().clear();
        
        // Adicionar nomes das exportações à lista
        if (exportacoes != null && exportacoes.length > 0) {
            for (File exportacao : exportacoes) {
                listaExportacoes.getItems().add(exportacao.getName());
            }
        } else {
            listaExportacoes.getItems().add("Nenhuma exportação encontrada");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Inicializar banco de dados
        DatabaseManager.initializeDatabase();

        // Mostrar tela de login
        mostrarTelaLogin();
    }

    private void mostrarTelaLogin() {
        loginStage = new Stage();
        loginStage.setTitle("Login -Quântica Tecnologia- Gerenciador de Estoque");

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Registrar");

        loginButton.setOnAction(e -> {
            if (UserManager.authenticateUser(usernameField.getText(), passwordField.getText())) {
                currentUser = usernameField.getText();
                loginStage.close();
                mostrarTelaPrincipal();
            } else {
                mostrarAlerta("Login inválido");
            }
        });

        registerButton.setOnAction(e -> {
            if (UserManager.registerUser(usernameField.getText(), passwordField.getText())) {
                mostrarAlerta("Usuário registrado com sucesso");
            } else {
                mostrarAlerta("Erro ao registrar usuário");
            }
        });

        loginLayout.getChildren().addAll(
                new Label("Usuário:"), usernameField,
                new Label("Senha:"), passwordField,
                loginButton, registerButton);

        Scene scene = new Scene(loginLayout, 300, 400);
        loginStage.setScene(scene);
        loginStage.show();
    }

    private void mostrarTelaPrincipal() {
        primaryStage = new Stage();
        TabPane tabPane = new TabPane();

        // Tab de Adicionar Produto
        Tab adicionarTab = new Tab("Entrada de Produto");
        VBox adicionarLayout = criarTelaAdicaoProduto();
        adicionarTab.setContent(adicionarLayout);

        // Tab de Visualizar Estoque
        Tab visualizarTab = new Tab("Visualizar Estoque");
        VBox visualizarLayout = criarTelaVisualizacaoEstoque();
        visualizarTab.setContent(visualizarLayout);

        // Tab de Remover Produto
        Tab removerTab = new Tab("Saida de Produto");
        VBox removerLayout = criarTelaRemocaoProduto();
        removerTab.setContent(removerLayout);

        Tab backupTab = new Tab("Backup");
        VBox backupLayout = criarTelaBackup();
        backupTab.setContent(backupLayout);

        tabPane.getTabs().addAll(adicionarTab, visualizarTab, removerTab, backupTab);

        Scene scene = new Scene(tabPane, 800, 700);
        primaryStage.setTitle("Quântica Tecnologia - Gerenciador de Estoque - Usuario: " + currentUser);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private VBox criarTelaVisualizacaoEstoque() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField pesquisaField = new TextField();
        pesquisaField.setPromptText("Pesquisar produto");

        TableView<Produto> tabelaEstoque = new TableView<>();
        TableColumn<Produto, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));

        TableColumn<Produto, String> identificadorColumn = new TableColumn<>("Identificador");
        identificadorColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdentificador()));

        TableColumn<Produto, String> quantidadeColumn = new TableColumn<>("Quantidade");
        quantidadeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getQuantidade() + " " + cellData.getValue().getUnidade()));

        tabelaEstoque.getColumns().addAll(nomeColumn, identificadorColumn, quantidadeColumn);

        Button pesquisarButton = new Button("Pesquisar");
        pesquisarButton.setOnAction(e -> {
            List<Produto> produtos = ProdutoManager.buscarProdutos(pesquisaField.getText());
            tabelaEstoque.getItems().setAll(produtos);
        });
        Button atualizarButton = new Button("Atualizar");
        atualizarButton.setOnAction(e -> {
            List<Produto> produtos = ProdutoManager.buscarProdutos("");
            tabelaEstoque.getItems().setAll(produtos);
        });
        TilePane a = new TilePane();
        a.getChildren().addAll(pesquisarButton, atualizarButton);

        layout.getChildren().addAll(
                new Label("Pesquisar Produtos:"), pesquisaField,
               a,
                tabelaEstoque);

        return layout;
    }

    private VBox criarTelaAdicaoProduto() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do Produto");

        TextField identificadorField = new TextField();
        identificadorField.setPromptText("Identificador do Produto");

        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade");

        ComboBox<String> unidadeComboBox = new ComboBox<>();
        unidadeComboBox.getItems().addAll("Litros","Caixa","Fardo", "Kg", "Unidade");
        unidadeComboBox.setPromptText("Volume");

        Button adicionarButton = new Button("Adicionar Produto");
        adicionarButton.setOnAction(e -> {
            try {
                double quantidade = Double.parseDouble(quantidadeField.getText());
                String unidade = unidadeComboBox.getValue();

                if (ProdutoManager.adicionarProduto(
                        currentUser,
                        identificadorField.getText(),
                        nomeField.getText(),
                        quantidade,
                        unidade)) {
                    mostrarAlerta("Produto adicionado com sucesso!");
                    nomeField.clear();
                    identificadorField.clear();
                    quantidadeField.clear();
                    unidadeComboBox.setValue(null);
                } else {
                    mostrarAlerta("Erro ao adicionar produto");
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Quantidade inválida");
            }
        });

        layout.getChildren().addAll(
                new Label("Nome do Produto:"), nomeField,
                new Label("Identificador:"), identificadorField,
                new Label("Quantidade:"), quantidadeField,
                new Label("Unidade:"), unidadeComboBox,
                adicionarButton);

        return layout;
    }

    private VBox criarTelaRemocaoProduto() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField identificadorField = new TextField();
        identificadorField.setPromptText("Identificador do Produto");

        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade a Remover");

        Button removerButton = new Button("Remover Produto");
        removerButton.setOnAction(e -> {
            try {
                double quantidadeRemover = Double.parseDouble(quantidadeField.getText());

                if (ProdutoManager.removerProduto(
                        currentUser,
                        identificadorField.getText(),
                        quantidadeRemover)) {
                    mostrarAlerta("Produto removido com sucesso!");
                    identificadorField.clear();
                    quantidadeField.clear();
                } else {
                    mostrarAlerta("Erro ao remover produto. Verifique o identificador e a quantidade.");
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Quantidade inválida");
            }
        });

        layout.getChildren().addAll(
                new Label("Identificador do Produto:"),
                identificadorField,
                new Label("Quantidade a Remover:"),
                quantidadeField,
                removerButton);

        return layout;
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}