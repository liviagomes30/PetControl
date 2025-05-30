// salvacao.petcontrol.control.EstoqueController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.EstoqueProdutoDTO;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.service.EstoqueService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) { // Renamed from getEstoqueById
        try {
            EstoqueProdutoDTO estoque = estoqueService.getEstoqueProdutoCompleto(id); // Calls existing service method
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<Object> getByProdutoId(@PathVariable Integer idProduto) {
        EstoqueModel estoque = estoqueService.getByProdutoId(idProduto);
        if (estoque != null) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estoque não encontrado para este produto");
    }

    @GetMapping
    public ResponseEntity<Object> getAll() { // Renamed from getAllEstoque
        List<EstoqueProdutoDTO> estoque = estoqueService.getAllEstoqueProduto(); // Calls existing service method
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado no estoque");
    }

    @GetMapping("/abaixo-minimo")
    public ResponseEntity<Object> getAbaixoMinimo() { // Renamed from getEstoqueAbaixoMinimo
        List<EstoqueProdutoDTO> estoque = estoqueService.getEstoqueProdutoAbaixoMinimo(); // Calls existing service method
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item abaixo do estoque mínimo");
    }

    @GetMapping("/buscar/nome/{nomeProduto}")
    public ResponseEntity<Object> getByNomeProduto(@PathVariable String nomeProduto) { // Renamed from buscarPorNomeProduto
        List<EstoqueProdutoDTO> estoque = estoqueService.getByNomeProduto(nomeProduto); // Calls updated service method
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado com este nome de produto");
    }

    @GetMapping("/buscar/tipo/{idTipoProduto}")
    public ResponseEntity<Object> getByTipoProduto(@PathVariable Integer idTipoProduto) { // Renamed from buscarPorTipoProduto
        try {
            List<EstoqueProdutoDTO> estoque = estoqueService.getByTipoProduto(idTipoProduto); // Calls updated service method
            if (!estoque.isEmpty()) {
                return ResponseEntity.ok(estoque);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado para este tipo de produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody EstoqueModel estoque) { // Renamed from addEstoque
        try {
            EstoqueModel novoEstoque = estoqueService.gravar(estoque); // Calls updated service method
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEstoque);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody EstoqueModel estoque) { // Renamed from updateEstoque
        try {
            boolean atualizado = estoqueService.alterar(id, estoque); // Calls updated service method
            if (atualizado) {
                return ResponseEntity.ok("Estoque atualizado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar estoque");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagar(@PathVariable Integer id) { // Renamed from deleteEstoque
        try {
            boolean removido = estoqueService.apagar(id); // Calls updated service method
            if (removido) {
                return ResponseEntity.ok("Estoque removido com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao remover estoque");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decrementar")
    public ResponseEntity<Object> decrementar(@RequestBody Map<String, Object> payload) { // Renamed from decrementarEstoque
        try {
            Integer idProduto = Integer.parseInt(payload.get("idProduto").toString());
            Integer quantidade = Integer.parseInt(payload.get("quantidade").toString());

            boolean decrementado = estoqueService.decrementar(idProduto, quantidade); // Calls updated service method
            if (decrementado) {
                return ResponseEntity.ok("Estoque decrementado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível decrementar o estoque");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/incrementar")
    public ResponseEntity<Object> incrementar(@RequestBody Map<String, Object> payload) { // Renamed from incrementarEstoque
        try {
            Integer idProduto = Integer.parseInt(payload.get("idProduto").toString());
            Integer quantidade = Integer.parseInt(payload.get("quantidade").toString());

            boolean incrementado = estoqueService.incrementar(idProduto, quantidade); // Calls updated service method
            if (incrementado) {
                return ResponseEntity.ok("Estoque incrementado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível incrementar o estoque");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<Object> verificarSuficiente( // Renamed from verificarEstoqueSuficiente
                                                       @RequestParam Integer idProduto,
                                                       @RequestParam Integer quantidade) {
        boolean suficiente = estoqueService.verificarSuficiente(idProduto, quantidade); // Calls updated service method
        return ResponseEntity.ok(Map.of("suficiente", suficiente));
    }
}