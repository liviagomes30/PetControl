package salvacao.petcontrol.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Nome do banco de dados ALTERADO AQUI
    private static final String URL = "jdbc:postgresql://localhost:5432/petcontrol-db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres123"; // Mantenha sua senha correta

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL não encontrado! Verifique a dependência no pom.xml.");
            throw new SQLException("Driver PostgreSQL não encontrado!", e);
        }
    }
}