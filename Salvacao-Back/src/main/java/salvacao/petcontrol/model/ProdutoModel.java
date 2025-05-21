package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dal.ProdutoDAL;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public class ProdutoModel {
    private Integer idproduto;
    private String nome;
    private Integer idtipoproduto;
    private Integer idunidademedida;
    private String fabricante;
    private BigDecimal preco;
    private Integer estoqueMinimo;
    private Date dataCadastro;
    private ProdutoDAL produtoDAL;

    // Construtor completo
    public ProdutoModel(Integer idproduto, String nome, Integer idtipoproduto, Integer idunidademedida,
                        String fabricante, BigDecimal preco, Integer estoqueMinimo, Date dataCadastro) {
        this.idproduto = idproduto;
        this.nome = nome;
        this.idtipoproduto = idtipoproduto;
        this.idunidademedida = idunidademedida;
        this.fabricante = fabricante;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.dataCadastro = dataCadastro;
    }

    // Construtor simplificado (usado no MedicamentoDAL)
    public ProdutoModel(Integer idproduto, String nome, Integer idtipoproduto, Integer idunidademedida, String fabricante) {
        this.idproduto = idproduto;
        this.nome = nome;
        this.idtipoproduto = idtipoproduto;
        this.idunidademedida = idunidademedida;
        this.fabricante = fabricante;
    }

    // Construtor sem ID para novas inserções
    public ProdutoModel(String nome, Integer idtipoproduto, Integer idunidademedida,
                        String fabricante, BigDecimal preco, Integer estoqueMinimo) {
        this.nome = nome;
        this.idtipoproduto = idtipoproduto;
        this.idunidademedida = idunidademedida;
        this.fabricante = fabricante;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.dataCadastro = new Date(); // Data atual
    }

    // Construtor vazio
    public ProdutoModel() {
        produtoDAL = new ProdutoDAL();
    }

    // Getters e Setters
    public Integer getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(Integer idproduto) {
        this.idproduto = idproduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdtipoproduto() {
        return idtipoproduto;
    }

    public void setIdtipoproduto(Integer idtipoproduto) {
        this.idtipoproduto = idtipoproduto;
    }

    public Integer getIdunidademedida() {
        return idunidademedida;
    }

    public void setIdunidademedida(Integer idunidademedida) {
        this.idunidademedida = idunidademedida;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ProdutoDAL getProdutoDAL() {
        return produtoDAL;
    }
}