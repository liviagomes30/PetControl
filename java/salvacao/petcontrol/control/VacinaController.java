package salvacao.petcontrol.control;

import salvacao.petcontrol.dto.VacinaDTO;
import salvacao.petcontrol.service.VacinaService; // A classe VacinaService que criamos

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacinas") // Define o caminho base para os endpoints deste controller
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem, ajuste conforme necessário para segurança
public class VacinaController {

    private final VacinaService vacinaService;

    @Autowired
    public VacinaController(VacinaService vacinaService) {
        this.vacinaService = vacinaService;
    }

    /**
     * Endpoint para cadastrar uma nova vacina.
     * HTTP POST /vacinas
     *
     * @param vacinaDTO Dados da vacina a ser cadastrada, vindos no corpo da requisição.
     * @return ResponseEntity com o VacinaDTO cadastrado e status HTTP 201 (Created) ou erro.
     */
    @PostMapping
    public ResponseEntity<?> cadastrarVacina(@RequestBody VacinaDTO vacinaDTO) {
        try {
            VacinaDTO novaVacina = vacinaService.cadastrarVacina(vacinaDTO);
            return new ResponseEntity<>(novaVacina, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Erro de validação ou argumento inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Outros erros durante o cadastro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar vacina: " + e.getMessage());
        }
    }

    /**
     * Endpoint para listar todas as vacinas.
     * HTTP GET /vacinas
     *
     * @return ResponseEntity com a lista de VacinaDTOs e status HTTP 200 (OK) ou erro.
     */
    @GetMapping
    public ResponseEntity<List<VacinaDTO>> listarTodasVacinas() {
        try {
            List<VacinaDTO> vacinas = vacinaService.listarTodasVacinas();
            if (vacinas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Ou OK com lista vazia
            }
            return ResponseEntity.ok(vacinas);
        } catch (Exception e) {
            // Tratar exceções gerais se o service puder lançá-las inesperadamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint para buscar uma vacina pelo ID.
     * HTTP GET /vacinas/{id}
     *
     * @param id O ID da vacina a ser buscada.
     * @return ResponseEntity com o VacinaDTO encontrado e status HTTP 200 (OK),
     * ou status 404 (Not Found) se não encontrada, ou erro.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarVacinaPorId(@PathVariable Integer id) {
        try {
            VacinaDTO vacinaDTO = vacinaService.buscarVacinaPorId(id);
            if (vacinaDTO != null) {
                return ResponseEntity.ok(vacinaDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vacina com ID " + id + " não encontrada.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // Não esperamos outras exceções aqui se buscarPorId retornar null para não encontrado
        // mas se VacinaService.buscarVacinaPorId pudesse lançar outras exceções:
        // catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar vacina: " + e.getMessage());
        // }
    }

    /**
     * Endpoint para atualizar uma vacina existente.
     * HTTP PUT /vacinas/{id}
     *
     * @param id O ID da vacina a ser atualizada.
     * @param vacinaDTO Dados da vacina para atualização.
     * @return ResponseEntity com o VacinaDTO atualizado e status HTTP 200 (OK) ou erro.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVacina(@PathVariable Integer id, @RequestBody VacinaDTO vacinaDTO) {
        try {
            VacinaDTO vacinaAtualizada = vacinaService.atualizarVacina(id, vacinaDTO);
            return ResponseEntity.ok(vacinaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { // Pode ser uma VacinaNaoEncontradaException customizada
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar vacina: " + e.getMessage());
        }
    }

    /**
     * Endpoint para deletar uma vacina.
     * HTTP DELETE /vacinas/{id}
     *
     * @param id O ID da vacina a ser deletada.
     * @return ResponseEntity com status HTTP 204 (No Content) se sucesso, ou erro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVacina(@PathVariable Integer id) {
        try {
            vacinaService.deletarVacina(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content é comum para DELETE bem-sucedido
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { // Pode ser uma VacinaNaoEncontradaException customizada
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar vacina: " + e.getMessage());
        }
    }
}