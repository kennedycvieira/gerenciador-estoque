package com.quantica_tecnologia;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class App extends Application {
    private static final String BACKUP_DIRECTORY = "backups";
    private Stage primaryStage;
    private Stage loginStage;
    private String currentUser;

    // Add this method to your App class
    private Node createLogo() {
        // Create an ImageView for the logo
        ImageView logoImageView = new ImageView();

        try {
            // Load the image - replace "logo.png" with your actual logo file
            // Make sure the logo file is in your project resources
            // Image logoImage = new Image(getClass().getResourceAsStream("logo.JPEG"));
            Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.JPEG")));

            logoImageView.setImage(logoImage);

            // Set a fixed size for the logo
            logoImageView.setFitWidth(150);
            logoImageView.setFitHeight(100);
            logoImageView.setPreserveRatio(true);
        } catch (Exception e) {

            System.err.println("Could not load logo image: " + e.getMessage());
            // Fallback if image fails to load - use a Text node instead
            Text fallbackText = new Text("Quântica Tecnologia");
            fallbackText.setStyle("-fx-font-weight: bold;");
            return fallbackText;
        }

        // Create a container with right-bottom alignment
        StackPane logoContainer = new StackPane(logoImageView);
        StackPane.setAlignment(logoImageView, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(logoImageView, new Insets(0, 10, 10, 0)); // Add some margin

        return logoContainer;
    }

    private VBox criarTelaAdicaoProduto() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Create a horizontal box for the search and buttons
        HBox searchBox = new HBox(10);
        TextField buscaIdentificadorField = new TextField();
        buscaIdentificadorField.setPromptText("Buscar por Identificador");
        Button buscarButton = new Button("Buscar");
        Button limparButton = new Button("Limpar Formulário");
        searchBox.getChildren().addAll(buscaIdentificadorField, buscarButton, limparButton);

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do Produto");

        TextField identificadorField = new TextField();
        identificadorField.setPromptText("Identificador do Produto");

        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade");

        ComboBox<String> unidadeComboBox = new ComboBox<>();
        unidadeComboBox.getItems().addAll("Litros", "Caixa", "Fardo", "Kg", "Unidade");
        unidadeComboBox.setPromptText("Volume");

        // Label to show the current product status
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: blue;");

        // Variable to track if we're updating an existing product
        final boolean[] isUpdating = { false };
        nomeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // When focus is lost from nomeField
            if (!newValue) {
                // If identificador is empty, fill it with nome without spaces
                if (identificadorField.getText().trim().isEmpty() && !nomeField.getText().trim().isEmpty()) {
                    String nomeWithoutSpaces = nomeField.getText().trim().replaceAll("\\s+", "");
                    identificadorField.setText(nomeWithoutSpaces);
                }
            }
        });
        // Search button action
        buscarButton.setOnAction(e -> {
            String identificador = buscaIdentificadorField.getText().trim();
            if (!identificador.isEmpty()) {
                Produto produto = ProdutoManager.buscarProdutoPorIdentificador(identificador);
                if (produto != null) {
                    // Product found, populate fields
                    identificadorField.setText(produto.getIdentificador());
                    nomeField.setText(produto.getNome());
                    quantidadeField.setText(String.valueOf(produto.getQuantidade()));
                    unidadeComboBox.setValue(produto.getUnidade());
                    statusLabel.setText("Produto encontrado. Atualize a quantidade e clique em 'Atualizar Produto'.");
                    isUpdating[0] = true;
                    // Make identifier and name read-only
                    identificadorField.setEditable(false);
                    nomeField.setEditable(false);
                    unidadeComboBox.setDisable(true);
                } else {
                    // Product not found
                    statusLabel.setText("Produto não encontrado. Preencha os campos para adicionar um novo produto.");
                    identificadorField.setText(identificador);
                    nomeField.setText("");
                    quantidadeField.setText("");
                    unidadeComboBox.setValue(null);
                    // Make identifier and name editable
                    identificadorField.setEditable(true);
                    nomeField.setEditable(true);
                    unidadeComboBox.setDisable(false);
                    isUpdating[0] = false;
                }
            }
        });

        // Clear form button action
        limparButton.setOnAction(e -> {
            buscaIdentificadorField.clear();
            nomeField.clear();
            identificadorField.clear();
            quantidadeField.clear();
            unidadeComboBox.setValue(null);
            statusLabel.setText("");
            isUpdating[0] = false;
            identificadorField.setEditable(true);
            nomeField.setEditable(true);
            unidadeComboBox.setDisable(false);
        });

        // Create buttons for add and update
        Button adicionarButton = new Button("Adicionar Produto");
        Button atualizarButton = new Button("Atualizar Produto");

        // Action for Add button
        adicionarButton.setOnAction(e -> {
            try {
                // If identificador is empty, fill it with nome without spaces
                if (identificadorField.getText().trim().isEmpty() && !nomeField.getText().trim().isEmpty()) {
                    String nomeWithoutSpaces = nomeField.getText().trim().replaceAll("\\s+", "");
                    identificadorField.setText(nomeWithoutSpaces);
                }

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
                    statusLabel.setText("");
                    isUpdating[0] = false;
                    identificadorField.setEditable(true);
                    nomeField.setEditable(true);
                    unidadeComboBox.setDisable(false);
                } else {
                    mostrarAlerta("Erro ao adicionar produto");
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Quantidade inválida");
            }
        });

        // Action for Update button
        atualizarButton.setOnAction(e -> {
            try {
                double novaQuantidade = Double.parseDouble(quantidadeField.getText());

                if (ProdutoManager.atualizarQuantidadeProduto(
                        currentUser,
                        identificadorField.getText(),
                        novaQuantidade)) {
                    mostrarAlerta("Quantidade atualizada com sucesso!");
                    buscaIdentificadorField.clear();
                    nomeField.clear();
                    identificadorField.clear();
                    quantidadeField.clear();
                    unidadeComboBox.setValue(null);
                    statusLabel.setText("");
                    isUpdating[0] = false;
                    identificadorField.setEditable(true);
                    nomeField.setEditable(true);
                    unidadeComboBox.setDisable(false);
                } else {
                    mostrarAlerta("Erro ao atualizar produto");
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Quantidade inválida");
            }
        });

        // Use a HBox to place the buttons side by side
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(adicionarButton, atualizarButton);

        layout.getChildren().addAll(
                new Label("Buscar Produto Existente:"), searchBox,
                statusLabel,
                new Label("Nome do Produto:"), nomeField,
                new Label("Identificador:"), identificadorField,
                new Label("Quantidade:"), quantidadeField,
                new Label("Unidade:"), unidadeComboBox,
                buttonBox);

        return layout;
    }

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
                atualizarListaBackupsButton);
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
                atualizarListaExportacoesButton);
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
        
        // Set application icon
        try {
            Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.JPEG")));
            //Image appIcon = new Image(getClass().getResourceAsStream("/app-icon.png"));
            loginStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Failed to load application icon: " + e.getMessage());
        }
        
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.TOP_CENTER); // Center align all elements
        
        // Create logo for login screen
        ImageView logoImageView = new ImageView();
        try {
            Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.JPEG")));
            //Image logoImage = new Image(getClass().getResourceAsStream("/logo.png"));
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(150); // Set appropriate size
            logoImageView.setFitHeight(75);
            logoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
            // Fallback to text if image fails to load
            Text fallbackText = new Text("Quântica Tecnologia");
            fallbackText.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
            loginLayout.getChildren().add(fallbackText);
        }
        
        // Add some spacing after the logo
        Region spacer = new Region();
        spacer.setPrefHeight(20); // 20 pixels of space after logo
        
        // Add logo and spacer to layout (if logo loaded successfully)
        if (logoImageView.getImage() != null) {
            loginLayout.getChildren().addAll(logoImageView, spacer);
        }
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        usernameField.setMaxWidth(250); // Limit width for better appearance
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        passwordField.setMaxWidth(250);
        
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Registrar");
        
        // Create an HBox for buttons to place them side by side
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);
        
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
                buttonBox);
    
        Scene scene = new Scene(loginLayout, 300, 400);
        loginStage.setScene(scene);
        loginStage.show();
    }
   

    private void mostrarTelaPrincipal() {
        primaryStage = new Stage();
        TabPane tabPane = new TabPane();
  
        try {
            //primaryStage.getIcons().add(appIcon);
            primaryStage.getIcons().addAll(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo-16.JPEG"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo-32.JPEG"))),
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo-64.JPEG"))));
        } catch (Exception e) {
            System.err.println("Failed to load application icon: " + e.getMessage());
        }

        // Create the logo once
        Node logo = createLogo();

    
        // Tab de Adicionar Produto
        Tab adicionarTab = new Tab("Entrada de Produto");
        VBox adicionarLayout = criarTelaAdicaoProduto();
        // Instead of StackPane, use BorderPane to position logo at bottom right
        BorderPane adicionarBorderPane = new BorderPane();
        adicionarBorderPane.setCenter(adicionarLayout);
        adicionarBorderPane.setBottom(logo);
        BorderPane.setAlignment(logo, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logo, new Insets(0, 10, 10, 0));
        adicionarTab.setContent(adicionarBorderPane);

        // Tab de Visualizar Estoque
        Tab visualizarTab = new Tab("Visualizar Estoque");
        VBox visualizarLayout = criarTelaVisualizacaoEstoque();
        BorderPane visualizarBorderPane = new BorderPane();
        visualizarBorderPane.setCenter(visualizarLayout);
        // Clone the logo for each tab
        Node logoClone1 = createLogo();
        visualizarBorderPane.setBottom(logoClone1);
        BorderPane.setAlignment(logoClone1, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoClone1, new Insets(0, 10, 10, 0));
        visualizarTab.setContent(visualizarBorderPane);

        // Tab de Remover Produto
        Tab removerTab = new Tab("Saida de Produto");
        VBox removerLayout = criarTelaRemocaoProduto();
        BorderPane removerBorderPane = new BorderPane();
        removerBorderPane.setCenter(removerLayout);
        Node logoClone2 = createLogo();
        removerBorderPane.setBottom(logoClone2);
        BorderPane.setAlignment(logoClone2, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoClone2, new Insets(0, 10, 10, 0));
        removerTab.setContent(removerBorderPane);

        // Tab de Backup
        Tab backupTab = new Tab("Backup");
        VBox backupLayout = criarTelaBackup();
        BorderPane backupBorderPane = new BorderPane();
        backupBorderPane.setCenter(backupLayout);
        Node logoClone3 = createLogo();
        backupBorderPane.setBottom(logoClone3);
        BorderPane.setAlignment(logoClone3, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoClone3, new Insets(0, 10, 10, 0));
        backupTab.setContent(backupBorderPane);

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
        nomeColumn.setPrefWidth(200);

        TableColumn<Produto, String> identificadorColumn = new TableColumn<>("Identificador");
        identificadorColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdentificador()));
        identificadorColumn.setPrefWidth(150);

        TableColumn<Produto, String> quantidadeColumn = new TableColumn<>("Quantidade");
        quantidadeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f %s", cellData.getValue().getQuantidade(), cellData.getValue().getUnidade())));
        quantidadeColumn.setPrefWidth(150);

        tabelaEstoque.getColumns().addAll(nomeColumn, identificadorColumn, quantidadeColumn);
        tabelaEstoque.setPrefHeight(400);

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

        // Add print buttons
        Button imprimirListaButton = new Button("Imprimir Lista");
        imprimirListaButton.setOnAction(e -> {
            if (!PrintManager.printProdutos(tabelaEstoque.getItems(), pesquisaField.getText())) {
                mostrarAlerta("Impressão cancelada ou erro ao imprimir");
            }
        });

        Button imprimirDetalhadoButton = new Button("Imprimir Produto Selecionado");
        imprimirDetalhadoButton.setOnAction(e -> {
            Produto produtoSelecionado = tabelaEstoque.getSelectionModel().getSelectedItem();
            if (produtoSelecionado != null) {
                if (!PrintManager.printProdutoDetalhado(produtoSelecionado)) {
                    mostrarAlerta("Impressão cancelada ou erro ao imprimir");
                }
            } else {
                mostrarAlerta("Selecione um produto para imprimir");
            }
        });

        // Create a more organized layout with HBox for buttons
        HBox searchButtonsBox = new HBox(10);
        searchButtonsBox.getChildren().addAll(pesquisarButton, atualizarButton);

        HBox printButtonsBox = new HBox(10);
        printButtonsBox.getChildren().addAll(imprimirListaButton, imprimirDetalhadoButton);

        // Add a separator between search and print sections
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        layout.getChildren().addAll(
                new Label("Pesquisar Produtos:"),
                pesquisaField,
                searchButtonsBox,
                tabelaEstoque,
                separator,
                new Label("Opções de Impressão:"),
                printButtonsBox);

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

    /*
    private VBox criarTelaRemocaoProduto() {
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));

    TextField identificadorField = new TextField();
    identificadorField.setPromptText("Identificador do Produto");

    TextField quantidadeField = new TextField();
    quantidadeField.setPromptText("Quantidade a Remover");
    
    // Create ComboBox for destinos
    ComboBox<Destino> destinoComboBox = new ComboBox<>();
    destinoComboBox.setPromptText("Selecione o Destino");
    destinoComboBox.setPrefWidth(200);
    
    // Create ComboBox for portadores
    ComboBox<Portador> portadorComboBox = new ComboBox<>();
    portadorComboBox.setPromptText("Selecione o Portador");
    portadorComboBox.setPrefWidth(200);
    
    // Populate the ComboBoxes
    List<Destino> destinos = DestinoManager.listarDestinos();
    destinoComboBox.getItems().addAll(destinos);
    
    List<Portador> portadores = PortadorManager.listarPortadores();
    portadorComboBox.getItems().addAll(portadores);
    
    // Add refresh buttons
    Button refreshDestinosButton = new Button("Atualizar Destinos");
    refreshDestinosButton.setOnAction(e -> {
        destinoComboBox.getItems().clear();
        destinoComboBox.getItems().addAll(DestinoManager.listarDestinos());
    });
    
    Button refreshPortadoresButton = new Button("Atualizar Portadores");
    refreshPortadoresButton.setOnAction(e -> {
        portadorComboBox.getItems().clear();
        portadorComboBox.getItems().addAll(PortadorManager.listarPortadores());
    });
    
    // Create HBoxes for ComboBoxes with refresh buttons
    HBox destinoBox = new HBox(10);
    destinoBox.getChildren().addAll(destinoComboBox, refreshDestinosButton);
    
    HBox portadorBox = new HBox(10);
    portadorBox.getChildren().addAll(portadorComboBox, refreshPortadoresButton);

    Button removerButton = new Button("Remover Produto");
    removerButton.setOnAction(e -> {
        try {
            double quantidadeRemover = Double.parseDouble(quantidadeField.getText());
            Destino destinoSelecionado = destinoComboBox.getValue();
            Portador portadorSelecionado = portadorComboBox.getValue();
            
            if (destinoSelecionado == null || portadorSelecionado == null) {
                mostrarAlerta("Selecione um destino e um portador");
                return;
            }

            // Assuming you need to modify ProdutoManager.removerProduto to accept destino and portador
            // If that's not implemented yet, you'll need to update that method too
            if (ProdutoManager.removerProduto(
                    currentUser,
                    identificadorField.getText(),
                    quantidadeRemover,
                    destinoSelecionado.getId(),
                    portadorSelecionado.getId())) {
                mostrarAlerta("Produto removido com sucesso!");
                identificadorField.clear();
                quantidadeField.clear();
                destinoComboBox.setValue(null);
                portadorComboBox.setValue(null);
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
            new Label("Destino:"),
            destinoBox,
            new Label("Portador:"),
            portadorBox,
            removerButton);

    return layout;
} */

    private VBox criarTelaGerenciamentoPortadoresDestinos() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        // Portadores section
        TitledPane portadorPane = new TitledPane();
        portadorPane.setText("Gerenciar Portadores");
        VBox portadorContent = new VBox(10);
        
        TextField portadorNomeField = new TextField();
        portadorNomeField.setPromptText("Nome do Portador");
        
        Button adicionarPortadorButton = new Button("Adicionar Portador");
        adicionarPortadorButton.setOnAction(e -> {
            String nome = portadorNomeField.getText().trim();
            if (!nome.isEmpty()) {
                if (PortadorManager.adicionarPortador(nome)) {
                    mostrarAlerta("Portador adicionado com sucesso!");
                    portadorNomeField.clear();
                    atualizarListaPortadores(); // You'll need to implement this
                } else {
                    mostrarAlerta("Erro ao adicionar portador");
                }
            } else {
                mostrarAlerta("Informe o nome do portador");
            }
        });
        
        ListView<Portador> listaPortadores = new ListView<>();
        listaPortadores.setPrefHeight(200);
        
        // Function to update the portadores list
        Button atualizarPortadoresButton = new Button("Atualizar Lista");
        atualizarPortadoresButton.setOnAction(e -> {
            List<Portador> portadores = PortadorManager.listarPortadores();
            listaPortadores.getItems().setAll(portadores);
        });
        
        HBox portadorButtonBox = new HBox(10);
        portadorButtonBox.getChildren().addAll(adicionarPortadorButton, atualizarPortadoresButton);
        
        portadorContent.getChildren().addAll(
                new Label("Nome do Portador:"),
                portadorNomeField,
                portadorButtonBox,
                new Label("Portadores Cadastrados:"),
                listaPortadores);
        portadorPane.setContent(portadorContent);
        
        // Destinos section
        TitledPane destinoPane = new TitledPane();
        destinoPane.setText("Gerenciar Destinos");
        VBox destinoContent = new VBox(10);
        
        TextField destinoNomeField = new TextField();
        destinoNomeField.setPromptText("Nome do Destino");
        
        Button adicionarDestinoButton = new Button("Adicionar Destino");
        adicionarDestinoButton.setOnAction(e -> {
            String nome = destinoNomeField.getText().trim();
            if (!nome.isEmpty()) {
                if (DestinoManager.adicionarDestino(nome)) {
                    mostrarAlerta("Destino adicionado com sucesso!");
                    destinoNomeField.clear();
                    atualizarListaDestinos(); // You'll need to implement this
                } else {
                    mostrarAlerta("Erro ao adicionar destino");
                }
            } else {
                mostrarAlerta("Informe o nome do destino");
            }
        });
        
        ListView<Destino> listaDestinos = new ListView<>();
        listaDestinos.setPrefHeight(200);
        
        // Function to update the destinos list
        Button atualizarDestinosButton = new Button("Atualizar Lista");
        atualizarDestinosButton.setOnAction(e -> {
            List<Destino> destinos = DestinoManager.listarDestinos();
            listaDestinos.getItems().setAll(destinos);
        });
        
        HBox destinoButtonBox = new HBox(10);
        destinoButtonBox.getChildren().addAll(adicionarDestinoButton, atualizarDestinosButton);
        
        destinoContent.getChildren().addAll(
                new Label("Nome do Destino:"),
                destinoNomeField,
                destinoButtonBox,
                new Label("Destinos Cadastrados:"),
                listaDestinos);
        destinoPane.setContent(destinoContent);
        
        // Add both sections to main layout
        layout.getChildren().addAll(portadorPane, destinoPane);
        
        // Initialize lists
        atualizarPortadoresButton.fire();
        atualizarDestinosButton.fire();
        
        return layout;
    }
    
    // Helper method to update portadores list
    private void atualizarListaPortadores() {
        // Find the ListView in the scene
        Scene currentScene = primaryStage.getScene();
        if (currentScene != null) {
            TabPane tabPane = (TabPane) currentScene.getRoot();
            Tab cadastrosTab = tabPane.getTabs().stream()
                    .filter(tab -> tab.getText().equals("Cadastros"))
                    .findFirst().orElse(null);
            
            if (cadastrosTab != null) {
                BorderPane borderPane = (BorderPane) cadastrosTab.getContent();
                VBox mainLayout = (VBox) borderPane.getCenter();
                TitledPane portadorPane = (TitledPane) mainLayout.getChildren().get(0);
                VBox portadorContent = (VBox) portadorPane.getContent();
                
                // Find the ListView
                for (Node node : portadorContent.getChildren()) {
                    if (node instanceof ListView) {
                        @SuppressWarnings("unchecked")
                        ListView<Portador> listaPortadores = (ListView<Portador>) node;
                        List<Portador> portadores = PortadorManager.listarPortadores();
                        listaPortadores.getItems().setAll(portadores);
                        break;
                    }
                }
            }
        }
    }
    
    // Helper method to update destinos list
    private void atualizarListaDestinos() {
        // Find the ListView in the scene
        Scene currentScene = primaryStage.getScene();
        if (currentScene != null) {
            TabPane tabPane = (TabPane) currentScene.getRoot();
            Tab cadastrosTab = tabPane.getTabs().stream()
                    .filter(tab -> tab.getText().equals("Cadastros"))
                    .findFirst().orElse(null);
            
            if (cadastrosTab != null) {
                BorderPane borderPane = (BorderPane) cadastrosTab.getContent();
                VBox mainLayout = (VBox) borderPane.getCenter();
                TitledPane destinoPane = (TitledPane) mainLayout.getChildren().get(1);
                VBox destinoContent = (VBox) destinoPane.getContent();
                
                // Find the ListView
                for (Node node : destinoContent.getChildren()) {
                    if (node instanceof ListView) {
                        @SuppressWarnings("unchecked")
                        ListView<Destino> listaDestinos = (ListView<Destino>) node;
                        List<Destino> destinos = DestinoManager.listarDestinos();
                        listaDestinos.getItems().setAll(destinos);
                        break;
                    }
                }
            }
        }
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