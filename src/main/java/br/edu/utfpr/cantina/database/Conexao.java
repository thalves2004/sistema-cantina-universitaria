package br.edu.utfpr.cantina.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Centraliza a conexão JDBC. Configure CANTINA_DB_URL, CANTINA_DB_USER e CANTINA_DB_PASSWORD no ambiente. */
public final class Conexao {
    private static final String URL = System.getenv().getOrDefault(
            "CANTINA_DB_URL", "jdbc:mysql://localhost:3306/cantina_universitaria?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true");
    private static final String USUARIO = System.getenv().getOrDefault("CANTINA_DB_USER", "root");
    private static final String SENHA = System.getenv().getOrDefault("CANTINA_DB_PASSWORD", "");

    private Conexao() { }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static boolean testarConexao() {
        try (Connection ignored = obterConexao()) { return true; }
        catch (SQLException e) { return false; }
    }
}
