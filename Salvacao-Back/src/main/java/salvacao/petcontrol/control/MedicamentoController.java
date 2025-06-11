// salvacao.petcontrol.control.MedicamentoController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.MedicamentoCompletoDTO;
import salvacao.petcontrol.model.MedicamentoModel;
import salvacao.petcontrol.service.MedicamentoService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    // ==================== CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<Object> cadastrar(@RequestBody MedicamentoCompletoDTO medicamento) {
        try {
            MedicamentoModel novoMedicamento = medicamentoService.gravar(medicamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMedicamento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        try {
            List<MedicamentoCompletoDTO> medicamentos = medicamentoService.getAll();
            if (!medicamentos.isEmpty()) {
                return ResponseEntity.ok(medicamentos);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar medicamentos: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<Object> listar() {
        return listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            MedicamentoCompletoDTO medicamento = medicamentoService.getId(id);
            if (medicamento != null) {
                return ResponseEntity.ok(medicamento);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicamento não encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody MedicamentoCompletoDTO medicamento) {
        try {
            boolean atualizado = medicamentoService.alterar(id, medicamento);
            if (atualizado) {
                return ResponseEntity.ok("Medicamento atualizado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar o medicamento");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            ResultadoOperacao resultado = medicamentoService.apagarMedicamento(id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== SEARCH OPERATIONS ====================

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> buscarPorNome(@PathVariable String filtro) {
        try {
            List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getNome(filtro);
            if (!medicamentoList.isEmpty()) {
                return ResponseEntity.ok(medicamentoList);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar medicamentos por nome: " + e.getMessage());
        }
    }

    @GetMapping("/composicao/{filtro}")
    public ResponseEntity<Object> buscarPorComposicao(@PathVariable String filtro) {
        try {
            List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getComposicao(filtro);
            if (!medicamentoList.isEmpty()) {
                return ResponseEntity.ok(medicamentoList);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar medicamentos por composição: " + e.getMessage());
        }
    }

    @GetMapping("/tipo/{filtro}")
    public ResponseEntity<Object> buscarPorTipo(@PathVariable String filtro) {
        try {
            List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getTipo(filtro);
            if (!medicamentoList.isEmpty()) {
                return ResponseEntity.ok(medicamentoList);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar medicamentos por tipo: " + e.getMessage());
        }
    }

    // ==================== STATUS OPERATIONS ====================

    @GetMapping("/inativos")
    public ResponseEntity<Object> listarInativos() {
        try {
            List<MedicamentoCompletoDTO> medicamentos = medicamentoService.getAllInactive();
            if (!medicamentos.isEmpty()) {
                return ResponseEntity.ok(medicamentos);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento inativo encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar medicamentos inativos: " + e.getMessage());
        }
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Object> listarDisponiveis() {
        try {
            List<MedicamentoCompletoDTO> medicamentos = medicamentoService.listarTodosDisponiveis();
            if (!medicamentos.isEmpty()) {
                return ResponseEntity.ok(medicamentos);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento disponível encontrado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar medicamentos disponíveis: " + e.getMessage());
        }
    }
}