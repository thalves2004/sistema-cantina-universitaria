package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.model.Produto;
import java.sql.SQLException;
import java.util.List;

/** Contrato de persistência de produtos, independente da tecnologia utilizada. */
public interface ProdutoRepositoryPort {
    List<Produto> listar() throws SQLException;
    void salvar(Produto produto) throws SQLException;
    void excluir(int id) throws SQLException;
}
