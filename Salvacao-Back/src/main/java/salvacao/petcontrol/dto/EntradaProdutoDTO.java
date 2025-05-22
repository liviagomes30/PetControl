package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;

public class EntradaProdutoDTO {
    private ProdutoModel produto;
    private EstoqueModel estoque;

    public EntradaProdutoDTO(ProdutoModel produto, EstoqueModel estoque) {
        this.produto = produto;
        this.estoque = estoque;
    }

    public EntradaProdutoDTO() {
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public EstoqueModel getEstoque() {
        return estoque;
    }

    public void setEstoque(EstoqueModel estoque) {
        this.estoque = estoque;
    }
}
