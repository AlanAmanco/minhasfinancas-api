package com.alanAmanco.minhasfinancas.api.resource;

import com.alanAmanco.minhasfinancas.DTO.AtualizarStatusDTO;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    private LancamentoService service;
    private UsuarioService usuarioService;

    public LancamentoResource( LancamentoService service, UsuarioService usuarioService ) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao",required = false) String descricao,
            @RequestParam(value = "mes",required = false) Integer mes,
            @RequestParam(value = "ano",required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario
    ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento(@PathVariable("id") Long id){
            return service.obterPorId(id)
                    .map( lancamento -> new ResponseEntity( converter(lancamento), HttpStatus.OK) )
                    .orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND) );
    }

    @PostMapping
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

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatus( @PathVariable("id") Long id, @RequestBody AtualizarStatusDTO dto) {
        return service.obterPorId(id).map( entidade ->{
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null){
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status valido");            }
            try {
                entidade.setStatus(statusSelecionado);
                service.atualizar(entidade);
                return ResponseEntity.ok(entidade);
            }catch (RegraNegociosException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(()-> new ResponseEntity("Lançamento não encontrado na vase de Dados.",HttpStatus.BAD_REQUEST));

    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar (@PathVariable("id") Long id){
        return service.obterPorId(id).map( entidade ->{
            service.delatar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(()-> new ResponseEntity("Lancamento não encontrado na base de Datos.",HttpStatus.BAD_REQUEST));
    }

    private LancamentoDTO converter( Lancamento lancamento) {
        LancamentoDTO lancamentoDTO = new LancamentoDTO();
        lancamentoDTO.setId(lancamento.getId());
        lancamentoDTO.setDescricao(lancamento.getDescricao());
        lancamentoDTO.setAno(lancamento.getAno());
        lancamentoDTO.setMes(lancamento.getMes());
        lancamentoDTO.setValor(lancamento.getValor());
        lancamentoDTO.setStatus(lancamento.getStatus().name());
        lancamentoDTO.setTipo(lancamento.getTipo().name());
        lancamentoDTO.setUsuario(lancamento.getUsuario().getId());

        return lancamentoDTO;
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
        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if(dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }
}
