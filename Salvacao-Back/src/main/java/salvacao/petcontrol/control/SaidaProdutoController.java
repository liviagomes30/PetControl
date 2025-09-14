package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.dto.EntradaProdutoDTO;
import salvacao.petcontrol.model.EntradaProdutoModel;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.RegistroModel;
import salvacao.petcontrol.service.EntradaProdutoService;
import salvacao.petcontrol.dto.ItensEntradaDTO;
import salvacao.petcontrol.service.SaidaProdutoServiceV2;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/saida-produto")
public class SaidaProdutoController {

    @Autowired
    private EntradaProdutoService entradaProdutoService;

    @Autowired
    private SaidaProdutoServiceV2 saidaProdutoService;

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
        List<RegistroModel> registrosList = saidaProdutoService.getRegistros();
        if (!registrosList.isEmpty())
            return ResponseEntity.ok(registrosList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum registro encontrado");
    }

    @GetMapping("/estoque/{id}")
    public ResponseEntity<Object> getEstoque(@PathVariable Integer id){
        EstoqueModel estoque = entradaProdutoService.getEstoque(id);
        if(estoque != null)
            return ResponseEntity.ok(estoque);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");

    }

    @GetMapping("/itens/{id}")
    public ResponseEntity<Object> getItens(@PathVariable Integer id){
        ItensEntradaDTO itensEntrada = entradaProdutoService.getItem(id);
        if (itensEntrada != null)
            return ResponseEntity.ok(itensEntrada);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum item encontrado");
    }

    @GetMapping("/periodo")
    public ResponseEntity<Object> buscarPorPeriodo(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {

        try {
            if (dataInicio == null || dataFim == null) {
                throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
            }

            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);

            List<RegistroModel> resultado = saidaProdutoService.getRegistrosPeriodo(inicio,fim);

            if (resultado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma movimentação encontrada no período.");
            }

            return ResponseEntity.ok(resultado);

        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de data inválido. Use yyyy-MM-dd.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

    // 3. Alterado para usar o novo método com Template Method
    @PostMapping()
    public ResponseEntity<Object> addRegistro(@RequestBody EntradaProdutoModel registro){
        try {
            // Chamando o método que encapsula a lógica de transação
            ResultadoOperacao resultado = saidaProdutoService.registrarSaidaComTemplate(registro);

            if (resultado.isSucesso()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            } else {
                return ResponseEntity.badRequest().body(resultado);
            }
        } catch (Exception e) {
            // Captura para erros inesperados não tratados pelo template
            ResultadoOperacao resultado = new ResultadoOperacao("saidaProduto", false, "Erro inesperado no controller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagarRegistro(@PathVariable Integer id){
        try {
            if (saidaProdutoService.apagar(id))
                return ResponseEntity.ok("Registro excluído com sucesso");
            else
                return ResponseEntity.badRequest().body("Erro ao excluir registros");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}