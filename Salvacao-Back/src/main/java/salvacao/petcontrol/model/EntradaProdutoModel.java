package salvacao.petcontrol.model;

import java.time.LocalDate;

public class EntradaProdutoModel {
    private Integer idprod;
    private Integer idusu;
    private Double quantidade;
    private String fornecedor;
    private String observacao;
    private LocalDate date;

    public EntradaProdutoModel(Integer idprod, Integer idusu, Double quantidade, String fornecedor, String observacao, LocalDate date) {
        this.idprod = idprod;
        this.idusu = idusu;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;
        this.observacao = observacao;
        this.date = date;
    }

    public EntradaProdutoModel(Integer idprod, Double quantidade, String fornecedor, String observacao, LocalDate date) {
        this.idprod = idprod;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;
        this.observacao = observacao;
        this.date = date;
    }

    public EntradaProdutoModel() {
    }

    public Integer getIdprod() {
        return idprod;
    }

    public void setIdprod(Integer idprod) {
        this.idprod = idprod;
    }

    public Integer getIdusu() {
        return idusu;
    }

    public void setIdusu(Integer idusu) {
        this.idusu = idusu;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
