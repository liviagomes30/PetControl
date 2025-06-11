package salvacao.petcontrol.model;

import java.time.LocalDate;

public class RegistroModel {
    private Integer id;
    private String usuario;
    private LocalDate data;
    private String observacao;

    public RegistroModel(Integer id, String usuario, LocalDate data, String observacao) {
        this.id = id;
        this.usuario = usuario;
        this.data = data;
        this.observacao = observacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getOboservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
