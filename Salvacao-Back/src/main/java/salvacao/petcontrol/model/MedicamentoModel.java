package salvacao.petcontrol.model;

public class MedicamentoModel {
    private Integer idproduto;
    private String composicao;

    public MedicamentoModel(Integer idproduto, String composicao) {
        this.idproduto = idproduto;
        this.composicao = composicao;
    }

    public MedicamentoModel(String composicao) {
        this.composicao = composicao;
    }

    // Construtor vazio
    public MedicamentoModel() {
        this(0, "");
    }

    // Getters e Setters
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
}
