package com.alanAmanco.minhasfinancas.service;

import com.alanAmanco.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario Usuario);

    void validarEmail(String email);

    Optional<Usuario> obterPorId(Long id);
}
