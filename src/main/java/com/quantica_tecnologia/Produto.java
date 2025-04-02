package com.quantica_tecnologia;

public class Produto {
    private String identificador;
    private String nome;
    private double quantidade;
    private String unidade;

    public Produto(String identificador, String nome, double quantidade, String unidade) {
        this.identificador = identificador;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
    }

    // Getters
    public String getIdentificador() { return identificador; }
    public String getNome() { return nome; }
    public double getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }

    // Setters
    public void setIdentificador(String identificador) { this.identificador = identificador; }
    public void setNome(String nome) { this.nome = nome; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
}
