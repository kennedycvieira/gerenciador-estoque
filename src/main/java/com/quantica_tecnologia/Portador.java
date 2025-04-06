package com.quantica_tecnologia;

public class Portador {
    private int id;
    private String nome;
    
    public Portador(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    // Constructor without ID for new portadores
    public Portador(String nome) {
        this.nome = nome;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    @Override
    public String toString() {
        return nome;
    }
}