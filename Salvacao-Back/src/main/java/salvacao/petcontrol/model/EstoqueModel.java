package salvacao.petcontrol.model;

import java.math.BigDecimal;

public class EstoqueModel {
    private Integer idestoque;
    private Integer idproduto;
    private BigDecimal quantidade;

    public EstoqueModel() {
    }

    public EstoqueModel(Integer idestoque, Integer idproduto, BigDecimal quantidade) {
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}