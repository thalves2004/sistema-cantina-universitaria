package br.edu.utfpr.cantina.repository;
import br.edu.utfpr.cantina.database.Conexao;
import br.edu.utfpr.cantina.model.Produto;
import java.sql.*; import java.math.BigDecimal; import java.util.*;
public class ProdutoRepository {
 public List<Produto> listar() throws SQLException { List<Produto> out=new ArrayList<>(); String q="SELECT id_produto,nome,preco,quantidade_estoque FROM produto ORDER BY nome"; try(Connection c=Conexao.obterConexao();PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){while(r.next())out.add(new Produto(r.getInt(1),r.getString(2),r.getBigDecimal(3),r.getInt(4)));} return out; }
 public void salvar(Produto x) throws SQLException { if(x.getIdProduto()==0){try(Connection c=Conexao.obterConexao();PreparedStatement p=c.prepareStatement("INSERT INTO produto(nome,preco,quantidade_estoque) VALUES(?,?,?)")){bind(p,x);p.executeUpdate();}}else{try(Connection c=Conexao.obterConexao();PreparedStatement p=c.prepareStatement("UPDATE produto SET nome=?,preco=?,quantidade_estoque=? WHERE id_produto=?")){bind(p,x);p.setInt(4,x.getIdProduto());if(p.executeUpdate()==0)throw new SQLException("Produto não encontrado.");}} }
 private void bind(PreparedStatement p,Produto x)throws SQLException{p.setString(1,x.getNome());p.setBigDecimal(2,x.getPreco());p.setInt(3,x.getQuantidadeEstoque());}
 public void excluir(int id)throws SQLException{try(Connection c=Conexao.obterConexao();PreparedStatement p=c.prepareStatement("DELETE FROM produto WHERE id_produto=?")){p.setInt(1,id);if(p.executeUpdate()==0)throw new SQLException("Produto não encontrado.");}}
}
