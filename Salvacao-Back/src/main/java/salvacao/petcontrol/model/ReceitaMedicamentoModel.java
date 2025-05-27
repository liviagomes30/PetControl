package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.ReceitaMedicamentoDAO; // Import ReceitaMedicamentoDAO

import java.time.LocalDate;

@Repository
public class ReceitaMedicamentoModel {
    private Integer idreceita;
    private LocalDate data;
    private String medico;
    private String clinica;
    private Integer animal_idanimal;

    private ReceitaMedicamentoDAO receitaMedicamentoDAO; // DAO instance

    public ReceitaMedicamentoModel() {
        this.receitaMedicamentoDAO = new ReceitaMedicamentoDAO(); // Instantiate DAO in default constructor
    }

    public ReceitaMedicamentoModel(Integer idreceita, LocalDate data, String medico, String clinica, Integer animal_idanimal) {
        this.idreceita = idreceita;
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animal_idanimal;
        this.receitaMedicamentoDAO = new ReceitaMedicamentoDAO(); // Instantiate DAO in parameterized constructor
    }

    // Getters and Setters
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

    // Getter for DAO
    public ReceitaMedicamentoDAO getRmDAO() {
        return receitaMedicamentoDAO;
    }
}