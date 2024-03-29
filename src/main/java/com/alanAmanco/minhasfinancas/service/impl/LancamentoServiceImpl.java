package com.alanAmanco.minhasfinancas.service.impl;

import com.alanAmanco.minhasfinancas.exception.RegraNegociosException;
import com.alanAmanco.minhasfinancas.model.entity.Lancamento;
import com.alanAmanco.minhasfinancas.model.enums.StatusLancamento;
import com.alanAmanco.minhasfinancas.model.enums.TipoLancamento;
import com.alanAmanco.minhasfinancas.model.repository.LancamentoRepository;
import com.alanAmanco.minhasfinancas.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    public void delatar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
           throw new RegraNegociosException("Informe um Descrição válido.");
        }
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
           throw new RegraNegociosException("Informe um Mês válido.");
        }
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ){
           throw new RegraNegociosException("Informe um Ano válido.");
        }
        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ){
           throw new RegraNegociosException("Informe um Valor válido.");
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
           throw new RegraNegociosException("Informe um Usuário válido.");
        }
        if(lancamento.getTipo() == null ){
           throw new RegraNegociosException("Informe um tipo de Lançamento.");
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {

        BigDecimal receitas = repository.obterSaldoPorTipoLancamentoUsuarioStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
        BigDecimal despesa = repository.obterSaldoPorTipoLancamentoUsuarioStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);

        if(receitas == null){
            receitas = BigDecimal.ZERO;
        }
        if(despesa == null){
            despesa = BigDecimal.ZERO;
        }

        return receitas.subtract(despesa);
    }
}
