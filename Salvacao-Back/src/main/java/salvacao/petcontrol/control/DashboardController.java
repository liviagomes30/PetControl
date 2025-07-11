package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import salvacao.petcontrol.dto.CompromissoMedicacaoDTO;
import salvacao.petcontrol.service.MedicacaoService;
import salvacao.petcontrol.service.ReceitaMedicamentoService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ReceitaMedicamentoService receitaService;

    @Autowired
    private MedicacaoService medicacaoService;

    @GetMapping("/compromissos-medicacao")
    public ResponseEntity<List<CompromissoMedicacaoDTO>> getCompromissosDeMedicacao() {
        try {
            List<CompromissoMedicacaoDTO> aplicadas = medicacaoService.listarComoCompromissos();

            List<CompromissoMedicacaoDTO> planejadas = receitaService.gerarAplicacoesFuturas();

            List<CompromissoMedicacaoDTO> todosOsCompromissos = new ArrayList<>();
            todosOsCompromissos.addAll(aplicadas);
            todosOsCompromissos.addAll(planejadas);

            todosOsCompromissos.sort(Comparator.comparing(CompromissoMedicacaoDTO::getDataHora));

            return ResponseEntity.ok(todosOsCompromissos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}