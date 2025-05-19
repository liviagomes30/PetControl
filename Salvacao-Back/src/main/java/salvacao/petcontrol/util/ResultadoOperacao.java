package salvacao.petcontrol.util;

public class ResultadoOperacao {
    private String operacao;
    private boolean sucesso;
    private String mensagem;


    public ResultadoOperacao() {
    }


    public ResultadoOperacao(String operacao, boolean sucesso, String mensagem) {
        this.operacao = operacao;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }


    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}