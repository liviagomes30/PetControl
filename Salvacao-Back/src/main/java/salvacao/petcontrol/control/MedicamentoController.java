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
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        List<MedicamentoCompletoDTO> medicamentos = medicamentoService.getAllMedicamentos();
        if (!medicamentos.isEmpty()) {
            return ResponseEntity.ok(medicamentos);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum medicamento encontrado");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMedicamentoById(@PathVariable Integer id) {
        MedicamentoCompletoDTO medicamento = medicamentoService.getMedicamentoById(id);
        if (medicamento != null) {
            return ResponseEntity.ok(medicamento);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicamento não encontrado");
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> addMedicamento(@RequestBody MedicamentoCompletoDTO medicamento) {
        try {
            MedicamentoModel novoMedicamento = medicamentoService.addMedicamento(medicamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMedicamento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMedicamento(@PathVariable Integer id, @RequestBody MedicamentoCompletoDTO medicamento) {
        try {
            boolean atualizado = medicamentoService.updateMedicamento(id, medicamento);
            if (atualizado) {
                return ResponseEntity.ok("Medicamento atualizado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar o medicamento");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> gerenciarExclusaoMedicamento(@PathVariable Integer id) {
        try {
            ResultadoOperacao resultado = medicamentoService.gerenciarExclusaoMedicamento(id);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getNome(@PathVariable String filtro){
        List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getNome(filtro);
        if(!medicamentoList.isEmpty())
            return ResponseEntity.ok(medicamentoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Medicamento encontrado");
    }

    @GetMapping("/composicao/{filtro}")
    public ResponseEntity<Object> getComposicao(@PathVariable String filtro){
        List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getComposicao(filtro);
        if(!medicamentoList.isEmpty())
            return ResponseEntity.ok(medicamentoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Medicamento encontrado");
    }

    @GetMapping("/tipo/{filtro}")
    public ResponseEntity<Object> getTipo(@PathVariable String filtro){
        List<MedicamentoCompletoDTO> medicamentoList = medicamentoService.getTipo(filtro);
        if(!medicamentoList.isEmpty())
            return ResponseEntity.ok(medicamentoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Medicamento encontrado");
    }
}
