package salvacao.petcontrol.model;

import java.time.LocalDate;

public class RegistroModel {
    private String usuario;
    private String produto;
    private LocalDate data;
    private Double quantidade;
    private String fornecedor;

    public RegistroModel(String usuario, String produto, LocalDate data, Double quantidade, String fornecedor) {
        this.usuario = usuario;
        this.produto = produto;
        this.data = data;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }
}
