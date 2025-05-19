package salvacao.petcontrol.model;

public class EstoqueModel {
    private Integer idestoque;
    private Integer idproduto;
    private Integer quantidade;

    public EstoqueModel() {
    }

    public EstoqueModel(Integer idestoque, Integer idproduto, Integer quantidade) {
        this.idestoque = idestoque;
        this.idproduto = idproduto;
        this.quantidade = quantidade;
    }

    public Integer getIdestoque() {
        return idestoque;
    }

    public void setIdestoque(Integer idestoque) {
        this.idestoque = idestoque;
    }

    public Integer getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(Integer idproduto) {
        this.idproduto = idproduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}