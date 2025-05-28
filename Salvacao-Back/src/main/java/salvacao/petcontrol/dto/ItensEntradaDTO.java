package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.ItensModel;

import java.time.LocalDate;
import java.util.List;

public class ItensEntradaDTO {
    private Integer id;
    private String usuario;
    private LocalDate data;
    private String observacao;
    private List<ItensModel> itens;

    public ItensEntradaDTO(Integer id, String usuario, LocalDate data, String observacao, List<ItensModel> itens) {
        this.id = id;
        this.usuario = usuario;
        this.data = data;
        this.observacao = observacao;
        this.itens = itens;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItensModel> getItens() {
        return itens;
    }

    public void setItens(List<ItensModel> itens) {
        this.itens = itens;
    }
}
