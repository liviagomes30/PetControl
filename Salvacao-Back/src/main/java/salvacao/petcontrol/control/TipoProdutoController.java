package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.service.TipoProdutoService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tipos-produto")
public class TipoProdutoController {

    @Autowired
    private TipoProdutoService tipoProdutoService;

    @GetMapping
    public ResponseEntity<Object> getAllTiposProduto() {
        try {
            List<TipoProdutoModel> tiposProduto = tipoProdutoService.getAllTiposProduto();
            if (!tiposProduto.isEmpty()) {
                return ResponseEntity.ok(tiposProduto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum tipo de produto encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar tipos de produto: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTipoProdutoById(@PathVariable Integer id) {
        try {
            TipoProdutoModel tipoProduto = tipoProdutoService.getTipoProdutoById(id);
            if (tipoProduto != null) {
                return ResponseEntity.ok(tipoProduto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de produto não encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar tipo de produto: " + e.getMessage());
        }
    }
}