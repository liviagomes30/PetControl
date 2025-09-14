package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.PreferenciaPorteDAO;

@Repository
public class PreferenciaPorteModel {
    private Integer idpreferencia;
    private Integer idpessoa;
    private Integer idporte;
    private PreferenciaPorteDAO preferenciaPorteDAO;

    public PreferenciaPorteModel() {
        preferenciaPorteDAO = new PreferenciaPorteDAO();
    }

    public PreferenciaPorteModel(Integer idpreferencia, Integer idpessoa, Integer idporte) {
        this.idpreferencia = idpreferencia;
        this.idpessoa = idpessoa;
        this.idporte = idporte;
    }

    public Integer getIdpreferencia() {
        return idpreferencia;
    }

    public void setIdpreferencia(Integer idpreferencia) {
        this.idpreferencia = idpreferencia;
    }

    public Integer getIdpessoa() {
        return idpessoa;
    }

    public void setIdpessoa(Integer idpessoa) {
        this.idpessoa = idpessoa;
    }

    public Integer getIdporte() {
        return idporte;
    }

    public void setIdporte(Integer idporte) {
        this.idporte = idporte;
    }

    public PreferenciaPorteDAO getPreferenciaPorteDAO() {
        return preferenciaPorteDAO;
    }

}
