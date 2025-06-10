package salvacao.petcontrol.config;

import java.sql.*;

public class Conexao
{
    private Connection connect;
    private String erro;

    public Conexao() {
        erro = "";
        connect = null;
    }

    public boolean conectar(String local, String banco, String usuario, String senha) {
        boolean conectado = false;
        try {
            //forçando carregamento do driver
            Class.forName("org.postgresql.Driver");

            String url = local + banco;
            connect = DriverManager.getConnection(url, usuario, senha);
            conectado = true;
        } catch (SQLException sqlex) {
            erro = "Impossível conectar com a base de dados: " + sqlex;
            sqlex.printStackTrace();
        } catch (Exception ex) {
            erro = "Outro erro: " + ex;
            ex.printStackTrace();
        }
        return conectado;
    }

    public String getMensagemErro() {
        return erro;
    }

    public boolean getEstadoConexao() {
        return (connect != null);
    }

    public boolean manipular(String sql) { // inserir, alterar, excluir
        boolean executou = false;
        try (Statement statement = connect.createStatement()) {
            int result = statement.executeUpdate(sql);
            executou = result >= 1;
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex;
            sqlex.printStackTrace();
        }
        return executou;
    }

    public ResultSet consultar(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = connect.createStatement();
            rs = statement.executeQuery(sql);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex;
            sqlex.printStackTrace();
        }
        return rs;
    }

    public int getMaxPK(String tabela, String chave) {
        String sql = "SELECT MAX(" + chave + ") FROM " + tabela;
        int max = 0;
        ResultSet rs = consultar(sql);
        try {
            if (rs != null && rs.next()) {
                max = rs.getInt(1);
            }
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex;
            max = -1;
        }
        return max;
    }

    public Connection getConnection() {
        return connect;
    }
}

