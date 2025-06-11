package salvacao.petcontrol.dto;

// import jakarta.validation.constraints.NotBlank; // Exemplo se usar Bean Validation
// import jakarta.validation.constraints.Size;     // Exemplo se usar Bean Validation

import com.fasterxml.jackson.annotation.JsonProperty;

public class VacinaDTO {
    @JsonProperty("id_vacina")
    private Integer idVacina;

    // @NotBlank(message = "A descrição da vacina é obrigatória.") // Exemplo de validação
    // @Size(min = 2, max = 100, message = "A descrição deve ter entre 2 e 100 caracteres.") // Exemplo
    @JsonProperty("descricao_vacina")
    private String descricaoVacina;

    // Construtor padrão
    public VacinaDTO() {
    }

    // Construtor com todos os campos
    public VacinaDTO(Integer idVacina, String descricaoVacina) {
        this.idVacina = idVacina;
        this.descricaoVacina = descricaoVacina;
    }

    // Construtor sem ID (para requisições de criação)
    public VacinaDTO(String descricaoVacina) {
        this.descricaoVacina = descricaoVacina;
    }

    // Getters e Setters
    public Integer getIdVacina() {
        return idVacina;
    }

    public void setIdVacina(Integer idVacina) {
        this.idVacina = idVacina;
    }

    public String getDescricaoVacina() {
        return descricaoVacina;
    }

    public void setDescricaoVacina(String descricaoVacina) {
        this.descricaoVacina = descricaoVacina;
    }
}