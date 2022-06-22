package com.alanAmanco.minhasfinancas.service;

import com.alanAmanco.minhasfinancas.model.entity.Lancamento;
import com.alanAmanco.minhasfinancas.model.enums.StatusLancamento;

import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void delatar(Lancamento lancament);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long id);
}
