package com.quantica_tecnologia;

import java.awt.print.*;
import java.util.List;
import java.text.SimpleDateFormat;
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
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    
                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
                    int y = 20;
                    int lineHeight = 15;
                    
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
                    graphics.drawString("Identificador", 50, y);
                    graphics.drawString("Nome", 150, y);
                    graphics.drawString("Quantidade", 350, y);
                    graphics.drawString("Unidade", 450, y);
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
                        
                        //graphics.drawString(produto.getId(), 50, y);
                        //how to convert int to string
                        graphics.drawString(String.valueOf(produto.getId()), 50, y);
                        // Truncate long names
                        String nome = produto.getNome();
                        if (nome.length() > 30) {
                            nome = nome.substring(0, 27) + "...";
                        }
                        graphics.drawString(nome, 150, y);
                        
                        graphics.drawString(String.format("%.2f", produto.getQuantidade()), 350, y);
                        graphics.drawString(produto.getUnidade(), 450, y);
                    }
                    
                    // Draw footer
                    y = (int)pageFormat.getImageableHeight() - 20;
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
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    
                    // Set up the graphics context
                    graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
                    int y = 20;
                    int lineHeight = 20;
                    
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
                    graphics.drawString("Identificador:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    //graphics.drawString(produto.getIdentificador(), 200, y);
                    graphics.drawString(String.valueOf(produto.getId()), 200, y);
                    y += lineHeight;
                    
                    graphics.setFont(new Font("Arial", Font.BOLD, 12));
                    graphics.drawString("Nome:", 50, y);
                    graphics.setFont(new Font("Arial", Font.PLAIN, 12));
                    graphics.drawString(produto.getNome(), 200, y);
                    y += lineHeight;
                    
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
}