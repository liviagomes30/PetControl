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
    public ResponseEntity<Object> getEstoqueById(@PathVariable Integer id) {
        try {
            EstoqueProdutoDTO estoque = estoqueService.getEstoqueProdutoCompleto(id);
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<Object> getEstoqueByProdutoId(@PathVariable Integer idProduto) {
        EstoqueModel estoque = estoqueService.getEstoqueByProdutoId(idProduto);
        if (estoque != null) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estoque não encontrado para este produto");
    }

    @GetMapping
    public ResponseEntity<Object> getAllEstoque() {
        List<EstoqueProdutoDTO> estoque = estoqueService.getAllEstoqueProduto();
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado no estoque");
    }

    @GetMapping("/abaixo-minimo")
    public ResponseEntity<Object> getEstoqueAbaixoMinimo() {
        List<EstoqueProdutoDTO> estoque = estoqueService.getEstoqueProdutoAbaixoMinimo();
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item abaixo do estoque mínimo");
    }

    @GetMapping("/buscar/nome/{nomeProduto}")
    public ResponseEntity<Object> buscarPorNomeProduto(@PathVariable String nomeProduto) {
        List<EstoqueProdutoDTO> estoque = estoqueService.buscarEstoquePorNomeProduto(nomeProduto);
        if (!estoque.isEmpty()) {
            return ResponseEntity.ok(estoque);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado com este nome de produto");
    }

    @GetMapping("/buscar/tipo/{idTipoProduto}")
    public ResponseEntity<Object> buscarPorTipoProduto(@PathVariable Integer idTipoProduto) {
        try {
            List<EstoqueProdutoDTO> estoque = estoqueService.buscarEstoquePorTipoProduto(idTipoProduto);
            if (!estoque.isEmpty()) {
                return ResponseEntity.ok(estoque);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado para este tipo de produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> addEstoque(@RequestBody EstoqueModel estoque) {
        try {
            EstoqueModel novoEstoque = estoqueService.addEstoque(estoque);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEstoque);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEstoque(@PathVariable Integer id, @RequestBody EstoqueModel estoque) {
        try {
            boolean atualizado = estoqueService.updateEstoque(id, estoque);
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
    public ResponseEntity<Object> deleteEstoque(@PathVariable Integer id) {
        try {
            boolean removido = estoqueService.deleteEstoque(id);
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
    public ResponseEntity<Object> decrementarEstoque(@RequestBody Map<String, Object> payload) {
        try {
            Integer idProduto = Integer.parseInt(payload.get("idProduto").toString());
            Integer quantidade = Integer.parseInt(payload.get("quantidade").toString());

            boolean decrementado = estoqueService.decrementarEstoque(idProduto, quantidade);
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
    public ResponseEntity<Object> incrementarEstoque(@RequestBody Map<String, Object> payload) {
        try {
            Integer idProduto = Integer.parseInt(payload.get("idProduto").toString());
            Integer quantidade = Integer.parseInt(payload.get("quantidade").toString());

            boolean incrementado = estoqueService.incrementarEstoque(idProduto, quantidade);
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
    public ResponseEntity<Object> verificarEstoqueSuficiente(
            @RequestParam Integer idProduto,
            @RequestParam Integer quantidade) {
        boolean suficiente = estoqueService.verificarEstoqueSuficiente(idProduto, quantidade);
        return ResponseEntity.ok(Map.of("suficiente", suficiente));
    }
}