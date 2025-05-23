package salvacao.petcontrol.model;

import java.time.LocalDateTime;

public class EventoModal {
    private Integer idevento;
    private String descricao;
    private LocalDateTime data;
    private String foto;
    private Integer animalId;
    private String local;
    private String responsavel;
    private boolean status;

    // Construtores
    public EventoModal() {
    }

    public EventoModal(Integer idEvento, String descricao, LocalDateTime data, String local, Integer animalId, String foto) {
        this.idevento = idEvento;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.animalId = animalId;
        this.foto = foto;
    }

    // Getters e Setters
    public Integer getIdEvento() {
        return idevento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idevento = idEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getIdevento() {
        return idevento;
    }

    public void setIdevento(Integer idevento) {
        this.idevento = idevento;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventoModel{" +
                "idEvento=" + idevento +
                ", descricao='" + descricao + '\'' +
                ", data=" + data +
                ", local='" + local + '\'' +
                ", animalId=" + animalId +
                ", foto='" + foto + '\'' +
                '}';
    }
}