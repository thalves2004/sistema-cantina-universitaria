package br.edu.utfpr.cantina.service;

import br.edu.utfpr.cantina.model.Cliente;
import br.edu.utfpr.cantina.repository.ClienteRepositoryPort;
import java.sql.SQLException;
import java.util.List;

public class ClienteService {
    private final ClienteRepositoryPort repository;
    public ClienteService(ClienteRepositoryPort repository) { this.repository = repository; }
    public List<Cliente> listar() throws SQLException { return repository.listarTodos(); }
    public void salvar(Cliente cliente) throws SQLException {
        validar(cliente);
        if (cliente.getIdCliente() == 0) repository.inserir(cliente); else repository.atualizar(cliente);
    }
    public void excluir(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Selecione um cliente válido.");
        repository.excluir(id);
    }
    private void validar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty())
            throw new IllegalArgumentException("Informe o nome do cliente.");
        if (cliente.getNome().trim().length() > 120)
            throw new IllegalArgumentException("O nome deve ter no máximo 120 caracteres.");
        if (cliente.getEmail() == null || !cliente.getEmail().matches("^[\\w.!#$%&'*+/=?^_`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+$"))
            throw new IllegalArgumentException("Informe um e-mail válido.");
        if (cliente.getEmail().length() > 150) throw new IllegalArgumentException("O e-mail deve ter no máximo 150 caracteres.");
        cliente.setNome(cliente.getNome().trim()); cliente.setEmail(cliente.getEmail().trim().toLowerCase());
    }
}
