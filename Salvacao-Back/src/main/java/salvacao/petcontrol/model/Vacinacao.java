package salvacao.petcontrol.model;

import java.sql.Date; // Importante usar java.sql.Date para compatibilidade com JDBC para colunas DATE

public class Vacinacao {

    private Integer idVacinacao;        // Mapeia para id_vacinacao
    private Integer idVacina;           // Mapeia para id_vacina (FK para o tipo de vacina)
    private Integer idAnimal;           // Mapeia para id_animal (FK para o animal)
    private Date dataVacinacao;         // Mapeia para data_vacinacao
    private String localVacinacao;      // Mapeia para local_vacinacao
    private String loteVacina;          // Mapeia para lote_vacina
    private Date dataValidadeVacina;    // Mapeia para data_validade_vacina
    private String laboratorioVacina;   // Mapeia para laboratorio_vacina

    // Construtor padrão
    public Vacinacao() {
    }

    // Construtor completo (útil para DAOs)
    public Vacinacao(Integer idVacinacao, Integer idVacina, Integer idAnimal, Date dataVacinacao, String localVacinacao, String loteVacina, Date dataValidadeVacina, String laboratorioVacina) {
        this.idVacinacao = idVacinacao;
        this.idVacina = idVacina;
        this.idAnimal = idAnimal;
        this.dataVacinacao = dataVacinacao;
        this.localVacinacao = localVacinacao;
        this.loteVacina = loteVacina;
        this.dataValidadeVacina = dataValidadeVacina;
        this.laboratorioVacina = laboratorioVacina;
    }

    // Construtor para criação (sem idVacinacao, pois é auto-gerado)
    public Vacinacao(Integer idVacina, Integer idAnimal, Date dataVacinacao, String localVacinacao, String loteVacina, Date dataValidadeVacina, String laboratorioVacina) {
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

    @Override
    public String toString() {
        return "Vacinacao{" +
                "idVacinacao=" + idVacinacao +
                ", idVacina=" + idVacina +
                ", idAnimal=" + idAnimal +
                ", dataVacinacao=" + dataVacinacao +
                ", localVacinacao='" + localVacinacao + '\'' +
                ", loteVacina='" + loteVacina + '\'' +
                ", dataValidadeVacina=" + dataValidadeVacina +
                ", laboratorioVacina='" + laboratorioVacina + '\'' +
                '}';
    }
}