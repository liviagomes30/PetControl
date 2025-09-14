package salvacao.petcontrol.control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.PessoaPreferenciaModel;
import salvacao.petcontrol.service.PessoaService;


import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        List<PessoaModel> listPessoa = pessoaService.getAllPessoa();
        return ResponseEntity.ok(listPessoa); // lista vazia é uma resposta válida
    }


    @GetMapping("/{cpf}")
    public ResponseEntity<Object> getPessoaCpf(@PathVariable String cpf) {
        PessoaPreferenciaModel pessoa = pessoaService.getPessoaCpfPreferencia(cpf);
        if(pessoa != null)
            return ResponseEntity.ok(pessoa);
        return ResponseEntity.badRequest().body("Pessoa não encontrada!");
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> addPessoa(@RequestBody PessoaPreferenciaModel pessoa) {
        try {
            PessoaModel novaPessoa = pessoaService.addPessoa(pessoa);
            return ResponseEntity.ok(novaPessoa);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar pessoa: " + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<Object> updatePessoa(@RequestBody PessoaPreferenciaModel pessoa) {
        try {
            boolean atualizada = pessoaService.updatePessoa(pessoa.getCpf(), pessoa);
            if (atualizada) {
                return ResponseEntity.ok("Pessoa alterada com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa não encontrada com o CPF: " + pessoa.getCpf());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao atualizar pessoa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Object> deletePessoa(@PathVariable String cpf) {
        try {
            pessoaService.deletePessoa(cpf);
            return ResponseEntity.ok("Pessoa deletada com sucesso!");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar pessoa!");
        }
    }
}