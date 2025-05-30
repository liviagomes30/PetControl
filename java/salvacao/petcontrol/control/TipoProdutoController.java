// salvacao.petcontrol.control.TipoProdutoController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.service.TipoProdutoService; // Still refers to the service

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tipos-produto")
public class TipoProdutoController {

    @Autowired
    private TipoProdutoService tipoProdutoService;

    @GetMapping
    public ResponseEntity<Object> getAll() { // Renamed from getAllTiposProduto
        try {
            List<TipoProdutoModel> tiposProduto = tipoProdutoService.getAll(); // Calls service.getAll()
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
    public ResponseEntity<Object> getId(@PathVariable Integer id) { // Renamed from getTipoProdutoById
        try {
            TipoProdutoModel tipoProduto = tipoProdutoService.getId(id); // Calls service.getId()
            if (tipoProduto != null) {
                return ResponseEntity.ok(tipoProduto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de produto não encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar tipo de produto: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody TipoProdutoModel tipoProduto) { // Added gravar method
        try {
            TipoProdutoModel novoTipoProduto = tipoProdutoService.gravar(tipoProduto); // Calls service.gravar()
            return ResponseEntity.status(HttpStatus.CREATED).body(novoTipoProduto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody TipoProdutoModel tipoProduto) { // Added alterar method
        try {
            // Ensure the ID in the path matches the object's ID if present, or set it.
            // For simplicity and consistency with AnimalController, the ID from path is preferred.
            tipoProduto.setIdtipoproduto(id);
            boolean atualizado = tipoProdutoService.alterar(tipoProduto); // Calls service.alterar()
            if (atualizado) {
                return ResponseEntity.ok("Tipo de produto atualizado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar o tipo de produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagar(@PathVariable Integer id) { // Added apagar method
        try {
            boolean deletado = tipoProdutoService.apagar(id); // Calls service.apagar()
            if (deletado) {
                return ResponseEntity.ok("Tipo de produto excluído com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao excluir o tipo de produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Example of a search method (if needed, mirroring AnimalController's search methods)
    @GetMapping("/descricao/{filtro}")
    public ResponseEntity<Object> getByDescricao(@PathVariable String filtro) {
        try {
            List<TipoProdutoModel> tiposProduto = tipoProdutoService.getByDescricao(filtro); // Calls service.getByDescricao()
            if (!tiposProduto.isEmpty()) {
                return ResponseEntity.ok(tiposProduto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum tipo de produto encontrado com o filtro: " + filtro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar tipos de produto por descrição: " + e.getMessage());
        }
    }
}