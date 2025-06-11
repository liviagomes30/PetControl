package salvacao.petcontrol.model;

// Não são necessárias anotações JPA se não estiver usando Spring Data JPA
// import jakarta.persistence.*; // Exemplo se fosse usar JPA

public class Vacina {

    private Integer idVacina; // Corresponde a id_vacina no banco
    private String descricaoVacina; // Corresponde a descricao_vacina no banco

    // Construtor padrão
    public Vacina() {
    }

    // Construtor com todos os campos
    public Vacina(Integer idVacina, String descricaoVacina) {
        this.idVacina = idVacina;
        this.descricaoVacina = descricaoVacina;
    }

    // Construtor sem ID (para criação)
    public Vacina(String descricaoVacina) {
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

    @Override
    public String toString() {
        return "Vacina{" +
                "idVacina=" + idVacina +
                ", descricaoVacina='" + descricaoVacina + '\'' +
                '}';
    }
}