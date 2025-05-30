package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.MedicacaoDAO; // Import MedicacaoDAO

import java.time.LocalDate;

@Repository
public class MedicacaoModel {
    private Integer idmedicacao;
    private Integer idanimal;
    private Integer idhistorico;
    private Integer posologia_medicamento_idproduto;
    private Integer posologia_receitamedicamento_idreceita;
    private LocalDate data;

    private MedicacaoDAO medicacaoDAO;

    public MedicacaoModel() {
        this.medicacaoDAO = new MedicacaoDAO();
    }

    public MedicacaoModel(Integer idmedicacao, Integer idanimal, Integer idhistorico, Integer posologia_medicamento_idproduto, Integer posologia_receitamedicamento_idreceita, LocalDate data) {
        this.idmedicacao = idmedicacao;
        this.idanimal = idanimal;
        this.idhistorico = idhistorico;
        this.posologia_medicamento_idproduto = posologia_medicamento_idproduto;
        this.posologia_receitamedicamento_idreceita = posologia_receitamedicamento_idreceita;
        this.data = data;
    }

    public Integer getIdmedicacao() {
        return idmedicacao;
    }

    public void setIdmedicacao(Integer idmedicacao) {
        this.idmedicacao = idmedicacao;
    }

    public Integer getIdanimal() {
        return idanimal;
    }

    public void setIdanimal(Integer idanimal) {
        this.idanimal = idanimal;
    }

    public Integer getIdhistorico() {
        return idhistorico;
    }

    public void setIdhistorico(Integer idhistorico) {
        this.idhistorico = idhistorico;
    }

    public Integer getPosologia_medicamento_idproduto() {
        return posologia_medicamento_idproduto;
    }

    public void setPosologia_medicamento_idproduto(Integer posologia_medicamento_idproduto) {
        this.posologia_medicamento_idproduto = posologia_medicamento_idproduto;
    }

    public Integer getPosologia_receitamedicamento_idreceita() {
        return posologia_receitamedicamento_idreceita;
    }

    public void setPosologia_receitamedicamento_idreceita(Integer posologia_receitamedicamento_idreceita) {
        this.posologia_receitamedicamento_idreceita = posologia_receitamedicamento_idreceita;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public MedicacaoDAO getMedDAO() {
        return medicacaoDAO;
    }
}