package salvacao.petcontrol.service.template;

import salvacao.petcontrol.config.SingletonDB;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.Connection;
import java.sql.SQLException;


public abstract class OperacaoEstoqueTemplate {


    public final ResultadoOperacao executarOperacao(Object dadosOperacao) throws Exception {
        Connection conn = null;
        boolean autoCommitOriginal = true;

        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            validarDados(dadosOperacao);

            validarRegrasNegocio(dadosOperacao, conn);

            Object dadosProcessados = prepararDadosOperacao(dadosOperacao, conn);

            Object resultadoOperacao = executarOperacaoPrincipal(dadosProcessados, conn);

            atualizarEstoque(dadosProcessados, resultadoOperacao, conn);

            registrarHistoricoOperacao(dadosProcessados, resultadoOperacao, conn);

            conn.commit();

            return criarResultadoSucesso(resultadoOperacao);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return criarResultadoErro(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    protected abstract void validarRegrasNegocio(Object dados, Connection conn) throws Exception;


    protected abstract Object prepararDadosOperacao(Object dados, Connection conn) throws Exception;


    protected abstract Object executarOperacaoPrincipal(Object dadosProcessados, Connection conn) throws Exception;


    protected abstract void atualizarEstoque(Object dadosProcessados, Object resultadoOperacao, Connection conn) throws Exception;


    protected abstract ResultadoOperacao criarResultadoSucesso(Object resultadoOperacao);


    protected void validarDados(Object dados) throws Exception {
        if (dados == null) {
            throw new Exception("Dados da operação não podem ser nulos");
        }
    }


    protected void registrarHistoricoOperacao(Object dadosProcessados, Object resultadoOperacao, Connection conn) throws Exception {
    }


    protected ResultadoOperacao criarResultadoErro(Exception e) {
        return new ResultadoOperacao("erro", false, "Erro na operação: " + e.getMessage());
    }
}
