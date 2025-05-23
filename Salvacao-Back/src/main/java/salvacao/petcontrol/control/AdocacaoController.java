//package salvacao.petcontrol.control;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import salvacao.petcontrol.model.AdocaoModel;
//import salvacao.petcontrol.service.AdocaoService;
//
//import java.util.List;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping("/adocao")
//public class AdocaoController {
//
//    private final AdocaoService adocaoService;
//
//    public AdocaoController(AdocaoService adocaoService) {
//        this.adocaoService = adocaoService;
//    }
//
//    @GetMapping("/listar")
//    public ResponseEntity<Object> getAll() {
//        List<AdocaoModel> listAdocao = adocaoService.getAllAdocao();
//        return ResponseEntity.ok(listAdocao); // lista vazia é uma resposta válida
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> getAdocaoById(@PathVariable Integer id) {
//        AdocaoModel adocao = adocaoService.getAdocaoById(id);
//        if(adocao != null)
//            return ResponseEntity.ok(adocao);
//        return ResponseEntity.badRequest().body("Adoção não encontrada!");
//    }
//
//    @GetMapping("/pessoa/{cpf}")
//    public ResponseEntity<Object> getAdocoesPorPessoa(@PathVariable String cpf) {
//        try {
//            List<AdocaoModel> adocoes = adocaoService.getAdocoesPorPessoa(cpf);
//            return ResponseEntity.ok(adocoes);
//        } catch(Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao buscar adoções da pessoa: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/animal/{idAnimal}")
//    public ResponseEntity<Object> getAdocoesPorAnimal(@PathVariable Integer idAnimal) {
//        try {
//            List<AdocaoModel> adocoes = adocaoService.getAdocoesPorAnimal(idAnimal);
//            return ResponseEntity.ok(adocoes);
//        } catch(Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao buscar adoções do animal: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/status/{status}")
//    public ResponseEntity<Object> getAdocoesPorStatus(@PathVariable String status) {
//        try {
//            List<AdocaoModel> adocoes = adocaoService.getAdocoesPorStatus(status);
//            return ResponseEntity.ok(adocoes);
//        } catch(Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao buscar adoções por status: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/cadastro")
//    public ResponseEntity<Object> addAdocao(@RequestBody AdocaoModel adocao) {
//        try {
//            AdocaoModel novaAdocao = adocaoService.addAdocao(adocao);
//            return ResponseEntity.ok(novaAdocao);
//        } catch(Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao adicionar adoção: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/alterar")
//    public ResponseEntity<Object> updateAdocao(@RequestBody AdocaoModel adocao) {
//        try {
//            boolean atualizada = adocaoService.updateAdocao(adocao.getIdAdocao(), adocao);
//            if (atualizada) {
//                return ResponseEntity.ok("Adoção alterada com sucesso!");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Adoção não encontrada com o ID: " + adocao.getIdAdocao());
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Erro ao atualizar adoção: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/status/{id}")
//    public ResponseEntity<Object> updateStatusAdocao(@PathVariable Integer id, @RequestBody String novoStatus) {
//        try {
//            boolean atualizada = adocaoService.updateStatusAdocao(id, novoStatus);
//            if (atualizada) {
//                return ResponseEntity.ok("Status da adoção alterado com sucesso!");
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Adoção não encontrada com o ID: " + id);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao atualizar status da adoção: " + e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> deleteAdocao(@PathVariable Integer id) {
//        try {
//            adocaoService.deleteAdocao(id);
//            return ResponseEntity.ok("Adoção deletada com sucesso!");
//        } catch(Exception e) {
//            return ResponseEntity.badRequest().body("Erro ao deletar adoção: " + e.getMessage());
//        }
//    }
//}