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
    public ResponseEntity<Object> getAcertoById(@PathVariable Integer id) {
        try {
            AcertoEstoqueCompletoDTO acerto = acertoEstoqueService.getAcertoById(id);
            return ResponseEntity.ok(acerto);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllAcertos() {
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getAllAcertos();
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> getAcertosByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getAcertosByPeriodo(dataInicio, dataFim);
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado no período especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Object> getAcertosByUsuario(@PathVariable Integer usuarioId) {
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getAcertosByUsuario(usuarioId);
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado para este usuário");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @PostMapping
    public ResponseEntity<Object> efetuarAcertoEstoque(@RequestBody AcertoEstoqueRequestDTO request) {
        try {
            ResultadoOperacao resultado = acertoEstoqueService.efetuarAcertoEstoque(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("acertoEstoque", false, e.getMessage());
            return ResponseEntity.badRequest().body(resultado);
        }
    }
}
