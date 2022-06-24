package com.alanAmanco.minhasfinancas.api.resource;

import com.alanAmanco.minhasfinancas.DTO.UsuarioDTO;
import com.alanAmanco.minhasfinancas.exception.RegraNegociosException;
import com.alanAmanco.minhasfinancas.model.entity.Usuario;
import com.alanAmanco.minhasfinancas.service.LancamentoService;
import com.alanAmanco.minhasfinancas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    private UsuarioService service;
    private LancamentoService lancamentoService;

    public UsuarioResource(UsuarioService service, LancamentoService lancamentoService ) {
        this.service = service;
        this.lancamentoService = lancamentoService;
    }

    @PostMapping("/autenticar")
    public ResponseEntity autentificar(@RequestBody UsuarioDTO dto){
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(),dto.getSenha());
            return  ResponseEntity.ok(usuarioAutenticado);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto){

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());

        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegociosException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id){
        Optional<Usuario> usuario = service.obterPorId(id);

        if(!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
