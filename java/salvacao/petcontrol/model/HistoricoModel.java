package salvacao.petcontrol.model;

import org.springframework.stereotype.Repository;
import salvacao.petcontrol.dao.HistoricoDAO;

import java.time.LocalDate;

@Repository
public class HistoricoModel {
    private Integer idhistorico;
    private String descricao;
    private LocalDate data;
    private Integer animal_idanimal;
    private Integer vacinacao_idvacinacao;
    private Integer medicacao_idmedicacao;
    private HistoricoDAO histDAO;


    public HistoricoModel() {
        histDAO = new HistoricoDAO();
    }

    public HistoricoModel(Integer idhistorico, String descricao, LocalDate data, Integer animal_idanimal, Integer vacinacao_idvacinacao, Integer medicacao_idmedicacao) {
        this.idhistorico = idhistorico;
        this.descricao = descricao;
        this.data = data;
        this.animal_idanimal = animal_idanimal;
        this.vacinacao_idvacinacao = vacinacao_idvacinacao;
        this.medicacao_idmedicacao = medicacao_idmedicacao;
    }

    public Integer getIdhistorico() {
        return idhistorico;
    }

    public void setIdhistorico(Integer idhistorico) {
        this.idhistorico = idhistorico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getAnimal_idanimal() {
        return animal_idanimal;
    }

    public void setAnimal_idanimal(Integer animal_idanimal) {
        this.animal_idanimal = animal_idanimal;
    }

    public Integer getVacinacao_idvacinacao() {
        return vacinacao_idvacinacao;
    }

    public void setVacinacao_idvacinacao(Integer vacinacao_idvacinacao) {
        this.vacinacao_idvacinacao = vacinacao_idvacinacao;
    }

    public Integer getMedicacao_idmedicacao() {
        return medicacao_idmedicacao;
    }

    public void setMedicacao_idmedicacao(Integer medicacao_idmedicacao) {
        this.medicacao_idmedicacao = medicacao_idmedicacao;
    }

    public HistoricoDAO getHistDAO() {
        return histDAO;
    }
}