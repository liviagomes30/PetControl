package salvacao.petcontrol.model;

public class ItensModel {
    private String produto;
    private String tipo;
    private String unidade;
    private double quantidade;

    public ItensModel(String produto, String tipo, String unidade, double quantidade) {
        this.produto = produto;
        this.tipo = tipo;
        this.unidade = unidade;
        this.quantidade = quantidade;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
