package salvacao.petcontrol.dto;

import java.time.LocalDateTime;

public class CompromissoMedicacaoDTO {
    private String animalNome;
    private String medicamentoNome;
    private LocalDateTime dataHora;
    private String status;
    private long posologiaId;


    public CompromissoMedicacaoDTO(String animalNome, String medicamentoNome, LocalDateTime dataHora, String status, long posologiaId) {
        this.animalNome = animalNome;
        this.medicamentoNome = medicamentoNome;
        this.dataHora = dataHora;
        this.status = status;
        this.posologiaId = posologiaId;
    }

    public String getAnimalNome() {
        return animalNome;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public String getMedicamentoNome() {
        return medicamentoNome;
    }

    public void setMedicamentoNome(String medicamentoNome) {
        this.medicamentoNome = medicamentoNome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPosologiaId() {
        return posologiaId;
    }

    public void setPosologiaId(long posologiaId) {
        this.posologiaId = posologiaId;
    }
}