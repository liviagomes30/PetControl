package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.ReceitaMedicamentoModel;
import salvacao.petcontrol.service.ReceitaMedicamentoService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/receitas-medicamento")
public class ReceitaMedicamentoController {

    @Autowired
    private ReceitaMedicamentoService receitaMedicamentoService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        try {
            ReceitaMedicamentoModel receita = receitaMedicamentoService.getId(id);
            return ResponseEntity.ok(receita);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody ReceitaMedicamentoModel receitaMedicamento) {
        try {
            ReceitaMedicamentoModel novaReceita = receitaMedicamentoService.gravar(receitaMedicamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaReceita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<Object> getReceitasByAnimal(@PathVariable Integer animalId) {
        try {
            List<ReceitaMedicamentoModel> receitas = receitaMedicamentoService.getReceitasByAnimal(animalId); //
            if (!receitas.isEmpty()) {
                return ResponseEntity.ok(receitas);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma receita encontrada para este animal.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar receitas por animal: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            List<ReceitaMedicamentoModel> receitas = receitaMedicamentoService.getAll();
            if (!receitas.isEmpty()) {
                return ResponseEntity.ok(receitas);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma receita de medicamento encontrada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar receitas de medicamento: " + e.getMessage());
        }
    }
}