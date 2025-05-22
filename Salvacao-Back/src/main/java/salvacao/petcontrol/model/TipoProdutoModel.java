package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.TipoProdutoDAO;

@Repository
public class TipoProdutoModel {
    private Integer idtipoproduto;
    private String descricao;
    private TipoProdutoDAO tpDAO;

    public TipoProdutoModel(Integer idtipoproduto, String descricao) {
        this.idtipoproduto = idtipoproduto;
        this.descricao = descricao;
    }

    public TipoProdutoModel(){
        tpDAO = new TipoProdutoDAO();
    }

    public TipoProdutoModel(String descricao) {
        this.descricao = descricao;
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

    public TipoProdutoDAO getTpDAO() {
        return tpDAO;
    }


}
