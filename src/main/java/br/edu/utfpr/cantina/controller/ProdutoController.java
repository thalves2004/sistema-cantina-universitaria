package br.edu.utfpr.cantina.controller;
import br.edu.utfpr.cantina.model.Produto; import br.edu.utfpr.cantina.service.ProdutoService; import java.sql.SQLException; import java.util.List;
public class ProdutoController {private final ProdutoService service;public ProdutoController(ProdutoService s){service=s;}public List<Produto> listar()throws SQLException{return service.listar();}public void salvar(Produto p)throws SQLException{service.salvar(p);}public void excluir(int id)throws SQLException{service.excluir(id);}}
