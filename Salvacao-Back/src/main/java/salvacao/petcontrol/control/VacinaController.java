package salvacao.petcontrol.control;

import salvacao.petcontrol.dto.VacinaDTO;
import salvacao.petcontrol.service.VacinaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacinas")
@CrossOrigin(origins = "*")
public class VacinaController {

    private final VacinaService vacinaService;

    @Autowired
    public VacinaController(VacinaService vacinaService) {
        this.vacinaService = vacinaService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrarVacina(@RequestBody VacinaDTO vacinaDTO) {
        try {
            VacinaDTO novaVacina = vacinaService.cadastrarVacina(vacinaDTO);
            return new ResponseEntity<>(novaVacina, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar vacina: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<VacinaDTO>> listarTodasVacinas() {
        try {
            List<VacinaDTO> vacinas = vacinaService.listarTodasVacinas();
            if (vacinas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(vacinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarVacinaPorId(@PathVariable Integer id) {
        VacinaDTO vacinaDTO = vacinaService.buscarVacinaPorId(id);
        if (vacinaDTO != null) {
            return ResponseEntity.ok(vacinaDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vacina com ID " + id + " não encontrada.");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<VacinaDTO>> buscarPorDescricao(@RequestParam String descricao) {
        try {
            List<VacinaDTO> vacinas = vacinaService.buscarPorDescricao(descricao);
            if (vacinas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(vacinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}