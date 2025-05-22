package salvacao.petcontrol.model;

import salvacao.petcontrol.dao.UnidadeMedidaDAO;

public class UnidadeMedidaModel {
    private int idUnidadeMedida;
    private String descricao;
    private String sigla;
    private UnidadeMedidaDAO unDAO;

    public UnidadeMedidaModel() {
        unDAO = new UnidadeMedidaDAO();
    }

    public UnidadeMedidaModel(int idUnidadeMedida, String descricao, String sigla) {
        this.idUnidadeMedida = idUnidadeMedida;
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public int getIdUnidadeMedida() {
        return idUnidadeMedida;
    }

    public void setIdUnidadeMedida(int idUnidadeMedida) {
        this.idUnidadeMedida = idUnidadeMedida;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return descricao + " (" + sigla + ")";
    }

    public UnidadeMedidaDAO getUnDAO() {
        return unDAO;
    }
}