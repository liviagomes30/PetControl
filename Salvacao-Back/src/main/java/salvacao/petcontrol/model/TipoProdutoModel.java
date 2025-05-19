package salvacao.petcontrol.model;

public class TipoProdutoModel {
    private Integer idtipoproduto;
    private String descricao;

    public TipoProdutoModel(Integer idtipoproduto, String descricao) {
        this.idtipoproduto = idtipoproduto;
        this.descricao = descricao;
    }

    public TipoProdutoModel(String descricao) {
        this.descricao = descricao;
    }

    public TipoProdutoModel() {
        this(0, "");
    }

    public Integer getIdtipoproduto() {
        return idtipoproduto;
    }

    public void setIdtipoproduto(Integer idtipoproduto) {
        this.idtipoproduto = idtipoproduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
