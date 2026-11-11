package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.database.Conexao;
import br.edu.utfpr.cantina.model.ResumoDashboard;
import br.edu.utfpr.cantina.model.VendaPorDia;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JdbcRelatorioRepository implements RelatorioRepositoryPort {
    public ResumoDashboard carregarResumo() throws SQLException {
        try(Connection c=Conexao.obterConexao()) {
            int clientes=contar(c,"SELECT COUNT(*) FROM cliente");
            int produtos=contar(c,"SELECT COUNT(*) FROM produto");
            int estoqueBaixo=contar(c,"SELECT COUNT(*) FROM produto WHERE quantidade_estoque <= 5");
            int pedidosAbertos=contar(c,"SELECT COUNT(*) FROM pedido WHERE status IN ('PENDENTE','EM PREPARO','PRONTO')");
            BigDecimal hoje=valor(c,"SELECT COALESCE(SUM(i.quantidade*i.preco_unitario),0) FROM pedido p JOIN item_pedido i ON i.id_pedido=p.id_pedido WHERE DATE(p.data_hora)=CURDATE() AND p.status <> 'CANCELADO'");
            BigDecimal mes=valor(c,"SELECT COALESCE(SUM(i.quantidade*i.preco_unitario),0) FROM pedido p JOIN item_pedido i ON i.id_pedido=p.id_pedido WHERE YEAR(p.data_hora)=YEAR(CURDATE()) AND MONTH(p.data_hora)=MONTH(CURDATE()) AND p.status <> 'CANCELADO'");
            return new ResumoDashboard(clientes,produtos,estoqueBaixo,pedidosAbertos,hoje,mes);
        }
    }
    public List<VendaPorDia> vendasPorPeriodo(LocalDate inicio,LocalDate fim) throws SQLException {
        String sql="SELECT DATE(p.data_hora) AS dia, COUNT(DISTINCT p.id_pedido) AS quantidade, " +
                "COALESCE(SUM(i.quantidade*i.preco_unitario),0) AS total " +
                "FROM pedido p LEFT JOIN item_pedido i ON i.id_pedido=p.id_pedido " +
                "WHERE DATE(p.data_hora) BETWEEN ? AND ? AND p.status <> 'CANCELADO' " +
                "GROUP BY DATE(p.data_hora) ORDER BY dia";
        List<VendaPorDia> lista=new ArrayList<>();
        try(Connection c=Conexao.obterConexao();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setDate(1,java.sql.Date.valueOf(inicio)); ps.setDate(2,java.sql.Date.valueOf(fim));
            try(ResultSet rs=ps.executeQuery()) { while(rs.next()) lista.add(new VendaPorDia(rs.getDate("dia").toLocalDate(),rs.getInt("quantidade"),rs.getBigDecimal("total"))); }
        }
        return lista;
    }
    public List<String[]> produtosComEstoqueBaixo() throws SQLException {
        List<String[]> lista=new ArrayList<>();
        try(Connection c=Conexao.obterConexao();PreparedStatement ps=c.prepareStatement("SELECT nome,quantidade_estoque FROM produto WHERE quantidade_estoque <= 5 ORDER BY quantidade_estoque,nome");ResultSet rs=ps.executeQuery()) {
            while(rs.next()) lista.add(new String[]{rs.getString(1),String.valueOf(rs.getInt(2))});
        }
        return lista;
    }
    private int contar(Connection c,String sql)throws SQLException {try(Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getInt(1);}}
    private BigDecimal valor(Connection c,String sql)throws SQLException {try(Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getBigDecimal(1);}}
}
