package com.alanAmanco.minhasfinancas.api.resource;

import com.alanAmanco.minhasfinancas.DTO.LancamentoDTO;
import com.alanAmanco.minhasfinancas.exception.RegraNegociosException;
import com.alanAmanco.minhasfinancas.model.entity.Lancamento;
import com.alanAmanco.minhasfinancas.model.entity.Usuario;
import com.alanAmanco.minhasfinancas.model.enums.StatusLancamento;
import com.alanAmanco.minhasfinancas.model.enums.TipoLancamento;
import com.alanAmanco.minhasfinancas.service.LancamentoService;
import com.alanAmanco.minhasfinancas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    private LancamentoService service;
    private UsuarioService usuarioService;

    public LancamentoResource(LancamentoService service) {
        this.service = service;
    }

    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch(RegraNegociosException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto){
        return service.obterPorId(id).map( entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch(RegraNegociosException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrato na base de Dados.",HttpStatus.BAD_REQUEST)) ;
    }

    public ResponseEntity deletar (@PathVariable("id") Long id){
        return service.obterPorId(id).map( entidade ->{
            service.delatar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(()-> new ResponseEntity("Lancamento não encontrado na base de Datos.",HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegociosException("Usuário não encontrado para o Id informado."));
        lancamento.setUsuario(usuario);
        lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

        return lancamento;
    }
}
