package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.model.ResumoDashboard;
import br.edu.utfpr.cantina.model.VendaPorDia;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Contrato de consulta gerencial; permite testar os serviços sem MySQL. */
public interface RelatorioRepositoryPort {
    ResumoDashboard carregarResumo() throws SQLException;
    List<VendaPorDia> vendasPorPeriodo(LocalDate inicio, LocalDate fim) throws SQLException;
    List<String[]> produtosComEstoqueBaixo() throws SQLException;
}
