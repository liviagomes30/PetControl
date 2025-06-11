package salvacao.petcontrol.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;

import java.util.Date; // Usar java.util.Date para DTOs é comum com Jackson, mas pode converter para java.sql.Date no Service

public class VacinacaoDTO {

    @JsonProperty("id_vacinacao")
    private Integer idVacinacao;

    @JsonProperty("id_vacina") // FK para o tipo de vacina
    private Integer idVacina;

    @JsonProperty("id_animal") // FK para o animal
    private Integer idAnimal;

    @JsonProperty("data_vacinacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Define o formato esperado da data do JSON
    private Date dataVacinacao;

    @JsonProperty("local_vacinacao")
    private String localVacinacao;

    @JsonProperty("lote_vacina")
    private String loteVacina;

    @JsonProperty("data_validade_vacina")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", lenient = OptBoolean.TRUE) // Lenient para aceitar null/vazio
    private Date dataValidadeVacina;

    @JsonProperty("laboratorio_vacina")
    private String laboratorioVacina;

    // Opcional: Campos para exibir informações relacionadas (ex: nome do animal, descrição da vacina)
    // Estes seriam populados apenas na resposta, não na requisição de cadastro.
    // @JsonProperty("nome_animal")
    // private String nomeAnimal;
    // @JsonProperty("descricao_tipo_vacina")
    // private String descricaoTipoVacina;


    // Construtor padrão
    public VacinacaoDTO() {
    }

    // Construtor para facilitar a criação (exemplo)
    public VacinacaoDTO(Integer idVacinacao, Integer idVacina, Integer idAnimal, Date dataVacinacao, String localVacinacao, String loteVacina, Date dataValidadeVacina, String laboratorioVacina) {
        this.idVacinacao = idVacinacao;
        this.idVacina = idVacina;
        this.idAnimal = idAnimal;
        this.dataVacinacao = dataVacinacao;
        this.localVacinacao = localVacinacao;
        this.loteVacina = loteVacina;
        this.dataValidadeVacina = dataValidadeVacina;
        this.laboratorioVacina = laboratorioVacina;
    }


    // Getters e Setters
    public Integer getIdVacinacao() {
        return idVacinacao;
    }

    public void setIdVacinacao(Integer idVacinacao) {
        this.idVacinacao = idVacinacao;
    }

    public Integer getIdVacina() {
        return idVacina;
    }

    public void setIdVacina(Integer idVacina) {
        this.idVacina = idVacina;
    }

    public Integer getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(Integer idAnimal) {
        this.idAnimal = idAnimal;
    }

    public Date getDataVacinacao() {
        return dataVacinacao;
    }

    public void setDataVacinacao(Date dataVacinacao) {
        this.dataVacinacao = dataVacinacao;
    }

    public String getLocalVacinacao() {
        return localVacinacao;
    }

    public void setLocalVacinacao(String localVacinacao) {
        this.localVacinacao = localVacinacao;
    }

    public String getLoteVacina() {
        return loteVacina;
    }

    public void setLoteVacina(String loteVacina) {
        this.loteVacina = loteVacina;
    }

    public Date getDataValidadeVacina() {
        return dataValidadeVacina;
    }

    public void setDataValidadeVacina(Date dataValidadeVacina) {
        this.dataValidadeVacina = dataValidadeVacina;
    }

    public String getLaboratorioVacina() {
        return laboratorioVacina;
    }

    public void setLaboratorioVacina(String laboratorioVacina) {
        this.laboratorioVacina = laboratorioVacina;
    }

    // public String getNomeAnimal() {
    //     return nomeAnimal;
    // }

    // public void setNomeAnimal(String nomeAnimal) {
    //     this.nomeAnimal = nomeAnimal;
    // }

    // public String getDescricaoTipoVacina() {
    //     return descricaoTipoVacina;
    // }

    // public void setDescricaoTipoVacina(String descricaoTipoVacina) {
    //     this.descricaoTipoVacina = descricaoTipoVacina;
    // }
}