package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.MotivoMovimentacaoDAO;

@Repository
public class MotivoMovimentacaoModel {
    private Integer idmotivo;
    private String descricao;
    private String tipo;

    private MotivoMovimentacaoDAO motivoMovimentacaoDAO;

    public MotivoMovimentacaoModel(Integer idmotivo, String descricao, String tipo) {
        this.idmotivo = idmotivo;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public MotivoMovimentacaoModel(String descricao, String tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public MotivoMovimentacaoModel() {
        motivoMovimentacaoDAO = new MotivoMovimentacaoDAO();
    }

    public Integer getIdmotivo() {
        return idmotivo;
    }

    public void setIdmotivo(Integer idmotivo) {
        this.idmotivo = idmotivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public MotivoMovimentacaoDAO getMotivoMovimentacaoDAO() {
        return motivoMovimentacaoDAO;
    }
}

