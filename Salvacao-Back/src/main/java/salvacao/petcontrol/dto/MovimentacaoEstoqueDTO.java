package salvacao.petcontrol.dto;

import java.time.LocalDate;

public class MovimentacaoEstoqueDTO {
    private String usuNome;
    private String proNome;
    private String obs;
    private int qtde;
    private String fornecedor;
    private LocalDate data;

    public MovimentacaoEstoqueDTO(String usuNome, String proNome, String obs, int qtde, String fornecedor, LocalDate data) {
        this.usuNome = usuNome;
        this.proNome = proNome;
        this.obs = obs;
        this.qtde = qtde;
        this.fornecedor = fornecedor;
        this.data = data;
    }

    public MovimentacaoEstoqueDTO() {
    }

    public String getUsuNome() {
        return usuNome;
    }

    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }

    public String getProNome() {
        return proNome;
    }

    public void setProNome(String proNome) {
        this.proNome = proNome;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public int getQtde() {
        return qtde;
    }

    public void setQtde(int qtde) {
        this.qtde = qtde;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
