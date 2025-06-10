package salvacao.petcontrol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.ReceitaMedicamentoDAO;

import java.time.LocalDate;

@Repository
public class ReceitaMedicamentoModel {
    private Integer idreceita;
    private LocalDate data;
    private String medico;
    private String clinica;
    private Integer animal_idanimal;
    private String status;

    private ReceitaMedicamentoDAO receitaMedicamentoDAO;

    public ReceitaMedicamentoModel() {
        this.receitaMedicamentoDAO = new ReceitaMedicamentoDAO();
    }

    public ReceitaMedicamentoModel(Integer idreceita, LocalDate data, String medico, String clinica, Integer animal_idanimal, String status, ReceitaMedicamentoDAO receitaMedicamentoDAO) {
        this.idreceita = idreceita;
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animal_idanimal;
        this.status = status;
        this.receitaMedicamentoDAO = receitaMedicamentoDAO;
    }

    public ReceitaMedicamentoModel(int idreceita, LocalDate data, String medico, String clinica, int animalIdanimal, String status) {
        this.idreceita = idreceita;
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animalIdanimal;
        this.status = status;
    }

    public ReceitaMedicamentoModel(int idreceita, LocalDate data, String medico, String clinica, int animalIdanimal) {
        this.idreceita = idreceita;
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animalIdanimal;
    }

    public Integer getIdreceita() {
        return idreceita;
    }

    public void setIdreceita(Integer idreceita) {
        this.idreceita = idreceita;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getClinica() {
        return clinica;
    }

    public void setClinica(String clinica) {
        this.clinica = clinica;
    }

    public Integer getAnimal_idanimal() {
        return animal_idanimal;
    }

    public void setAnimal_idanimal(Integer animal_idanimal) {
        this.animal_idanimal = animal_idanimal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReceitaMedicamentoDAO getRmDAO() {
        return receitaMedicamentoDAO;
    }
}