package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.AcertoEstoqueCompletoDTO;
import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.model.AcertoEstoqueModel;
// 1. Importar o novo serviço V2
import salvacao.petcontrol.service.AcertoEstoqueServiceV2;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/acerto-estoque")
public class AcertoEstoqueController {

    @Autowired
    private AcertoEstoqueServiceV2 acertoEstoqueService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        try {
            AcertoEstoqueCompletoDTO acerto = acertoEstoqueService.getId(id);
            return ResponseEntity.ok(acerto);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getAll();
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> getByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getByPeriodo(dataInicio, dataFim);
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado no período especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Object> getByUsuario(@PathVariable Integer usuarioId) {
        List<AcertoEstoqueModel> acertos = acertoEstoqueService.getByUsuario(usuarioId);
        if (!acertos.isEmpty()) {
            return ResponseEntity.ok(acertos);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhum acerto de estoque encontrado para este usuário");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody AcertoEstoqueRequestDTO request) {
        try {
            ResultadoOperacao resultado = acertoEstoqueService.gravarComTemplate(request);

            if (resultado.isSucesso()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            } else {
                return ResponseEntity.badRequest().body(resultado);
            }
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("acertoEstoque", false, "Erro inesperado no controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
        }
    }
}