package br.edu.utfpr.cantina.repository;
import br.edu.utfpr.cantina.model.Cliente;
import java.sql.SQLException;
import java.util.List;
/** Abstração de persistência: regras de negócio dependem deste contrato, não do JDBC. */
public interface ClienteRepositoryPort {
    List<Cliente> listarTodos() throws SQLException;
    void inserir(Cliente cliente) throws SQLException;
    void atualizar(Cliente cliente) throws SQLException;
    void excluir(int id) throws SQLException;
}
