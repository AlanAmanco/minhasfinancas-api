package com.alanAmanco.minhasfinancas.DTO;

public class UsuarioDTO {

    private String email;
    private String nome;
    private String senha;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String email, String nome, String senha) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
    }

    public UsuarioDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
