package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.MovimentacaoEstoqueDAO;

import java.time.LocalDate;

@Repository
public class MovimentacaoEstoqueModel {
    private Integer idmovimentacao;
    private String tipomovimentacao;
    private LocalDate data;
    private Integer usuarioPessoaId;
    private String obs;
    private String fornecedor;

    private MovimentacaoEstoqueDAO movimentacaoEstoqueDAO;

    public MovimentacaoEstoqueModel(Integer idmovimentacao, String tipomovimentacao, LocalDate data, Integer usuarioPessoaId, String obs, String fornecedor) {
        this.idmovimentacao = idmovimentacao;
        this.tipomovimentacao = tipomovimentacao;
        this.data = data;
        this.usuarioPessoaId = usuarioPessoaId;
        this.obs = obs;
        this.fornecedor = fornecedor;
    }

    public MovimentacaoEstoqueModel(String tipomovimentacao, LocalDate data, Integer usuarioPessoaId, String obs, String fornecedor) {
        this.tipomovimentacao = tipomovimentacao;
        this.data = data;
        this.usuarioPessoaId = usuarioPessoaId;
        this.obs = obs;
        this.fornecedor = fornecedor;
    }

    public MovimentacaoEstoqueModel() {
        movimentacaoEstoqueDAO = new MovimentacaoEstoqueDAO();
    }

    public Integer getIdmovimentacao() {
        return idmovimentacao;
    }

    public void setIdmovimentacao(Integer idmovimentacao) {
        this.idmovimentacao = idmovimentacao;
    }

    public String getTipomovimentacao() {
        return tipomovimentacao;
    }

    public void setTipomovimentacao(String tipomovimentacao) {
        this.tipomovimentacao = tipomovimentacao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getUsuarioPessoaId() {
        return usuarioPessoaId;
    }

    public void setUsuarioPessoaId(Integer usuarioPessoaId) {
        this.usuarioPessoaId = usuarioPessoaId;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public MovimentacaoEstoqueDAO getMovimentacaoEstoqueDAL() {
        return movimentacaoEstoqueDAO;
    }
}

