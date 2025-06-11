package salvacao.petcontrol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.AdocaoDAL;
import java.time.LocalDate;

@Repository
public class AdocaoModel {
    private int idAdocao;
    private int idAdotante;
    private int idAnimal;
    private LocalDate dataAdocao;
    private int pessoaIdPessoa;
    private String obs;
    private String statusAcompanhamento;
    private LocalDate dataAcompanhamento;
    private AdocaoDAL adocaoDAL;

    // Construtor vazio
    public AdocaoModel() {
        adocaoDAL = new AdocaoDAL();
    }

    // Construtor com todos os campos
    public AdocaoModel(int idAdocao, int idAdotante, int idAnimal, LocalDate dataAdocao,
                       int pessoaIdPessoa, String obs, String statusAcompanhamento, LocalDate dataAcompanhamento) {
        this.idAdocao = idAdocao;
        this.idAdotante = idAdotante;
        this.idAnimal = idAnimal;
        this.dataAdocao = dataAdocao;
        this.pessoaIdPessoa = pessoaIdPessoa;
        this.obs = obs;
        this.statusAcompanhamento = statusAcompanhamento;
        this.dataAcompanhamento = dataAcompanhamento;
        this.adocaoDAL = new AdocaoDAL();
    }

    // Método para acessar o DAO
    @JsonIgnore
    public AdocaoDAL getDAL() {
        if (adocaoDAL == null) {
            adocaoDAL = new AdocaoDAL();
        }
        return adocaoDAL;
    }

    public int getIdAdocao() {
        return idAdocao;
    }

    public void setIdAdocao(int idAdocao) {
        this.idAdocao = idAdocao;
    }

    public int getIdAdotante() {
        return idAdotante;
    }

    public void setIdAdotante(int idAdotante) {
        this.idAdotante = idAdotante;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public LocalDate getDataAdocao() {
        return dataAdocao;
    }

    public void setDataAdocao(String dataAdocao) {
        this.dataAdocao = (dataAdocao != null) ? LocalDate.parse(dataAdocao) : null;
    }

    public int getPessoaIdPessoa() {
        return pessoaIdPessoa;
    }

    public void setPessoaIdPessoa(int pessoaIdPessoa) {
        this.pessoaIdPessoa = pessoaIdPessoa;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getStatusAcompanhamento() {
        return statusAcompanhamento;
    }

    public void setStatusAcompanhamento(String statusAcompanhamento) {
        this.statusAcompanhamento = statusAcompanhamento;
    }

    public LocalDate getDataAcompanhamento() {
        return dataAcompanhamento;
    }

    public void setDataAcompanhamento(LocalDate dataAcompanhamento) {
        this.dataAcompanhamento = dataAcompanhamento;
    }
}