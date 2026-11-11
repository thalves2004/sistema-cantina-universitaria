package br.edu.utfpr.cantina.service;

import br.edu.utfpr.cantina.model.*;
import br.edu.utfpr.cantina.repository.RelatorioRepositoryPort;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class RelatorioService {
    private final RelatorioRepositoryPort repository;
    public RelatorioService(RelatorioRepositoryPort repository){this.repository=Objects.requireNonNull(repository, "O repositório de relatórios é obrigatório.");}
    public ResumoDashboard resumo() throws SQLException{return repository.carregarResumo();}
    public List<VendaPorDia> vendas(LocalDate inicio,LocalDate fim)throws SQLException{
        if(inicio==null||fim==null||fim.isBefore(inicio))throw new IllegalArgumentException("Informe um período válido: a data final não pode ser anterior à inicial.");
        if(inicio.isBefore(LocalDate.now().minusYears(5)))throw new IllegalArgumentException("O período deve estar dentro dos últimos cinco anos.");
        return repository.vendasPorPeriodo(inicio,fim);
    }
    public List<String[]> estoqueBaixo()throws SQLException{return repository.produtosComEstoqueBaixo();}
}
