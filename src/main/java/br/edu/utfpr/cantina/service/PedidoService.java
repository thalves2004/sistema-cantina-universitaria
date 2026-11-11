package br.edu.utfpr.cantina.service;

import br.edu.utfpr.cantina.model.*;
import br.edu.utfpr.cantina.repository.PedidoRepositoryPort;
import java.sql.SQLException;
import java.util.*;

public class PedidoService {
    private static final Set<String> STATUS = new HashSet<>(Arrays.asList("PENDENTE", "EM PREPARO", "PRONTO", "ENTREGUE", "CANCELADO"));
    private final PedidoRepositoryPort repositorio;
    public PedidoService(PedidoRepositoryPort repositorio) { this.repositorio=Objects.requireNonNull(repositorio); }
    public void registrar(Pedido pedido) throws SQLException {
        if(pedido==null || pedido.getCliente()==null || pedido.getCliente().getIdCliente()<=0) throw new IllegalArgumentException("Selecione um cliente.");
        if(pedido.getItens().isEmpty()) throw new IllegalArgumentException("Adicione pelo menos um produto ao pedido.");
        for(ItemPedido item:pedido.getItens()) if(item==null || item.getQuantidade()<=0 || item.getProduto()==null || item.getProduto().getIdProduto()<=0) throw new IllegalArgumentException("Item do pedido inválido.");
        repositorio.registrar(pedido);
    }
    public List<Pedido> listar() throws SQLException { return repositorio.listarTodos(); }
    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        if (idCliente <= 0) throw new IllegalArgumentException("Selecione um cliente.");
        return repositorio.listarPorCliente(idCliente);
    }
    public void atualizarStatus(int id,String status) throws SQLException {
        if(id<=0) throw new IllegalArgumentException("Selecione um pedido.");
        if(status==null || !STATUS.contains(status)) throw new IllegalArgumentException("Status inválido.");
        repositorio.atualizarStatus(id,status);
    }
}
