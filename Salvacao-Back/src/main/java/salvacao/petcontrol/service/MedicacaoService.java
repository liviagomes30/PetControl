package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dalNÃOUSARMAIS.AnimalDAL;
import salvacao.petcontrol.dalNÃOUSARMAIS.MedicacaoDAL;
import salvacao.petcontrol.dalNÃOUSARMAIS.MedicamentoDAL;
import salvacao.petcontrol.dalNÃOUSARMAIS.ReceitaMedicamentoDAL;
import salvacao.petcontrol.dto.MedicacaoCompletaDTO;
import salvacao.petcontrol.model.MedicacaoModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicacaoService {

    @Autowired
    private MedicacaoDAL medicacaoDAL;

    @Autowired
    private AnimalDAL animalDAL;

    @Autowired
    private MedicamentoDAL medicamentoDAL;

    @Autowired
    private ReceitaMedicamentoDAL receitaMedicamentoDAL;

    public MedicacaoCompletaDTO getMedicacaoById(Integer id) {
        return medicacaoDAL.findMedicacaoCompleta(id);
    }

    public List<MedicacaoCompletaDTO> getMedicacoesPorAnimal(Integer idAnimal) {
        return medicacaoDAL.findMedicacoesPorAnimal(idAnimal);
    }

    public List<MedicacaoCompletaDTO> getMedicacoesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return medicacaoDAL.findMedicacoesPorData(dataInicio, dataFim);
    }

    public List<MedicacaoCompletaDTO> getAllMedicacoes() {
        return medicacaoDAL.findTodasMedicacoes();
    }

    public MedicacaoModel efetuarMedicacao(MedicacaoModel medicacao, String descricaoHistorico) throws Exception {
        if (medicacao.getIdanimal() == null) {
            throw new Exception("Animal é obrigatório");
        }

        if (medicacao.getPosologia_medicamento_idproduto() == null) {
            throw new Exception("Medicamento é obrigatório");
        }

        if (medicacao.getPosologia_receitamedicamento_idreceita() == null) {
            throw new Exception("Receita é obrigatória");
        }

        // Verificar se animal existe
        if (animalDAL.getId(medicacao.getIdanimal()) == null) {
            throw new Exception("Animal não encontrado");
        }

        // Verificar se medicamento existe
        if (medicamentoDAL.findById(medicacao.getPosologia_medicamento_idproduto()) == null) {
            throw new Exception("Medicamento não encontrado");
        }

//        if (receitaMedicamentoDAL.findById(medicacao.getPosologia_receitamedicamento_idreceita()) == null) {
//            throw new Exception("Receita não encontrada");
//        }

        if (medicacao.getData() == null) {
            medicacao.setData(LocalDate.now());
        }

        try {
            return medicacaoDAL.gravarMedicacao(medicacao, descricaoHistorico);
        } catch (SQLException e) {
            throw new Exception("Erro ao efetuar medicação: " + e.getMessage());
        }
    }
}