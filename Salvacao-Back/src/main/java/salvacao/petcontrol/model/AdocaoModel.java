package salvacao.petcontrol.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.AdocaoDAL;

import java.time.LocalDate;

@Repository
public class AdocaoModel {

    private int idAdocao;
    private int idAdotante;
    private int idAnimal;
    private AdocaoDAL adocaoDAL;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataAdocao;

    private int pessoaIdPessoa;
    private String obs;
    private String statusAcompanhamento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataAcompanhamento;

    public AdocaoModel() {
        adocaoDAL = new AdocaoDAL();
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

    public void setDataAdocao(LocalDate dataAdocao) {
        this.dataAdocao = dataAdocao;
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

    @JsonIgnore
    public AdocaoDAL getDAL() {
        return adocaoDAL;
    }
}
