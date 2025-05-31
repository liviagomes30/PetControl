package salvacao.petcontrol.dto;

import java.time.LocalDate;
import java.util.List;

public class ReceitaMedicamentoRequestDTO {
    private LocalDate data;
    private String medico;
    private String clinica;
    private Integer animal_idanimal;
    private List<PosologiaRequestDTO> posologias;

    public static class PosologiaRequestDTO {
        private String dose;
        private Integer quantidadedias;
        private Integer intervalohoras;
        private Integer medicamento_idproduto;

        public PosologiaRequestDTO() {
        }

        public PosologiaRequestDTO(String dose, Integer quantidadedias, Integer intervalohoras, Integer medicamento_idproduto) {
            this.dose = dose;
            this.quantidadedias = quantidadedias;
            this.intervalohoras = intervalohoras;
            this.medicamento_idproduto = medicamento_idproduto;
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
    }

    public ReceitaMedicamentoRequestDTO() {
    }

    public ReceitaMedicamentoRequestDTO(LocalDate data, String medico, String clinica, Integer animal_idanimal, List<PosologiaRequestDTO> posologias) {
        this.data = data;
        this.medico = medico;
        this.clinica = clinica;
        this.animal_idanimal = animal_idanimal;
        this.posologias = posologias;
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

    public List<PosologiaRequestDTO> getPosologias() {
        return posologias;
    }

    public void setPosologias(List<PosologiaRequestDTO> posologias) {
        this.posologias = posologias;
    }
} 