package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.model.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface PedidoRepositoryPort {
    void registrar(Pedido pedido) throws SQLException;
    List<Pedido> listarTodos() throws SQLException;
    List<Pedido> listarPorCliente(int idCliente) throws SQLException;
    void atualizarStatus(int idPedido, String status) throws SQLException;
}
