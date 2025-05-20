package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;

public class EstoqueProdutoDTO {
    private EstoqueModel estoque;
    private ProdutoModel produto;
    private TipoProdutoModel tipoProduto;
    private UnidadeMedidaModel unidadeMedida;
    private boolean abaixoMinimo;

    public EstoqueProdutoDTO() {
    }

    public EstoqueProdutoDTO(EstoqueModel estoque, ProdutoModel produto,
                             TipoProdutoModel tipoProduto, UnidadeMedidaModel unidadeMedida) {
        this.estoque = estoque;
        this.produto = produto;
        this.tipoProduto = tipoProduto;
        this.unidadeMedida = unidadeMedida;
        this.abaixoMinimo = produto.getEstoqueMinimo() != null &&
                estoque.getQuantidade().intValue() < produto.getEstoqueMinimo();
    }

    public EstoqueModel getEstoque() {
        return estoque;
    }

    public void setEstoque(EstoqueModel estoque) {
        this.estoque = estoque;
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

    public boolean isAbaixoMinimo() {
        return abaixoMinimo;
    }

    public void setAbaixoMinimo(boolean abaixoMinimo) {
        this.abaixoMinimo = abaixoMinimo;
    }
}