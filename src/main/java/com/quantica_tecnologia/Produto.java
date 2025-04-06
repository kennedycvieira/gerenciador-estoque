package com.quantica_tecnologia;

public class Produto {
    private int id; // Replace identificador with id
    private String nome;
    private double quantidade;
    private String unidade;

    public Produto(int id, String nome, double quantidade, String unidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
}