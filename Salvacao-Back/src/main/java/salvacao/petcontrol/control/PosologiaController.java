package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.service.PosologiaService;


import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/posologias")
public class PosologiaController {

    @Autowired
    private PosologiaService posologiaService;

    // NOVO MÉTODO: Listar todas as posologias
    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            List<PosologiaModel> posologias = posologiaService.getAll();
            if (!posologias.isEmpty()) {
                return ResponseEntity.ok(posologias);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma posologia encontrada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar posologias: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody PosologiaModel posologia) {
        try {
            PosologiaModel novaPosologia = posologiaService.gravar(posologia);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPosologia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{medicamentoId}/{receitaId}")
    public ResponseEntity<Object> getPosologiaByIds(@PathVariable Integer medicamentoId, @PathVariable Integer receitaId) {
        try {
            PosologiaModel posologia = posologiaService.getId(medicamentoId, receitaId);
            if (posologia != null) {
                return ResponseEntity.ok(posologia);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Posologia não encontrada para o medicamento e receita especificados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar posologia: " + e.getMessage());
        }
    }

    @GetMapping("/receita/{receitaId}")
    public ResponseEntity<Object> getMedicamentosByReceita(
            @PathVariable Integer receitaId,
            @RequestParam Integer animalId) { // Adicionado @RequestParam
        try {
            // Chama um novo método no serviço que fará a validação
            List<PosologiaDTO> posologiasPendentes = posologiaService.getPosologiasPendentes(receitaId, animalId);

            // Se a lista estiver vazia (todos os medicamentos foram aplicados), retorna 204 No Content
            if (posologiasPendentes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(posologiasPendentes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar medicamentos da receita: " + e.getMessage());
        }
    }
}