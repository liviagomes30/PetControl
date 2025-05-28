package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;

public class EntradaProdutoDTO {
    private ProdutoCompletoDTO produto;
    private EstoqueModel estoque;

    public EntradaProdutoDTO(ProdutoCompletoDTO produto, EstoqueModel estoque) {
        this.produto = produto;
        this.estoque = estoque;
    }

    public EntradaProdutoDTO() {
    }

    public ProdutoCompletoDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoCompletoDTO produto) {
        this.produto = produto;
    }

    public EstoqueModel getEstoque() {
        return estoque;
    }

    public void setEstoque(EstoqueModel estoque) {
        this.estoque = estoque;
    }
}
