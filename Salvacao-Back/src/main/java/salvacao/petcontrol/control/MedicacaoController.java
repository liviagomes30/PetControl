package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.BatchMedicacaoRequestDTO;
import salvacao.petcontrol.model.MedicacaoModel;
import salvacao.petcontrol.service.MedicacaoService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/medicacoes")
public class MedicacaoController {

    @Autowired
    private MedicacaoService medicacaoService;

    @PostMapping("/efetuar-lote")
    public ResponseEntity<ResultadoOperacao> efetuarMedicacaoEmLote(@RequestBody BatchMedicacaoRequestDTO request) {
        try {
            ResultadoOperacao resultado = medicacaoService.efetuarMedicacaoEmLote(request);
            if (resultado.isSucesso()) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResultadoOperacao("efetuarMedicacaoEmLote", false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicacaoModel> getMedicacaoById(@PathVariable Integer id) {
        MedicacaoModel medicacao = medicacaoService.getId(id);
        if (medicacao != null) {
            return ResponseEntity.ok(medicacao);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicacaoModel>> getAllMedicacoes() {
        List<MedicacaoModel> medicacoes = medicacaoService.getAll();
        return ResponseEntity.ok(medicacoes);
    }

    @PostMapping
    public ResponseEntity<?> gravarMedicacao(@RequestBody MedicacaoModel medicacao) {
        try {
            MedicacaoModel novaMedicacao = medicacaoService.gravar(medicacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaMedicacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterarMedicacao(@PathVariable Integer id, @RequestBody MedicacaoModel medicacao) {
        if (!id.equals(medicacao.getIdmedicacao())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID no caminho não corresponde ao ID da medicação no corpo da requisição.");
        }
        try {
            boolean atualizado = medicacaoService.alterar(medicacao);
            if (atualizado) {
                return ResponseEntity.ok().body("Medicação atualizada com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicação não encontrada para atualização.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarMedicacao(@PathVariable Integer id) {
        try {
            boolean deletado = medicacaoService.apagar(id);
            if (deletado) {
                return ResponseEntity.ok().body("Medicação excluída com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicação não encontrada para exclusão.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<List<MedicacaoModel>> getMedicacoesByAnimal(@PathVariable Integer idAnimal) {
        List<MedicacaoModel> medicacoes = medicacaoService.getMedicacoesByAnimal(idAnimal);
        return ResponseEntity.ok(medicacoes);
    }

    @GetMapping("/search-by-composicao")
    public ResponseEntity<List<MedicacaoModel>> searchMedicacoesByComposicao(@RequestParam String searchTerm) {
        List<MedicacaoModel> medicacoes = medicacaoService.searchMedicacoesByComposicao(searchTerm);
        return ResponseEntity.ok(medicacoes);
    }
}