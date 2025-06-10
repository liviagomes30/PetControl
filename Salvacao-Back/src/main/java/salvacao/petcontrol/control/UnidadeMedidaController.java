// salvacao.petcontrol.control.UnidadeMedidaController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.service.UnidadeMedidaService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/unidades-medida")
public class UnidadeMedidaController {

    @Autowired
    private UnidadeMedidaService unidadeMedidaService;

    @GetMapping
    public ResponseEntity<Object> getAll() { // Renamed from getAllUnidadesMedida
        try {
            List<UnidadeMedidaModel> unidadesMedida = unidadeMedidaService.getAll(); // Calls service.getAll()
            if (!unidadesMedida.isEmpty()) {
                return ResponseEntity.ok(unidadesMedida);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma unidade de medida encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar unidades de medida: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) { // Renamed from getUnidadeMedidaById
        try {
            UnidadeMedidaModel unidadeMedida = unidadeMedidaService.getId(id); // Calls service.getId()
            if (unidadeMedida != null) {
                return ResponseEntity.ok(unidadeMedida);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unidade de medida não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar unidade de medida: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody UnidadeMedidaModel unidadeMedida) { // Added gravar method
        try {
            UnidadeMedidaModel novaUnidadeMedida = unidadeMedidaService.gravar(unidadeMedida); // Calls service.gravar()
            return ResponseEntity.status(HttpStatus.CREATED).body(novaUnidadeMedida);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody UnidadeMedidaModel unidadeMedida) { // Added alterar method
        try {
            unidadeMedida.setIdUnidadeMedida(id); // Set ID from path for consistency
            boolean atualizado = unidadeMedidaService.alterar(unidadeMedida); // Calls service.alterar()
            if (atualizado) {
                return ResponseEntity.ok("Unidade de medida atualizada com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar a unidade de medida");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagar(@PathVariable Integer id) { // Added apagar method
        try {
            boolean deletado = unidadeMedidaService.apagar(id); // Calls service.apagar()
            if (deletado) {
                return ResponseEntity.ok("Unidade de medida excluída com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao excluir a unidade de medida");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Example of a search method (if needed, mirroring AnimalController's search methods)
    @GetMapping("/search/{filtro}")
    public ResponseEntity<Object> getByDescricaoSigla(@PathVariable String filtro) {
        try {
            List<UnidadeMedidaModel> unidadesMedida = unidadeMedidaService.getByDescricaoSigla(filtro); // Calls service.getByDescricaoSigla()
            if (!unidadesMedida.isEmpty()) {
                return ResponseEntity.ok(unidadesMedida);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma unidade de medida encontrada com o filtro: " + filtro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar unidades de medida por descrição/sigla: " + e.getMessage());
        }
    }
}