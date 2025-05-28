// salvacao.petcontrol.control.PessoaController.java
package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.service.PessoaService;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id) {
        try {
            PessoaModel pessoa = pessoaService.getId(id);
            return ResponseEntity.ok(pessoa);
        } catch (Exception e) {
            ResultadoOperacao resultado = new ResultadoOperacao("buscar", false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<PessoaModel> pessoas = pessoaService.getAll();
        if (!pessoas.isEmpty()) {
            return ResponseEntity.ok(pessoas);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhuma pessoa encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getByNome(@PathVariable String filtro) {
        List<PessoaModel> pessoas = pessoaService.getByNome(filtro);
        if (!pessoas.isEmpty()) {
            return ResponseEntity.ok(pessoas);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhuma pessoa encontrada com o nome especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/cpf/{filtro}")
    public ResponseEntity<Object> getByCpf(@PathVariable String filtro) {
        List<PessoaModel> pessoas = pessoaService.getByCpf(filtro);
        if (!pessoas.isEmpty()) {
            return ResponseEntity.ok(pessoas);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhuma pessoa encontrada com o CPF especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/email/{filtro}")
    public ResponseEntity<Object> getByEmail(@PathVariable String filtro) {
        List<PessoaModel> pessoas = pessoaService.getByEmail(filtro);
        if (!pessoas.isEmpty()) {
            return ResponseEntity.ok(pessoas);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhuma pessoa encontrada com o email especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @GetMapping("/telefone/{filtro}")
    public ResponseEntity<Object> getByTelefone(@PathVariable String filtro) {
        List<PessoaModel> pessoas = pessoaService.getByTelefone(filtro);
        if (!pessoas.isEmpty()) {
            return ResponseEntity.ok(pessoas);
        }
        ResultadoOperacao resultado = new ResultadoOperacao("listar", false, "Nenhuma pessoa encontrada com o telefone especificado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody PessoaModel pessoa) {
        try {
            PessoaModel novaPessoa = pessoaService.gravar(pessoa);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> alterar(@PathVariable Integer id, @RequestBody PessoaModel pessoa) {
        try {
            // Ensure the ID in the path matches the object's ID if present, or set it.
            pessoa.setIdpessoa(id);
            boolean atualizado = pessoaService.alterar(pessoa);
            if (atualizado) {
                return ResponseEntity.ok("Pessoa atualizada com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar pessoa");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagar(@PathVariable Integer id) {
        try {
            boolean deletado = pessoaService.apagar(id);
            if (deletado) {
                return ResponseEntity.ok("Pessoa excluída com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao excluir a pessoa");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}