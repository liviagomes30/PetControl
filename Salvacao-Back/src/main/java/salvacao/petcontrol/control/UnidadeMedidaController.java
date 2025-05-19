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
    public ResponseEntity<Object> getAllUnidadesMedida() {
        try {
            List<UnidadeMedidaModel> unidadesMedida = unidadeMedidaService.getAllUnidadesMedida();
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
    public ResponseEntity<Object> getUnidadeMedidaById(@PathVariable Integer id) {
        try {
            UnidadeMedidaModel unidadeMedida = unidadeMedidaService.getUnidadeMedidaById(id);
            if (unidadeMedida != null) {
                return ResponseEntity.ok(unidadeMedida);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unidade de medida não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar unidade de medida: " + e.getMessage());
        }
    }
}