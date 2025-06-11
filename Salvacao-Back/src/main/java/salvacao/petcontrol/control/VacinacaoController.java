package salvacao.petcontrol.control;

import salvacao.petcontrol.dto.VacinacaoDTO;
import salvacao.petcontrol.model.Vacinacao;
import salvacao.petcontrol.service.VacinacaoService; // A classe VacinacaoService que criamos

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacinacoes") // Define o caminho base para os endpoints desta funcionalidade
@CrossOrigin(origins = "*")    // Permite requisições de qualquer origem, ajuste conforme necessário
public class VacinacaoController {

    private final VacinacaoService vacinacaoService;

    @Autowired
    public VacinacaoController(VacinacaoService vacinacaoService) {
        this.vacinacaoService = vacinacaoService;
    }

    /**
     * Endpoint para registrar uma nova vacinação (ato).
     * HTTP POST /vacinacoes
     *
     * @param vacinacaoDTO Dados da vacinação a ser registrada, vindos no corpo da requisição.
     * @return ResponseEntity com o VacinacaoDTO registrado e status HTTP 201 (Created) ou erro.
     */
    @PostMapping
    public ResponseEntity<?> registrarVacinacao(@RequestBody VacinacaoDTO vacinacaoDTO) {
        // LOG ADICIONADO AQUI
        System.out.println("BACKEND VacinacaoController: Recebido DTO: " + vacinacaoDTO);
        if (vacinacaoDTO != null) {
            System.out.println("BACKEND DTO - idAnimal: " + vacinacaoDTO.getIdAnimal());
            System.out.println("BACKEND DTO - idVacina: " + vacinacaoDTO.getIdVacina());
            System.out.println("BACKEND DTO - dataVacinacao: " + vacinacaoDTO.getDataVacinacao());
            System.out.println("BACKEND DTO - localVacinacao: " + vacinacaoDTO.getLocalVacinacao());
            System.out.println("BACKEND DTO - dataValidadeVacina: " + vacinacaoDTO.getDataValidadeVacina());
        }
        try {
            VacinacaoDTO novaVacinacao = vacinacaoService.registrarVacinacao(vacinacaoDTO);
            return new ResponseEntity<>(novaVacinacao, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Logar a exceção no backend é importante para debugging
            System.err.println("Erro ao registrar vacinação: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao registrar vacinação: " + e.getMessage());
        }
    }

    /**
     * Endpoint para listar todos os registros de vacinação.
     * HTTP GET /vacinacoes
     *
     * @return ResponseEntity com a lista de VacinacaoDTOs e status HTTP 200 (OK).
     */

/*
    @PostMapping
    public ResponseEntity<?> registrarVacinacao(@RequestBody Vacinacao vacinacao) {
        try {
            Vacinacao novaVacinacao = vacinacaoService.registrarVacinacao(vacinacao);
            return new ResponseEntity<>(novaVacinacao, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } */

    @GetMapping
    public ResponseEntity<List<VacinacaoDTO>> listarTodasAsVacinacoes() {
        try {
            List<VacinacaoDTO> vacinacoes = vacinacaoService.listarTodasVacinacoes();
            return ResponseEntity.ok(vacinacoes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<List<Vacinacao>> listarVacinacoesPorAnimal(@PathVariable Integer idAnimal) {
        try {
            List<Vacinacao> vacinacoes = vacinacaoService.listarVacinacoesPorAnimal(idAnimal);
            return ResponseEntity.ok(vacinacoes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para buscar um registro de vacinação pelo seu ID.
     * HTTP GET /vacinacoes/{id}
     *
     * @param id O ID do registro de vacinação.
     * @return ResponseEntity com o VacinacaoDTO ou status 404 se não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarVacinacaoPorId(@PathVariable Integer id) {
        try {
            VacinacaoDTO vacinacaoDTO = vacinacaoService.buscarVacinacaoPorId(id);
            if (vacinacaoDTO != null) {
                return ResponseEntity.ok(vacinacaoDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Registro de vacinação com ID " + id + " não encontrado.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao buscar vacinação por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao buscar vacinação: " + e.getMessage());
        }
    }

    /**
     * Endpoint para atualizar um registro de vacinação existente.
     * HTTP PUT /vacinacoes/{id}
     *
     * @param id O ID do registro de vacinação a ser atualizado.
     * @param vacinacaoDTO Dados para atualização.
     * @return ResponseEntity com o VacinacaoDTO atualizado ou erro.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVacinacao(@PathVariable Integer id, @RequestBody VacinacaoDTO vacinacaoDTO) {
        try {
            VacinacaoDTO vacinacaoAtualizada = vacinacaoService.atualizarVacinacao(id, vacinacaoDTO);
            return ResponseEntity.ok(vacinacaoAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            System.err.println("Erro ao atualizar vacinação: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao atualizar vacinação: " + e.getMessage());
        }
    }

    /**
     * Endpoint para deletar um registro de vacinação.
     * HTTP DELETE /vacinacoes/{id}
     *
     * @param id O ID do registro de vacinação a ser deletado.
     * @return ResponseEntity com status 204 (No Content) ou erro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVacinacao(@PathVariable Integer id) {
        try {
            vacinacaoService.deletarVacinacao(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            System.err.println("Erro ao deletar vacinação: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao deletar vacinação: " + e.getMessage());
        }
    }




}