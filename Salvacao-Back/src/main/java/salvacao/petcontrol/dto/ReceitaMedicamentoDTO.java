package salvacao.petcontrol.dto;

import java.time.LocalDate;
import java.util.ArrayList; // 1. IMPORTAR a classe ArrayList
import java.util.List;

public class ReceitaMedicamentoDTO {
    private Integer idreceita;
    private LocalDate data;
    private String medico;
    private String clinica;
    private Integer animal_idanimal;
    private String animalNome;
    private List<PosologiaDTO> posologias;

    public ReceitaMedicamentoDTO() {
        this.posologias = new ArrayList<>();
    }

    public ReceitaMedicamentoDTO(Integer idreceita, LocalDate data, String medico, String clinica, Integer animal_idanimal) {
        this.idreceita = idreceita;
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animal_idanimal;
        this.posologias = new ArrayList<>();
    }

    // Getters e Setters (sem alteração)
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

    public String getAnimalNome() {
        return animalNome;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public List<PosologiaDTO> getPosologias() {
        return posologias;
    }

    public void setPosologias(List<PosologiaDTO> posologias) {
        this.posologias = posologias;
    }
}