package salvacao.petcontrol.model;

public class ItensEntrada {
    private Integer produtoId;
    private String nome;
    private double quantidade;

    public ItensEntrada(Integer produtoId, String nome, double quantidade) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
