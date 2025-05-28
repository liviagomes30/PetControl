package salvacao.petcontrol.model;

import java.time.LocalDate;
import java.util.List;

public class EntradaProdutoModel {
    private Integer usuarioId;
    private String observacao;
    private LocalDate data;
    private List<ItensEntrada> itens;

    public EntradaProdutoModel(Integer usuarioId, String observacao, LocalDate data, List<ItensEntrada> itens) {
        this.usuarioId = usuarioId;
        this.observacao = observacao;
        this.data = data;
        this.itens = itens;
    }

    public EntradaProdutoModel() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<ItensEntrada> getItens() {
        return itens;
    }

    public void setItens(List<ItensEntrada> itens) {
        this.itens = itens;
    }
}
