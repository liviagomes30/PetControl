package salvacao.petcontrol.model;

import salvacao.petcontrol.dao.MedicamentoDAO;

public class MedicamentoModel {
    private Integer idproduto;
    private String composicao;
    private MedicamentoDAO medDAO;

    public MedicamentoModel(Integer idproduto, String composicao) {
        this.idproduto = idproduto;
        this.composicao = composicao;
    }

    public MedicamentoModel(String composicao) {
        this.composicao = composicao;
    }

    public MedicamentoModel() {
        medDAO = new MedicamentoDAO();
    }

    public Integer getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(Integer idproduto) {
        this.idproduto = idproduto;
    }

    public String getComposicao() {
        return composicao;
    }

    public void setComposicao(String composicao) {
        this.composicao = composicao;
    }

    public MedicamentoDAO getMedDAO(){
        return medDAO;
    }
}
