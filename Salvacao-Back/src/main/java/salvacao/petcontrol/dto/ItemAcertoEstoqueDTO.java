package salvacao.petcontrol.dto;

import java.math.BigDecimal;
import salvacao.petcontrol.model.ItemAcertoEstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;

public class ItemAcertoEstoqueDTO {
    private ItemAcertoEstoqueModel item;
    private ProdutoModel produto;
    private String nomeTipoProduto;
    private String unidadeMedida;

    // Construtores
    public ItemAcertoEstoqueDTO() {
    }

    public ItemAcertoEstoqueDTO(ItemAcertoEstoqueModel item, ProdutoModel produto,
                                String nomeTipoProduto, String unidadeMedida) {
        this.item = item;
        this.produto = produto;
        this.nomeTipoProduto = nomeTipoProduto;
        this.unidadeMedida = unidadeMedida;
    }

    // Getters e Setters
    public ItemAcertoEstoqueModel getItem() {
        return item;
    }

    public void setItem(ItemAcertoEstoqueModel item) {
        this.item = item;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public String getNomeTipoProduto() {
        return nomeTipoProduto;
    }

    public void setNomeTipoProduto(String nomeTipoProduto) {
        this.nomeTipoProduto = nomeTipoProduto;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}