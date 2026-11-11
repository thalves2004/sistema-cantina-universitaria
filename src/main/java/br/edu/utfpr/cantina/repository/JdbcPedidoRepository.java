package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.database.Conexao;
import br.edu.utfpr.cantina.model.*;
import java.sql.*;
import java.util.*;

public class JdbcPedidoRepository implements PedidoRepositoryPort {
    @Override public void registrar(Pedido pedido) throws SQLException {
        String inserirPedido = "INSERT INTO pedido(id_cliente,status) VALUES(?, 'PENDENTE')";
        String buscarProduto = "SELECT nome,preco,quantidade_estoque FROM produto WHERE id_produto=? FOR UPDATE";
        String baixarEstoque = "UPDATE produto SET quantidade_estoque=quantidade_estoque-? WHERE id_produto=? AND quantidade_estoque>=?";
        String inserirItem = "INSERT INTO item_pedido(id_pedido,id_produto,quantidade,preco_unitario) VALUES(?,?,?,?)";
        try (Connection c = Conexao.obterConexao()) {
            c.setAutoCommit(false);
            try {
                int idPedido;
                try (PreparedStatement ps = c.prepareStatement(inserirPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getCliente().getIdCliente()); ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (!rs.next()) throw new SQLException("Não foi possível gerar o número do pedido."); idPedido = rs.getInt(1); }
                }
                for (ItemPedido item : pedido.getItens()) {
                    int idProduto = item.getProduto().getIdProduto();
                    java.math.BigDecimal preco;
                    try (PreparedStatement ps = c.prepareStatement(buscarProduto)) {
                        ps.setInt(1, idProduto);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new SQLException("Produto não encontrado: " + item.getProduto().getNome());
                            int estoque = rs.getInt("quantidade_estoque");
                            if (estoque < item.getQuantidade()) throw new SQLException("Estoque insuficiente para " + rs.getString("nome") + ". Disponível: " + estoque);
                            preco = rs.getBigDecimal("preco");
                        }
                    }
                    try (PreparedStatement ps = c.prepareStatement(baixarEstoque)) {
                        ps.setInt(1,item.getQuantidade()); ps.setInt(2,idProduto); ps.setInt(3,item.getQuantidade());
                        if (ps.executeUpdate()!=1) throw new SQLException("Não foi possível atualizar o estoque do produto.");
                    }
                    try (PreparedStatement ps = c.prepareStatement(inserirItem)) {
                        ps.setInt(1,idPedido); ps.setInt(2,idProduto); ps.setInt(3,item.getQuantidade()); ps.setBigDecimal(4,preco); ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
            finally { c.setAutoCommit(true); }
        }
    }

    @Override public List<Pedido> listarTodos() throws SQLException {
        String sql = "SELECT p.id_pedido,p.id_cliente,c.nome,c.email,p.data_hora,p.status, " +
                "i.id_produto,pr.nome AS produto_nome,i.quantidade,i.preco_unitario " +
                "FROM pedido p JOIN cliente c ON c.id_cliente=p.id_cliente " +
                "LEFT JOIN item_pedido i ON i.id_pedido=p.id_pedido " +
                "LEFT JOIN produto pr ON pr.id_produto=i.id_produto ORDER BY p.data_hora DESC,p.id_pedido DESC";
        Map<Integer,Pedido> pedidos = new LinkedHashMap<>();
        try (Connection c=Conexao.obterConexao(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) {
                int id=rs.getInt("id_pedido");
                Pedido pedido=pedidos.get(id);
                if(pedido==null) {
                    Timestamp ts=rs.getTimestamp("data_hora");
                    pedido=new Pedido(id,new Cliente(rs.getInt("id_cliente"),rs.getString("nome"),rs.getString("email")),ts.toLocalDateTime(),rs.getString("status"));
                    pedidos.put(id,pedido);
                }
                int idProduto=rs.getInt("id_produto");
                if(!rs.wasNull()) {
                    java.math.BigDecimal preco=rs.getBigDecimal("preco_unitario");
                    Produto produto=new Produto(idProduto,rs.getString("produto_nome"),preco,0);
                    pedido.adicionarItem(new ItemPedido(produto,rs.getInt("quantidade")));
                }
            }
        }
        return new ArrayList<>(pedidos.values());
    }

    @Override public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT p.id_pedido,p.id_cliente,c.nome,c.email,p.data_hora,p.status, " +
                "i.id_produto,pr.nome AS produto_nome,i.quantidade,i.preco_unitario " +
                "FROM pedido p JOIN cliente c ON c.id_cliente=p.id_cliente " +
                "LEFT JOIN item_pedido i ON i.id_pedido=p.id_pedido " +
                "LEFT JOIN produto pr ON pr.id_produto=i.id_produto " +
                "WHERE p.id_cliente=? ORDER BY p.data_hora DESC,p.id_pedido DESC";
        Map<Integer,Pedido> pedidos = new LinkedHashMap<>();
        try (Connection c=Conexao.obterConexao(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,idCliente);
            try (ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    int id=rs.getInt("id_pedido");
                    Pedido pedido=pedidos.get(id);
                    if(pedido==null) {
                        pedido=new Pedido(id,new Cliente(rs.getInt("id_cliente"),rs.getString("nome"),rs.getString("email")),
                                rs.getTimestamp("data_hora").toLocalDateTime(),rs.getString("status"));
                        pedidos.put(id,pedido);
                    }
                    int idProduto=rs.getInt("id_produto");
                    if(!rs.wasNull()) pedido.adicionarItem(new ItemPedido(
                            new Produto(idProduto,rs.getString("produto_nome"),rs.getBigDecimal("preco_unitario"),0),rs.getInt("quantidade")));
                }
            }
        }
        return new ArrayList<>(pedidos.values());
    }

    @Override public void atualizarStatus(int idPedido,String status) throws SQLException {
        try(Connection c=Conexao.obterConexao()) {
            c.setAutoCommit(false);
            try {
                String atual;
                try(PreparedStatement ps=c.prepareStatement("SELECT status FROM pedido WHERE id_pedido=? FOR UPDATE")) {
                    ps.setInt(1,idPedido);
                    try(ResultSet rs=ps.executeQuery()) { if(!rs.next()) throw new SQLException("Pedido não encontrado."); atual=rs.getString(1); }
                }
                if("CANCELADO".equals(atual)) throw new SQLException("Um pedido cancelado não pode ser alterado novamente.");
                if("CANCELADO".equals(status)) {
                    String restaurar="UPDATE produto pr JOIN item_pedido i ON i.id_produto=pr.id_produto SET pr.quantidade_estoque=pr.quantidade_estoque+i.quantidade WHERE i.id_pedido=?";
                    try(PreparedStatement ps=c.prepareStatement(restaurar)){ps.setInt(1,idPedido);ps.executeUpdate();}
                }
                try(PreparedStatement ps=c.prepareStatement("UPDATE pedido SET status=? WHERE id_pedido=?")) { ps.setString(1,status);ps.setInt(2,idPedido);ps.executeUpdate(); }
                c.commit();
            } catch(SQLException | RuntimeException e) { c.rollback(); throw e; }
            finally { c.setAutoCommit(true); }
        }
    }
}
