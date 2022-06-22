package com.alanAmanco.minhasfinancas.service.impl;

import com.alanAmanco.minhasfinancas.exception.ErroAutenticacao;
import com.alanAmanco.minhasfinancas.exception.RegraNegociosException;
import com.alanAmanco.minhasfinancas.model.entity.Usuario;
import com.alanAmanco.minhasfinancas.model.repository.UsuarioRepository;
import com.alanAmanco.minhasfinancas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if(!usuario.isPresent()) {
            throw new ErroAutenticacao("Usuário não encontrado.");
        }
        if(!usuario.get().getSenha().equals(senha )) {
            throw new ErroAutenticacao("Senha inválida.");
        }

        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe){
            throw new RegraNegociosException("Já existe um usuário cadastrado com esse email");
        }
    }
}
