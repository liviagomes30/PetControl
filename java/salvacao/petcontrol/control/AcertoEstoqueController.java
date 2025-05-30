// salvacao.petcontrol.control.AcertoEstoqueController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.AcertoEstoqueCompletoDTO;
import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.model.AcertoEstoqueModel;
import salvacao.petcontrol.service.AcertoEstoqueService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/acerto-estoque")
public class AcertoEstoqueController {

    @Autowired
    private AcertoEstoqueService acertoEstoqueService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) { // Renamed from getAcertoById
        try {
            AcertoEstoqueCompletoDTO acerto = acertoEstoqueService.getId(id); // Calls service.getId()
            return ResponseEntity.ok(acerto);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() { // Renamed from getAllAcertos
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getAll(); // Calls service.getAll()
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> getByPeriodo( // Renamed from getAcertosByPeriodo
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getByPeriodo(dataInicio, dataFim); // Calls service.getByPeriodo()
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado no período especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Object> getByUsuario(@PathVariable Integer usuarioId) { // Renamed from getAcertosByUsuario
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getByUsuario(usuarioId); // Calls service.getByUsuario()
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado para este usuário");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody AcertoEstoqueRequestDTO request) { // Renamed from efetuarAcertoEstoque
        try {
            ResultadoOperacao resultado = acertoEstoqueService.gravar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("acertoEstoque", false, e.getMessage());
            return ResponseEntity.badRequest().body(resultado);
        }
    }
}