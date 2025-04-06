package com.quantica_tecnologia;

import java.util.Date;
/*
public class SaidaProduto {
    private int id;
    private int produtoId;
    private int portadorId;
    private int destinoId;
    private double quantidade;
    private Date dataSaida;
    
    // Full constructor
    public SaidaProduto(int id, int produtoId, int portadorId, int destinoId, double quantidade, Date dataSaida) {
        this.id = id;
        this.produtoId = produtoId;
        this.portadorId = portadorId;
        this.destinoId = destinoId;
        this.quantidade = quantidade;
        this.dataSaida = dataSaida;
    }
    
    // Constructor for new entries (without ID and date)
    public SaidaProduto(int produtoId, int portadorId, int destinoId, double quantidade) {
        this.produtoId = produtoId;
        this.portadorId = portadorId;
        this.destinoId = destinoId;
        this.quantidade = quantidade;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    
    public int getPortadorId() { return portadorId; }
    public void setPortadorId(int portadorId) { this.portadorId = portadorId; }
    
    public int getDestinoId() { return destinoId; }
    public void setDestinoId(int destinoId) { this.destinoId = destinoId; }
    
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    
    public Date getDataSaida() { return dataSaida; }
    public void setDataSaida(Date dataSaida) { this.dataSaida = dataSaida; }
}
    */

    public class SaidaProduto {
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
        
        public SaidaProduto(int id, int produtoId, String produtoNome, 
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