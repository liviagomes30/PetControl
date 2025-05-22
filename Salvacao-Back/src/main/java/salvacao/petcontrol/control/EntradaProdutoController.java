package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.EntradaProdutoDTO;
import salvacao.petcontrol.model.EntradaProdutoModel;
import salvacao.petcontrol.model.RegistroModel;
import salvacao.petcontrol.service.EntradaProdutoService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/entrada-produto")
public class EntradaProdutoController {
    @Autowired
    private EntradaProdutoService entradaProdutoService;

    @GetMapping()
    public ResponseEntity<Object> getAll(){
        List<EntradaProdutoDTO> entradaProdutoDTOList = entradaProdutoService.getPoduto();
        if (!entradaProdutoDTOList.isEmpty())
            return ResponseEntity.ok(entradaProdutoDTOList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
    }

    @GetMapping("/registro")
    public ResponseEntity<Object> getAllRegistro(){
        List<RegistroModel> registrosList = entradaProdutoService.getRegistros();
        if (!registrosList.isEmpty())
            return ResponseEntity.ok(registrosList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum registro encontrado");
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getProdutoNome(@PathVariable String filtro){
        List<EntradaProdutoDTO> entradaProdutoDTOList = entradaProdutoService.getPodutoNome(filtro);
        if (!entradaProdutoDTOList.isEmpty())
            return ResponseEntity.ok(entradaProdutoDTOList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
    }

    @PostMapping()
    public ResponseEntity<Object> addRegistroEntrada(@RequestBody EntradaProdutoModel registro){
        try {
            entradaProdutoService.addRegistro(registro);
            return ResponseEntity.ok(registro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
