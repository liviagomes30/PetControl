// salvacao.petcontrol.control.PosologiaController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.PosologiaModel;
import salvacao.petcontrol.service.PosologiaService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/posologias")
public class PosologiaController {

    @Autowired
    private PosologiaService posologiaService;

    // As DAOs e Services para Posologia não possuem métodos de listagem, alteração ou exclusão
    // baseados apenas no ID da posologia, pois a chave primária é composta.
    // Portanto, este controller terá foco no método de gravação e, se aplicável, busca por chaves compostas.

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody PosologiaModel posologia) {
        try {
            PosologiaModel novaPosologia = posologiaService.gravar(posologia);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPosologia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Example of a GET endpoint if a composite key search was needed in the service
    // @GetMapping("/{medicamentoId}/{receitaId}")
    // public ResponseEntity<Object> getByIds(@PathVariable Integer medicamentoId, @PathVariable Integer receitaId) {
    //     try {
    //         PosologiaModel posologia = posologiaService.getByIds(medicamentoId, receitaId);
    //         return ResponseEntity.ok(posologia);
    //     } catch (Exception e) {
    //         ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    //     }
    // }

    @GetMapping("/{medicamentoId}/{receitaId}")
    public ResponseEntity<Object> getPosologiaByIds(@PathVariable Integer medicamentoId, @PathVariable Integer receitaId) {
        try {
            // Este método já existe em PosologiaService.java
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
}