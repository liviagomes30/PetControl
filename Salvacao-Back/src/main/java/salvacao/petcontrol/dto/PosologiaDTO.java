package salvacao.petcontrol.dto;

public class PosologiaDTO {
    private String dose;
    private Integer quantidadedias;
    private Integer intervalohoras;
    private Integer medicamento_idproduto;
    private String medicamentoNome;
    private String medicamentoComposicao;
    private Integer receitamedicamento_idreceita;

    public PosologiaDTO() {
    }

    public PosologiaDTO(String dose, Integer quantidadedias, Integer intervalohoras, 
                       Integer medicamento_idproduto, Integer receitamedicamento_idreceita) {
        this.dose = dose;
        this.quantidadedias = quantidadedias;
        this.intervalohoras = intervalohoras;
        this.medicamento_idproduto = medicamento_idproduto;
        this.receitamedicamento_idreceita = receitamedicamento_idreceita;
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

    public String getMedicamentoNome() {
        return medicamentoNome;
    }

    public void setMedicamentoNome(String medicamentoNome) {
        this.medicamentoNome = medicamentoNome;
    }

    public String getMedicamentoComposicao() {
        return medicamentoComposicao;
    }

    public void setMedicamentoComposicao(String medicamentoComposicao) {
        this.medicamentoComposicao = medicamentoComposicao;
    }

    public Integer getReceitamedicamento_idreceita() {
        return receitamedicamento_idreceita;
    }

    public void setReceitamedicamento_idreceita(Integer receitamedicamento_idreceita) {
        this.receitamedicamento_idreceita = receitamedicamento_idreceita;
    }
} 