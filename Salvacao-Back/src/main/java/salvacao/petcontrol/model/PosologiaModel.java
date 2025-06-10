package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.PosologiaDAO;

@Repository
public class PosologiaModel {
    private String dose;
    private Integer quantidadedias;
    private Integer intervalohoras;
    private Integer medicamento_idproduto;
    private Integer receitamedicamento_idreceita;
    private PosologiaDAO posologiaDAO;

    public PosologiaModel() {
        posologiaDAO = new PosologiaDAO();
    }

    public PosologiaModel(String dose, Integer quantidadedias, Integer intervalohoras, Integer medicamento_idproduto, Integer receitamedicamento_idreceita) {
        this.dose = dose;
        this.quantidadedias = quantidadedias;
        this.intervalohoras = intervalohoras;
        this.medicamento_idproduto = medicamento_idproduto;
        this.receitamedicamento_idreceita = receitamedicamento_idreceita;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public Integer getQuantidadedias() {
        return quantidadedias;
    }

    public void setQuantidadedias(Integer quantidadedias) {
        this.quantidadedias = quantidadedias;
    }

    public Integer getIntervalohoras() {
        return intervalohoras;
    }

    public void setIntervalohoras(Integer intervalohoras) {
        this.intervalohoras = intervalohoras;
    }

    public Integer getMedicamento_idproduto() {
        return medicamento_idproduto;
    }

    public void setMedicamento_idproduto(Integer medicamento_idproduto) {
        this.medicamento_idproduto = medicamento_idproduto;
    }

    public Integer getReceitamedicamento_idreceita() {
        return receitamedicamento_idreceita;
    }

    public void setReceitamedicamento_idreceita(Integer receitamedicamento_idreceita) {
        this.receitamedicamento_idreceita = receitamedicamento_idreceita;
    }

    public PosologiaDAO getPosologiaDAO() {
        return posologiaDAO;
    }
}