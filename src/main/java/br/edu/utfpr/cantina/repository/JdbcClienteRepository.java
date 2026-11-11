package br.edu.utfpr.cantina.repository;

import br.edu.utfpr.cantina.database.Conexao;
import br.edu.utfpr.cantina.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcClienteRepository implements ClienteRepositoryPort {
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT id_cliente, nome, email FROM cliente ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection c = Conexao.obterConexao(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) clientes.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nome"), rs.getString("email")));
        }
        return clientes;
    }
    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, email) VALUES (?, ?)";
        try (Connection c = Conexao.obterConexao(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome()); ps.setString(2, cliente.getEmail()); ps.executeUpdate();
        }
    }
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, email = ? WHERE id_cliente = ?";
        try (Connection c = Conexao.obterConexao(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome()); ps.setString(2, cliente.getEmail()); ps.setInt(3, cliente.getIdCliente());
            if (ps.executeUpdate() == 0) throw new SQLException("Cliente não encontrado.");
        }
    }
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection c = Conexao.obterConexao(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); if (ps.executeUpdate() == 0) throw new SQLException("Cliente não encontrado.");
        }
    }
}
