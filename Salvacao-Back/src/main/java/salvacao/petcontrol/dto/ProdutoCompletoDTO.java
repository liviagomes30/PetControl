package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;


public class ProdutoCompletoDTO {
    private ProdutoModel produto;
    private TipoProdutoModel tipoProduto;
    private UnidadeMedidaModel unidadeMedida;

    public ProdutoCompletoDTO() {
    }

    public ProdutoCompletoDTO(ProdutoModel produto, TipoProdutoModel tipoProduto, UnidadeMedidaModel unidadeMedida) {
        this.produto = produto;
        this.tipoProduto = tipoProduto;
        this.unidadeMedida = unidadeMedida;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public TipoProdutoModel getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProdutoModel tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public UnidadeMedidaModel getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedidaModel unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}