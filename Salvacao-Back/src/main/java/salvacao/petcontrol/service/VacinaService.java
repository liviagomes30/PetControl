package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.VacinaDAO;
import salvacao.petcontrol.dto.VacinaDTO;
import salvacao.petcontrol.model.Vacina;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacinaService {

    private final VacinaDAO vacinaDAO;

    @Autowired
    public VacinaService(VacinaDAO vacinaDAO) {
        this.vacinaDAO = vacinaDAO;
    }

    public VacinaDTO cadastrarVacina(VacinaDTO vacinaDTO) throws Exception {
        if (vacinaDTO == null || vacinaDTO.getDescricaoVacina() == null || vacinaDTO.getDescricaoVacina().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da vacina não pode ser nula ou vazia.");
        }

        Vacina vacinaModel = new Vacina();
        vacinaModel.setDescricaoVacina(vacinaDTO.getDescricaoVacina());

        Vacina vacinaCadastrada = vacinaDAO.cadastrar(vacinaModel);

        if (vacinaCadastrada == null || vacinaCadastrada.getIdVacina() == null) {
            throw new Exception("Falha ao cadastrar a vacina no banco de dados.");
        }

        return new VacinaDTO(vacinaCadastrada.getIdVacina(), vacinaCadastrada.getDescricaoVacina());
    }

    public VacinaDTO buscarVacinaPorId(Integer id) {
        Vacina vacinaModel = vacinaDAO.buscarPorId(id);
        return vacinaModel != null ? new VacinaDTO(vacinaModel.getIdVacina(), vacinaModel.getDescricaoVacina()) : null;
    }

    public List<VacinaDTO> listarTodasVacinas() {
        return vacinaDAO.listarTodas().stream()
                .map(vacina -> new VacinaDTO(vacina.getIdVacina(), vacina.getDescricaoVacina()))
                .collect(Collectors.toList());
    }

    public List<VacinaDTO> buscarPorDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return List.of();
        }
        return vacinaDAO.buscarPorDescricao(descricao).stream()
                .map(vacina -> new VacinaDTO(vacina.getIdVacina(), vacina.getDescricaoVacina()))
                .collect(Collectors.toList());
    }
}