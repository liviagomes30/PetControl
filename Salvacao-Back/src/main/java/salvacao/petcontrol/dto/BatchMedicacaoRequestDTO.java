package salvacao.petcontrol.dto;

import java.time.LocalDate;
import java.util.List;

public class BatchMedicacaoRequestDTO {
    private Integer idAnimal;
    private Integer idReceitaMedicamento; // Pode ser nulo
    private LocalDate dataMedicao;
    private List<ItemMedicacaoRequestDTO> medicacoes;

    // Getters e Setters
    public Integer getIdAnimal() { return idAnimal; }
    public void setIdAnimal(Integer idAnimal) { this.idAnimal = idAnimal; }
    public Integer getIdReceitaMedicamento() { return idReceitaMedicamento; }
    public void setIdReceitaMedicamento(Integer idReceitaMedicamento) { this.idReceitaMedicamento = idReceitaMedicamento; }
    public LocalDate getDataMedicao() { return dataMedicao; }
    public void setDataMedicao(LocalDate dataMedicao) { this.dataMedicao = dataMedicao; }
    public List<ItemMedicacaoRequestDTO> getMedicacoes() { return medicacoes; }
    public void setMedicacoes(List<ItemMedicacaoRequestDTO> medicacoes) { this.medicacoes = medicacoes; }
}