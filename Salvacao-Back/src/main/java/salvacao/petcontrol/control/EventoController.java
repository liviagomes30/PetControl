package salvacao.petcontrol.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salvacao.petcontrol.model.EventoModel;
import salvacao.petcontrol.service.EventoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/evento")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll() {
        try {
            List<EventoModel> listEventos = eventoService.getAllEventos();
            return ResponseEntity.ok(listEventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar eventos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEventoById(@PathVariable int id) {
        try {
            EventoModel evento = eventoService.getEventoById(id);
            if (evento != null) {
                return ResponseEntity.ok(evento);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Evento não encontrado!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar evento: " + e.getMessage());
        }
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<Object> getEventosByData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            List<EventoModel> eventos = eventoService.getEventosByData(data);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar eventos por data: " + e.getMessage());
        }
    }

    @GetMapping("/animal/{idAnimal}")
    public ResponseEntity<Object> getEventosByAnimal(@PathVariable int idAnimal) {
        try {
            List<EventoModel> eventos = eventoService.getEventosByAnimal(idAnimal);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar eventos por animal: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getEventosByStatus(@PathVariable String status) {
        try {
            List<EventoModel> eventos = eventoService.getEventosByStatus(status);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar eventos por status: " + e.getMessage());
        }
    }

    @GetMapping("/proximos")
    public ResponseEntity<Object> getEventosProximos() {
        try {
            List<EventoModel> eventos = eventoService.getEventosProximos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar eventos próximos: " + e.getMessage());
        }
    }

    @GetMapping("/passados")
    public ResponseEntity<Object> getEventosPassados() {
        try {
            List<EventoModel> eventos = eventoService.getEventosPassados();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar eventos passados: " + e.getMessage());
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> addEvento(@RequestBody EventoModel evento) {
        try {
            EventoModel novoEvento = eventoService.addEvento(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao adicionar evento: " + e.getMessage());
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<Object> updateEvento(@RequestBody EventoModel evento) {
        try {
            boolean atualizado = eventoService.updateEvento(evento.getIdEvento(), evento);
            if (atualizado) {
                return ResponseEntity.ok("Evento alterado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Evento não encontrado com o ID: " + evento.getIdEvento());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao atualizar evento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEvento(@PathVariable int id) {
        try {
            eventoService.deleteEvento(id);
            return ResponseEntity.ok("Evento deletado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao deletar evento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Object> updateStatus(@PathVariable int id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            boolean atualizado = eventoService.updateStatus(id, status);
            if (atualizado) {
                return ResponseEntity.ok("Status do evento alterado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar status do evento: " + e.getMessage());
        }
    }
}