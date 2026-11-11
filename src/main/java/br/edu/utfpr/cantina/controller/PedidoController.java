package br.edu.utfpr.cantina.controller;
import br.edu.utfpr.cantina.model.Pedido;
import br.edu.utfpr.cantina.service.PedidoService;
import java.sql.SQLException;
import java.util.List;
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service){this.service=service;}
    public void registrar(Pedido pedido)throws SQLException{service.registrar(pedido);}
    public List<Pedido> listar()throws SQLException{return service.listar();}
    public List<Pedido> listarPorCliente(int idCliente)throws SQLException{return service.listarPorCliente(idCliente);}
    public void atualizarStatus(int id,String status)throws SQLException{service.atualizarStatus(id,status);}
}
