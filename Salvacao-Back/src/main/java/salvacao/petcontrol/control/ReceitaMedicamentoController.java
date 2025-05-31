package salvacao.petcontrol.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import salvacao.petcontrol.dto.ReceitaMedicamentoDTO;
import salvacao.petcontrol.dto.ReceitaMedicamentoRequestDTO;
import salvacao.petcontrol.dto.PosologiaDTO;
import salvacao.petcontrol.service.ReceitaMedicamentoService;
import salvacao.petcontrol.service.AnimalService;
import salvacao.petcontrol.service.MedicamentoService;
import salvacao.petcontrol.util.ResultadoOperacao;

@RestController
@RequestMapping("/api/receituario")
@CrossOrigin(origins = "*")
public class ReceitaMedicamentoController {

    @Autowired
    private ReceitaMedicamentoService receitaService;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private MedicamentoService medicamentoService;

    public ReceitaMedicamentoController() {
        if (receitaService == null) {
            receitaService = new ReceitaMedicamentoService();
        }
    }

    @PostMapping
    public ResponseEntity<Object> cadastrar(@RequestBody ReceitaMedicamentoRequestDTO request) {
        try {
            ResultadoOperacao resultado = receitaService.cadastrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> listarTodas() {
        try {
            List<ReceitaMedicamentoDTO> receitas = receitaService.listarTodas();
            return ResponseEntity.ok(receitas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar receitas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            ReceitaMedicamentoDTO receita = receitaService.buscarPorId(id);
            if (receita != null) {
                return ResponseEntity.ok(receita);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receita não encontrada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody ReceitaMedicamentoRequestDTO request) {
        try {
            boolean atualizado = receitaService.alterar(id, request);
            if (atualizado) {
                return ResponseEntity.ok("Receita atualizada com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar a receita");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagarReceita(@PathVariable Integer id) {
        try {
            ResultadoOperacao resultado = receitaService.apagarReceita(id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<Object> buscarPorAnimal(@PathVariable Integer animalId) {
        try {
            List<ReceitaMedicamentoDTO> receitas = receitaService.buscarPorAnimal(animalId);
            if (!receitas.isEmpty()) {
                return ResponseEntity.ok(receitas);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma receita encontrada para este animal");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar receitas por animal: " + e.getMessage());
        }
    }

    @GetMapping("/medico/{medico}")
    public ResponseEntity<Object> buscarPorMedico(@PathVariable String medico) {
        try {
            List<ReceitaMedicamentoDTO> receitas = receitaService.buscarPorMedico(medico);
            if (!receitas.isEmpty()) {
                return ResponseEntity.ok(receitas);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma receita encontrada para este médico");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar receitas por médico: " + e.getMessage());
        }
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<Object> buscarPorData(@PathVariable String data) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(data);
            List<ReceitaMedicamentoDTO> receitas = receitaService.buscarPorData(localDate);
            if (!receitas.isEmpty()) {
                return ResponseEntity.ok(receitas);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma receita encontrada para esta data");
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de data inválido. Use o formato YYYY-MM-DD");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar receitas por data: " + e.getMessage());
        }
    }

    @GetMapping("/{receitaId}/posologias")
    public ResponseEntity<Object> buscarPosologiasPorReceita(@PathVariable Integer receitaId) {
        try {
            List<PosologiaDTO> posologias = receitaService.buscarPosologiasPorReceita(receitaId);
            return ResponseEntity.ok(posologias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar posologias: " + e.getMessage());
        }
    }

    @GetMapping("/medicamento/{medicamentoId}/posologias")
    public ResponseEntity<Object> buscarPosologiasPorMedicamento(@PathVariable Integer medicamentoId) {
        try {
            List<PosologiaDTO> posologias = receitaService.buscarPosologiasPorMedicamento(medicamentoId);
            return ResponseEntity.ok(posologias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar posologias por medicamento: " + e.getMessage());
        }
    }

    @GetMapping("/animais")
    public ResponseEntity<Object> buscarAnimais() {
        try {
            return ResponseEntity.ok(animalService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar animais: " + e.getMessage());
        }
    }

    @GetMapping("/medicamentos")
    public ResponseEntity<Object> buscarMedicamentos() {
        try {
            return ResponseEntity.ok(medicamentoService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar medicamentos: " + e.getMessage());
        }
    }
}