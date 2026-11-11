package br.edu.utfpr.cantina.controller;

import br.edu.utfpr.cantina.model.*;
import br.edu.utfpr.cantina.service.RelatorioService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RelatorioController {
    private final RelatorioService service;
    public RelatorioController(RelatorioService service){this.service=service;}
    public ResumoDashboard resumo()throws SQLException{return service.resumo();}
    public List<VendaPorDia> vendas(LocalDate inicio,LocalDate fim)throws SQLException{return service.vendas(inicio,fim);}
    public List<String[]> estoqueBaixo()throws SQLException{return service.estoqueBaixo();}
}
