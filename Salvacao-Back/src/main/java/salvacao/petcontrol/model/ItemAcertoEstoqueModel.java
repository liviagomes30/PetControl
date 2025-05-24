package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class ItemAcertoEstoqueModel {
    private Integer iditem;
    private Integer acerto_id;
    private Integer produto_id;
    private BigDecimal quantidade_antes;
    private BigDecimal quantidade_depois;
    private String tipoajuste; // ENTRADA ou SAIDA

    public ItemAcertoEstoqueModel() {
    }

    public ItemAcertoEstoqueModel(Integer iditem, Integer acerto_id, Integer produto_id,
                                  BigDecimal quantidade_antes, BigDecimal quantidade_depois,
                                  String tipoajuste) {
        this.iditem = iditem;
        this.acerto_id = acerto_id;
        this.produto_id = produto_id;
        this.quantidade_antes = quantidade_antes;
        this.quantidade_depois = quantidade_depois;
        this.tipoajuste = tipoajuste;
    }

    // Getters e Setters
    public Integer getIditem() {
        return iditem;
    }

    public void setIditem(Integer iditem) {
        this.iditem = iditem;
    }

    public Integer getAcerto_id() {
        return acerto_id;
    }

    public void setAcerto_id(Integer acerto_id) {
        this.acerto_id = acerto_id;
    }

    public Integer getProduto_id() {
        return produto_id;
    }

    public void setProduto_id(Integer produto_id) {
        this.produto_id = produto_id;
    }

    public BigDecimal getQuantidade_antes() {
        return quantidade_antes;
    }

    public void setQuantidade_antes(BigDecimal quantidade_antes) {
        this.quantidade_antes = quantidade_antes;
    }

    public BigDecimal getQuantidade_depois() {
        return quantidade_depois;
    }

    public void setQuantidade_depois(BigDecimal quantidade_depois) {
        this.quantidade_depois = quantidade_depois;
    }

    public String getTipoajuste() {
        return tipoajuste;
    }

    public void setTipoajuste(String tipoajuste) {
        this.tipoajuste = tipoajuste;
    }
}