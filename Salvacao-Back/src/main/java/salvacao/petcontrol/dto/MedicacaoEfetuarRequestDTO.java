package salvacao.petcontrol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicacaoEfetuarRequestDTO {
    private Integer idAnimal;
    private Integer idMedicamentoProduto;
    private Integer idReceitaMedicamento; // Opcional
    private BigDecimal quantidadeAdministrada;
    private LocalDate dataMedicao; // Opcional
    private String descricaoHistorico; // Opcional

    // Construtor padrão
    public MedicacaoEfetuarRequestDTO() {}

    // Construtor com todos os campos (opcional, mas útil)
    public MedicacaoEfetuarRequestDTO(Integer idAnimal, Integer idMedicamentoProduto, Integer idReceitaMedicamento, BigDecimal quantidadeAdministrada, LocalDate dataMedicao, String descricaoHistorico) {
        this.idAnimal = idAnimal;
        this.idMedicamentoProduto = idMedicamentoProduto;
        this.idReceitaMedicamento = idReceitaMedicamento;
        this.quantidadeAdministrada = quantidadeAdministrada;
        this.dataMedicao = dataMedicao;
        this.descricaoHistorico = descricaoHistorico;
    }

    // Getters e Setters para todos os campos
    public Integer getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(Integer idAnimal) {
        this.idAnimal = idAnimal;
    }

    public Integer getIdMedicamentoProduto() {
        return idMedicamentoProduto;
    }

    public void setIdMedicamentoProduto(Integer idMedicamentoProduto) {
        this.idMedicamentoProduto = idMedicamentoProduto;
    }

    public Integer getIdReceitaMedicamento() {
        return idReceitaMedicamento;
    }

    public void setIdReceitaMedicamento(Integer idReceitaMedicamento) {
        this.idReceitaMedicamento = idReceitaMedicamento;
    }

    public BigDecimal getQuantidadeAdministrada() {
        return quantidadeAdministrada;
    }

    public void setQuantidadeAdministrada(BigDecimal quantidadeAdministrada) {
        this.quantidadeAdministrada = quantidadeAdministrada;
    }

    public LocalDate getDataMedicao() {
        return dataMedicao;
    }

    public void setDataMedicao(LocalDate dataMedicao) {
        this.dataMedicao = dataMedicao;
    }

    public String getDescricaoHistorico() {
        return descricaoHistorico;
    }

    public void setDescricaoHistorico(String descricaoHistorico) {
        this.descricaoHistorico = descricaoHistorico;
    }
}