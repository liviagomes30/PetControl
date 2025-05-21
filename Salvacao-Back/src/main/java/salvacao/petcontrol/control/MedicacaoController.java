package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.MedicacaoCompletaDTO;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.service.MedicacaoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/medicacoes")
public class MedicacaoController {

    @Autowired
    private MedicacaoService medicacaoService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMedicacaoById(@PathVariable Integer id) {
        MedicacaoCompletaDTO medicacao = medicacaoService.getMedicacaoById(id);
        if (medicacao != null) {
            return ResponseEntity.ok(medicacao);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicação não encontrada");
    }

    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<Object> getMedicacoesPorAnimal(@PathVariable Integer idAnimal) {
        List<MedicacaoCompletaDTO> medicacoes = medicacaoService.getMedicacoesPorAnimal(idAnimal);
        if (!medicacoes.isEmpty()) {
            return ResponseEntity.ok(medicacoes);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma medicação encontrada para este animal");
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> getMedicacoesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<MedicacaoCompletaDTO> medicacoes = medicacaoService.getMedicacoesPorPeriodo(dataInicio, dataFim);
        if (!medicacoes.isEmpty()) {
            return ResponseEntity.ok(medicacoes);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma medicação encontrada no período especificado");
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllMedicacoes() {
        List<MedicacaoCompletaDTO> medicacoes = medicacaoService.getAllMedicacoes();
        if (!medicacoes.isEmpty()) {
            return ResponseEntity.ok(medicacoes);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma medicação encontrada");
    }

    @PostMapping("/efetuar")
    public ResponseEntity<Object> efetuarMedicacao(@RequestBody Map<String, Object> payload) {
        try {
            MedicacaoModel medicacao = new MedicacaoModel();

            if (payload.get("idAnimal") != null) {
                medicacao.setIdanimal(Integer.parseInt(payload.get("idAnimal").toString()));
            }

            if (payload.get("idMedicamento") != null) {
                medicacao.setPosologia_medicamento_idproduto(Integer.parseInt(payload.get("idMedicamento").toString()));
            }

            if (payload.get("idReceita") != null) {
                medicacao.setPosologia_receitamedicamento_idreceita(Integer.parseInt(payload.get("idReceita").toString()));
            }

            if (payload.get("data") != null) {
                medicacao.setData(LocalDate.parse(payload.get("data").toString()));
            }

            String descricaoHistorico = payload.get("descricaoHistorico") != null ?
                    payload.get("descricaoHistorico").toString() : null;

            MedicacaoModel novaMedicacao = medicacaoService.efetuarMedicacao(medicacao, descricaoHistorico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaMedicacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}