package salvacao.petcontrol.model;

import java.time.LocalDate;

public class EventoModel {
    private int idEvento;
    private String descricao;
    private LocalDate data;
    private String foto;
    private int animalIdAnimal;
    private String local;
    private String responsavel;
    private String status;

    // Construtor vazio
    public EventoModel() {
    }

    // Construtor com todos os campos
    public EventoModel(int idEvento, String descricao, LocalDate data, String foto,
                       int animalIdAnimal, String local, String responsavel, String status) {
        this.idEvento = idEvento;
        this.descricao = descricao;
        this.data = data;
        this.foto = foto;
        this.animalIdAnimal = animalIdAnimal;
        this.local = local;
        this.responsavel = responsavel;
        this.status = status;
    }

    // Getters e Setters
    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(String data) {
        this.data = (data != null) ? LocalDate.parse(data) : null;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getAnimalIdAnimal() {
        return animalIdAnimal;
    }

    public void setAnimalIdAnimal(int animalIdAnimal) {
        this.animalIdAnimal = animalIdAnimal;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventoModel{" +
                "idEvento=" + idEvento +
                ", descricao='" + descricao + '\'' +
                ", data=" + data +
                ", foto='" + foto + '\'' +
                ", animalIdAnimal=" + animalIdAnimal +
                ", local='" + local + '\'' +
                ", responsavel='" + responsavel + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}