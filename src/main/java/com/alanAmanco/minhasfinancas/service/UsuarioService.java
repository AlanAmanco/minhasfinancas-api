package com.alanAmanco.minhasfinancas.service;

import com.alanAmanco.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario Usuario);

    void validarEmail(String email);
}
