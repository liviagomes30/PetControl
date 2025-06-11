package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.service.ProdutoService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        List<ProdutoCompletoDTO> produtos = produtoService.getAll(); // Updated method call
        if (!produtos.isEmpty()) {
            return ResponseEntity.ok(produtos);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        ProdutoCompletoDTO produto = produtoService.getId(id); // Updated method call
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
    }

    @GetMapping("/tipo/{idTipo}")
    public ResponseEntity<Object> getByTipo(@PathVariable Integer idTipo) {
        List<ProdutoCompletoDTO> produtos = produtoService.getProdutosByTipo(idTipo);
        if (!produtos.isEmpty()) {
            return ResponseEntity.ok(produtos);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado para o tipo especificado");
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String termo) {
        List<ProdutoCompletoDTO> produtos = produtoService.getByName(termo);
        if (!produtos.isEmpty()) {
            return ResponseEntity.ok(produtos);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com o termo: " + termo);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> gravar(@RequestBody ProdutoCompletoDTO produto) {
        try {
            ProdutoModel novoProduto = produtoService.gravar(produto); // Updated method call
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody ProdutoCompletoDTO produto) {
        try {
            boolean atualizado = produtoService.alterar(id, produto); // Updated method call
            if (atualizado) {
                return ResponseEntity.ok("Produto atualizado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível atualizar o produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagarProduto(@PathVariable Integer id) {
        try {
            ResultadoOperacao resultado = produtoService.apagarProduto(id); // Updated method call
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reativar")
    public ResponseEntity<Object> reativarProduto(@PathVariable Integer id) {
        try {
            boolean reativado = produtoService.reativarProduto(id); // Method name in service remains reativarProduto
            if (reativado) {
                return ResponseEntity.ok("Produto reativado com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível reativar o produto");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getNome(@PathVariable String filtro) {
        List<ProdutoCompletoDTO> produtoList = produtoService.getByName(filtro); // Method name in service remains getByName
        if (!produtoList.isEmpty())
            return ResponseEntity.ok(produtoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Produto encontrado");
    }

    @GetMapping("/fabricante/{filtro}")
    public ResponseEntity<Object> getByFabricante(@PathVariable String filtro) {
        List<ProdutoCompletoDTO> produtoList = produtoService.getByFabricante(filtro); // Method name in service remains getByFabricante
        if (!produtoList.isEmpty())
            return ResponseEntity.ok(produtoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Produto encontrado");
    }

    @GetMapping("/tipoDescricao/{filtro}")
    public ResponseEntity<Object> getByTipoDescricao(@PathVariable String filtro) {
        List<ProdutoCompletoDTO> produtoList = produtoService.getByTipoDescricao(filtro); // Method name in service remains getByTipoDescricao
        if (!produtoList.isEmpty())
            return ResponseEntity.ok(produtoList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Produto encontrado");
    }
}