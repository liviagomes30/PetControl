package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.ItemMovimentacaoDAO;

@Repository
public class ItemMovimentacaoModel {
    private Integer iditem;
    private Integer movimentacaoId;
    private Integer produtoId;
    private Double quantidade;
    private Integer motivomovimentacaoId;

    private ItemMovimentacaoDAO itemMovimentacaoDAO;

    public ItemMovimentacaoModel(Integer iditem, Integer movimentacaoId, Integer produtoId, Double quantidade, Integer motivomovimentacaoId) {
        this.iditem = iditem;
        this.movimentacaoId = movimentacaoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.motivomovimentacaoId = motivomovimentacaoId;
    }

    public ItemMovimentacaoModel(Integer movimentacaoId, Integer produtoId, Double quantidade, Integer motivomovimentacaoId) {
        this.movimentacaoId = movimentacaoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.motivomovimentacaoId = motivomovimentacaoId;
    }

    public ItemMovimentacaoModel() {
        itemMovimentacaoDAO = new ItemMovimentacaoDAO();
    }

    public Integer getIditem() {
        return iditem;
    }

    public void setIditem(Integer iditem) {
        this.iditem = iditem;
    }

    public Integer getMovimentacaoId() {
        return movimentacaoId;
    }

    public void setMovimentacaoId(Integer movimentacaoId) {
        this.movimentacaoId = movimentacaoId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getMotivomovimentacaoId() {
        return motivomovimentacaoId;
    }

    public void setMotivomovimentacaoId(Integer motivomovimentacaoId) {
        this.motivomovimentacaoId = motivomovimentacaoId;
    }

    public ItemMovimentacaoDAO getItemMovimentacaoDAO() {
        return itemMovimentacaoDAO;
    }
}

