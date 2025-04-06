package com.quantica_tecnologia;

import java.awt.print.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Graphics;
import java.awt.Font;

public class PrintManager {

    // Method to print a list of products
    public static boolean printProdutos(List<Produto> produtos, String searchTerm) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Quântica Tecnologia - Relatório de Estoque");

            // Create a custom printable object
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    int y = 20; // Start position for printing
                    int lineHeight = 15; // Height of each line

                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE; // Only one page for now
                    }

                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                    // Print header
                    graphics.setFont(new Font("Arial", Font.BOLD, 14));
                    graphics.drawString("Quântica Tecnologia - Relatório de Estoque", 50, y);
                    y += lineHeight + 5;

                    // Print date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String dateTime = dateFormat.format(new Date());
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    graphics.drawString("Data/Hora: " + dateTime, 50, y);
                    y += lineHeight;

                    // Print search term if provided
                    if (searchTerm != null && !searchTerm.isEmpty()) {
                        graphics.drawString("Termo de Busca: " + searchTerm, 50, y);
                        y += lineHeight;
                    }

                    graphics.drawString("Total de Itens: " + produtos.size(), 50, y);
                    y += lineHeight + 10;

                    // Print table header
                    graphics.setFont(new Font("Arial", Font.BOLD, 10));
                    graphics.drawString("ID", 50, y);
                    graphics.drawString("Nome", 100, y);
                    graphics.drawString("Quantidade", 300, y);
                    graphics.drawString("Unidade", 400, y);
                    y += lineHeight;

                    // Draw a line below the header
                    graphics.drawLine(50, y - 5, 500, y - 5);

                    // Print items
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    for (Produto produto : produtos) {
                        y += lineHeight;

                        // Check if we need to start a new page
                        if (y > pageFormat.getImageableHeight() - 20) {
                            return PAGE_EXISTS;
                        }

                        // Print product details
                        graphics.drawString(String.valueOf(produto.getId()), 50, y);

                        // Handle long product names with line breaks
                        String nome = produto.getNome();
                        if (nome.length() > 30) {
                            String[] lines = splitTextIntoLines(nome, 30);
                            for (String line : lines) {
                                graphics.drawString(line, 100, y);
                                y += lineHeight;
                            }
                            y -= lineHeight; // Adjust for extra line height added
                        } else {
                            graphics.drawString(nome, 100, y);
                        }

                        graphics.drawString(String.format("%.2f", produto.getQuantidade()), 300, y);
                        graphics.drawString(produto.getUnidade(), 400, y);
                    }

                    // Draw footer
                    y = (int) pageFormat.getImageableHeight() - 20;
                    graphics.setFont(new Font("Arial", Font.ITALIC, 8));
                    graphics.drawString("Quântica Tecnologia - Sistema de Gerenciamento de Estoque", 50, y);

                    return PAGE_EXISTS;
                }
            };

            // Set the printable on the PrinterJob
            job.setPrintable(printable);

            // Show the print dialog and print if the user confirms
            if (job.printDialog()) {
                job.print();
                return true;
            }

            return false;
        } catch (PrinterException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to print a single product detail
    public static boolean printProdutoDetalhado(Produto produto) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Quântica Tecnologia - Detalhes do Produto");

            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    int y = 20; // Start position for printing
                    int lineHeight = 20; // Height of each line

                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE; // Only one page for now
                    }

                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                    // Print header
                    graphics.setFont(new Font("Arial", Font.BOLD, 14));
                    graphics.drawString("Quântica Tecnologia - Detalhes do Produto", 50, y);
                    y += lineHeight + 10;

                    // Print date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String dateTime = dateFormat.format(new Date());
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    graphics.drawString("Data/Hora: " + dateTime, 50, y);
                    y += lineHeight + 20;

                    // Print product details
                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("ID:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(String.valueOf(produto.getId()), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Nome:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));

                    // Handle long product names with line breaks
                    String nome = produto.getNome();
                    if (nome.length() > 30) {
                        String[] lines = splitTextIntoLines(nome, 30);
                        for (String line : lines) {
                            graphics.drawString(line, 200, y);
                            y += lineHeight;
                        }
                    } else {
                        graphics.drawString(nome, 200, y);
                        y += lineHeight;
                    }

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Quantidade:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(String.format("%.2f %s", produto.getQuantidade(), produto.getUnidade()), 200, y);
                    y += lineHeight + 30;

                    return PAGE_EXISTS;
                }
            };

            job.setPrintable(printable);

            if (job.printDialog()) {
                job.print();
                return true;
            }

            return false;
        } catch (PrinterException e) {
            e.printStackTrace();
            return false;
        }
    }

   


    // Method to print a list of SaidaProduto
    public static boolean printSaidas(List<SaidaProduto> saidas, String searchTerm) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Quântica Tecnologia - Relatório de Saídas");

            // Create a custom printable object
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    int y = 20; // Start position for printing
                    int lineHeight = 15; // Height of each line

                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE; // Only one page for now
                    }

                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                    // Print header
                    graphics.setFont(new Font("Arial", Font.BOLD, 14));
                    graphics.drawString("Quântica Tecnologia - Relatório de Saídas", 50, y);
                    y += lineHeight + 5;

                    // Print date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String dateTime = dateFormat.format(new Date());
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    graphics.drawString("Data/Hora: " + dateTime, 50, y);
                    y += lineHeight;

                    // Print search term if provided
                    if (searchTerm != null && !searchTerm.isEmpty()) {
                        graphics.drawString("Termo de Busca: " + searchTerm, 50, y);
                        y += lineHeight;
                    }

                    graphics.drawString("Total de Saídas: " + saidas.size(), 50, y);
                    y += lineHeight + 10;

                    // Print table header
                    graphics.setFont(new Font("Arial", Font.BOLD, 10));
                    graphics.drawString("ID", 50, y);
                    graphics.drawString("Produto", 100, y);
                    graphics.drawString("Quantidade", 250, y);
                    graphics.drawString("Portador", 350, y);
                    graphics.drawString("Destino", 450, y);
                    graphics.drawString("Data", 550, y);
                    y += lineHeight;

                    // Draw a line below the header
                    graphics.drawLine(50, y - 5, 600, y - 5);

                    // Print items
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    for (SaidaProduto saida : saidas) {
                        y += lineHeight;

                        // Check if we need to start a new page
                        if (y > pageFormat.getImageableHeight() - 20) {
                            return PAGE_EXISTS;
                        }

                        // Print SaidaProduto details
                        graphics.drawString(String.valueOf(saida.getId()), 50, y);
                        graphics.drawString(saida.getProdutoNome(), 100, y);
                        graphics.drawString(String.format("%.2f %s", saida.getQuantidade(), saida.getUnidade()), 250, y);
                        graphics.drawString(saida.getPortadorNome(), 350, y);
                        graphics.drawString(saida.getDestinoNome(), 450, y);
                        graphics.drawString(saida.getDataSaida().toString(), 550, y);
                    }

                    // Draw footer
                    y = (int) pageFormat.getImageableHeight() - 20;
                    graphics.setFont(new Font("Arial", Font.ITALIC, 8));
                    graphics.drawString("Quântica Tecnologia - Sistema de Gerenciamento de Estoque", 50, y);

                    return PAGE_EXISTS;
                }
            };

            // Set the printable on the PrinterJob
            job.setPrintable(printable);

            // Show the print dialog and print if the user confirms
            if (job.printDialog()) {
                job.print();
                return true;
            }

            return false;
        } catch (PrinterException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to print detailed information about a single SaidaProduto
    public static boolean printSaidaDetalhada(SaidaProduto saida) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Quântica Tecnologia - Detalhes da Saída");

            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    int y = 20; // Start position for printing
                    int lineHeight = 20; // Height of each line

                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE; // Only one page for now
                    }

                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

                    // Print header
                    graphics.setFont(new Font("Arial", Font.BOLD, 14));
                    graphics.drawString("Quântica Tecnologia - Detalhes da Saída", 50, y);
                    y += lineHeight + 10;

                    // Print date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String dateTime = dateFormat.format(new Date());
                    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
                    graphics.drawString("Data/Hora: " + dateTime, 50, y);
                    y += lineHeight + 20;

                    // Print SaidaProduto details
                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("ID:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(String.valueOf(saida.getId()), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Produto:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(saida.getProdutoNome(), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Quantidade:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(String.format("%.2f %s", saida.getQuantidade(), saida.getUnidade()), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Portador:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(saida.getPortadorNome(), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Destino:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(saida.getDestinoNome(), 200, y);
                    y += lineHeight;

                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Data Saída:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(saida.getDataSaida().toString(), 200, y);
                    y += lineHeight + 30;

                    return PAGE_EXISTS;
                }
            };

            job.setPrintable(printable);

            if (job.printDialog()) {
                job.print();
                return true;
            }

            return false;
        } catch (PrinterException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to split text into lines of a specified length
    private static String[] splitTextIntoLines(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        while (text.length() > maxLength) {
            int breakIndex = text.lastIndexOf(' ', maxLength);
            if (breakIndex == -1) {
                breakIndex = maxLength; // No spaces, force break
            }
            lines.add(text.substring(0, breakIndex));
            text = text.substring(breakIndex).trim();
        }
        lines.add(text); // Add the remaining text
        return lines.toArray(new String[0]);
    }
    
}