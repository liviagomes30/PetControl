package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.AcertoEstoqueDAO;

import java.time.LocalDate;

@Repository
public class AcertoEstoqueModel {
    private Integer idacerto;
    private LocalDate data;
    private Integer usuario_pessoa_id;
    private String motivo;
    private String observacao;
    private AcertoEstoqueDAO acDAO;

    public AcertoEstoqueModel() {
        acDAO = new AcertoEstoqueDAO();
    }

    public AcertoEstoqueModel(Integer idacerto, LocalDate data, Integer usuario_pessoa_id, String motivo, String observacao) {
        this.idacerto = idacerto;
        this.data = data;
        this.usuario_pessoa_id = usuario_pessoa_id;
        this.motivo = motivo;
        this.observacao = observacao;
    }

    public Integer getIdacerto() {
        return idacerto;
    }

    public void setIdacerto(Integer idacerto) {
        this.idacerto = idacerto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getUsuario_pessoa_id() {
        return usuario_pessoa_id;
    }

    public void setUsuario_pessoa_id(Integer usuario_pessoa_id) {
        this.usuario_pessoa_id = usuario_pessoa_id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public AcertoEstoqueDAO getAcDAO() {
        return acDAO;
    }
}