package br.edu.utfpr.cantina.controller;

import br.edu.utfpr.cantina.model.Cliente;
import br.edu.utfpr.cantina.service.ClienteService;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    public List<Cliente> listar() throws SQLException { return service.listar(); }
    public void salvar(Cliente cliente) throws SQLException { service.salvar(cliente); }
    public void excluir(int id) throws SQLException { service.excluir(id); }
}
