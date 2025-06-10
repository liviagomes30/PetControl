// salvacao.petcontrol.control.HistoricoController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.HistoricoModel;
import salvacao.petcontrol.service.HistoricoService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/historicos")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        try {
            HistoricoModel historico = historicoService.getId(id);
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<HistoricoModel> historicos = historicoService.getAll();
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<Object> getByAnimal(@PathVariable Integer idAnimal) {
        List<HistoricoModel> historicos = historicoService.getByAnimal(idAnimal);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado para este animal");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/animal/{idAnimal}/periodo")
    public ResponseEntity<Object> getByAnimalAndPeriodo(
            @PathVariable Integer idAnimal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<HistoricoModel> historicos = historicoService.getByAnimalAndPeriodo(idAnimal, dataInicio, dataFim);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado para este animal no período especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/vacinacao/{idVacinacao}")
    public ResponseEntity<Object> getByVacinacao(@PathVariable Integer idVacinacao) {
        List<HistoricoModel> historicos = historicoService.getByVacinacao(idVacinacao);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado para esta vacinação");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/medicacao/{idMedicacao}")
    public ResponseEntity<Object> getByMedicacao(@PathVariable Integer idMedicacao) {
        List<HistoricoModel> historicos = historicoService.getByMedicacao(idMedicacao);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado para esta medicação");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> getByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<HistoricoModel> historicos = historicoService.getByPeriodo(dataInicio, dataFim);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado no período especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/descricao/{filtro}")
    public ResponseEntity<Object> getByDescricao(@PathVariable String filtro) {
        List<HistoricoModel> historicos = historicoService.getByDescricao(filtro);
        if (!historicos.isEmpty()) {
            return ResponseEntity.ok(historicos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum histórico encontrado com a descrição especificada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody HistoricoModel historico) {
        try {
            HistoricoModel novoHistorico = historicoService.gravar(historico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoHistorico);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody HistoricoModel historico) {
        try {
            historico.setIdhistorico(id); // Set the ID from the path into the object
            boolean atualizado = historicoService.alterar(historico);
            if (atualizado) {
                return ResponseEntity.ok("Histórico atualizado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar histórico");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagar(@PathVariable Integer id) {
        try {
            boolean deletado = historicoService.apagar(id);
            if (deletado) {
                return ResponseEntity.ok("Histórico excluído com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao excluir o histórico");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}