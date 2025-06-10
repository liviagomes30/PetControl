package salvacao.petcontrol.dto;
import java.math.BigDecimal;

public class ItemMedicacaoRequestDTO {
    private Integer idMedicamentoProduto;
    private BigDecimal quantidadeAdministrada;
    private String descricaoHistorico;

    // Getters e Setters
    public Integer getIdMedicamentoProduto() { return idMedicamentoProduto; }
    public void setIdMedicamentoProduto(Integer idMedicamentoProduto) { this.idMedicamentoProduto = idMedicamentoProduto; }
    public BigDecimal getQuantidadeAdministrada() { return quantidadeAdministrada; }
    public void setQuantidadeAdministrada(BigDecimal quantidadeAdministrada) { this.quantidadeAdministrada = quantidadeAdministrada; }
    public String getDescricaoHistorico() { return descricaoHistorico; }
    public void setDescricaoHistorico(String descricaoHistorico) { this.descricaoHistorico = descricaoHistorico; }
}