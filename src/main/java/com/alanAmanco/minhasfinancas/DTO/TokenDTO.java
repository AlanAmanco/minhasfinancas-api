package com.alanAmanco.minhasfinancas.DTO;

public class TokenDTO {

    private String nome;
    private long id;
    private String token;


    public TokenDTO(String nome, Long id, String token) {
        this.nome = nome;
        this.token = token;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
