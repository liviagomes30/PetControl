package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.HistoricoModel;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;

public class MedicacaoCompletaDTO {
    private MedicacaoModel medicacao;
    private AnimalModel animal;
    private MedicamentoModel medicamento;
    private ReceitaMedicamentoModel receita;
    private PosologiaModel posologia;
    private HistoricoModel historico;

    public MedicacaoCompletaDTO() {
    }

    public MedicacaoCompletaDTO(MedicacaoModel medicacao, AnimalModel animal,
                                MedicamentoModel medicamento, ReceitaMedicamentoModel receita,
                                PosologiaModel posologia, HistoricoModel historico) {
        this.medicacao = medicacao;
        this.animal = animal;
        this.medicamento = medicamento;
        this.receita = receita;
        this.posologia = posologia;
        this.historico = historico;
    }

    // Getters e Setters
    public MedicacaoModel getMedicacao() {
        return medicacao;
    }

    public void setMedicacao(MedicacaoModel medicacao) {
        this.medicacao = medicacao;
    }

    public AnimalModel getAnimal() {
        return animal;
    }

    public void setAnimal(AnimalModel animal) {
        this.animal = animal;
    }

    public MedicamentoModel getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoModel medicamento) {
        this.medicamento = medicamento;
    }

    public ReceitaMedicamentoModel getReceita() {
        return receita;
    }

    public void setReceita(ReceitaMedicamentoModel receita) {
        this.receita = receita;
    }

    public PosologiaModel getPosologia() {
        return posologia;
    }

    public void setPosologia(PosologiaModel posologia) {
        this.posologia = posologia;
    }

    public HistoricoModel getHistorico() {
        return historico;
    }

    public void setHistorico(HistoricoModel historico) {
        this.historico = historico;
    }
}