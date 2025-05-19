package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.service.AnimalService;
import salvacao.petcontrol.util.Erro;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/animais")
public class AnimalController {
    @Autowired
    private AnimalService animalService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getId(@PathVariable Integer id){
        AnimalModel animalModel = animalService.getId(id);
        if(animalModel != null)
            return ResponseEntity.ok(animalModel);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Animal encontrado");
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(){
        List<AnimalModel> animalModelList = animalService.getAll();
        if(!animalModelList.isEmpty())
            return ResponseEntity.ok(animalModelList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Animal encontrado");
    }

    @GetMapping("/nome/{filtro}")
    public ResponseEntity<Object> getNome(@PathVariable String filtro){
        List<AnimalModel> animalModelList = animalService.getNome(filtro);
        if(!animalModelList.isEmpty())
            return ResponseEntity.ok(animalModelList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Animal encontrado");
    }

    @GetMapping("/especie/{filtro}")
    public ResponseEntity<Object> getEspecie(@PathVariable String filtro){
        List<AnimalModel> animalModelList = animalService.getEspecie(filtro);
        if(!animalModelList.isEmpty())
            return ResponseEntity.ok(animalModelList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Animal encontrado");
    }

    @GetMapping("/raca/{filtro}")
    public ResponseEntity<Object> getRaca(@PathVariable String filtro){
        List<AnimalModel> animalModelList = animalService.getRaca(filtro);
        if(!animalModelList.isEmpty())
            return ResponseEntity.ok(animalModelList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Animal encontrado");
    }

    @PostMapping()
    public ResponseEntity<Object> addAnimal(@RequestBody AnimalModel animal){
        try{
            AnimalModel novoAnimal = animalService.addAnimal(animal);
            return ResponseEntity.ok(novoAnimal);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> uptAnimal(@RequestBody AnimalModel animal){
        try{
            boolean atualizado = animalService.uptAnimal(animal);
            if (atualizado) {
                return ResponseEntity.ok("Animal atualizado com sucesso");
            }
            else
                return ResponseEntity.badRequest().body("Erro ao atualizar animal");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        try {
            System.out.println("Controller: "+id);
            boolean deletado = animalService.apagarAnimal(id);
            if (deletado) {
                return ResponseEntity.ok("Animal excluído com sucesso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao excluir o animal");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
