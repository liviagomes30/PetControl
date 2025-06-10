package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.AdocaoModel;
import salvacao.petcontrol.service.AdocaoService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/adocao")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        List<AdocaoModel> listAdocao = adocaoService.getAllAdocao();
        return ResponseEntity.ok(listAdocao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAdocaoById(@PathVariable int id) {
        AdocaoModel adocao = adocaoService.getAdocaoById(id);
        if (adocao != null)
            return ResponseEntity.ok(adocao);
        return ResponseEntity.badRequest().body("Adoção não encontrada!");
    }

    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<Object> getAdocaoByAnimal(@PathVariable int idAnimal) {
        List<AdocaoModel> adocoes = adocaoService.getAdocoesByAnimal(idAnimal);
        return ResponseEntity.ok(adocoes);
    }

    @GetMapping("/adotante/{idAdotante}")
    public ResponseEntity<Object> getAdocaoByAdotante(@PathVariable int idAdotante) {
        List<AdocaoModel> adocoes = adocaoService.getAdocoesByAdotante(idAdotante);
        return ResponseEntity.ok(adocoes);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> addAdocao(@RequestBody AdocaoModel adocao) {
        try {
            AdocaoModel novaAdocao = adocaoService.addAdocao(adocao);
            return ResponseEntity.ok(novaAdocao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar adoção: " + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<Object> updateAdocao(@RequestBody AdocaoModel adocao) {
        try {
            boolean atualizada = adocaoService.updateAdocao(adocao.getIdAdocao(), adocao);
            if (atualizada) {
                return ResponseEntity.ok("Adoção alterada com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Adoção não encontrada com o ID: " + adocao.getIdAdocao());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar adoção: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAdocao(@PathVariable int id) {
        try {
            adocaoService.deleteAdocao(id);
            return ResponseEntity.ok("Adoção deletada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar adoção: " + e.getMessage());
        }
    }
}