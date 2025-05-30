package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.EventoDAO;
import salvacao.petcontrol.dao.EventoDAO;
import salvacao.petcontrol.model.EventoModel;
import salvacao.petcontrol.util.Validation;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoDAO eventoDAL;

    public EventoModel addEvento(EventoModel evento) throws Exception {
        // Validações básicas
        if (evento.getDescricao() == null || evento.getDescricao().trim().isEmpty()) {
            throw new Exception("Descrição do evento é obrigatória!");
        }

        if (!Validation.isDataEventoValida(evento.getData())) {
            throw new Exception("Data do evento é inválida!");
        }

        if (evento.getLocal() == null || evento.getLocal().trim().isEmpty()) {
            throw new Exception("Local do evento é obrigatório!");
        }

        if (evento.getResponsavel() == null || evento.getResponsavel().trim().isEmpty()) {
            throw new Exception("Responsável pelo evento é obrigatório!");
        }

        if (evento.getStatus() == null || evento.getStatus().trim().isEmpty()) {
            evento.setStatus("Planejado"); // Status padrão
        }

        // Validar animal (se informado)
        if (!Validation.isAnimalValidoParaEvento(evento.getAnimalIdAnimal())) {
            throw new Exception("Animal informado é inválido!");
        }

        return eventoDAL.addEvento(evento);
    }

    public EventoModel getEventoById(int id) {
        return eventoDAL.findById(id);
    }

    public List<EventoModel> getEventosByData(LocalDate data) {
        return eventoDAL.findByData(data);
    }

    public List<EventoModel> getEventosByAnimal(int animalId) {
        return eventoDAL.findByAnimal(animalId);
    }

    public List<EventoModel> getEventosByStatus(String status) {
        return eventoDAL.findByStatus(status);
    }

    public boolean updateEvento(int id, EventoModel evento) throws Exception {
        // Validações básicas
        if (evento.getDescricao() == null || evento.getDescricao().trim().isEmpty()) {
            throw new Exception("Descrição do evento é obrigatória!");
        }

        if (!Validation.isDataEventoValida(evento.getData())) {
            throw new Exception("Data do evento é inválida!");
        }

        if (evento.getLocal() == null || evento.getLocal().trim().isEmpty()) {
            throw new Exception("Local do evento é obrigatório!");
        }

        if (evento.getResponsavel() == null || evento.getResponsavel().trim().isEmpty()) {
            throw new Exception("Responsável pelo evento é obrigatório!");
        }

        // Verificar se o evento existe
        if (eventoDAL.findById(id) == null) {
            throw new Exception("Evento não encontrado!");
        }

        // Validar animal (se informado)
        if (!Validation.isAnimalValidoParaEvento(evento.getAnimalIdAnimal())) {
            throw new Exception("Animal informado é inválido!");
        }

        return eventoDAL.updateEvento(id, evento);
    }

    public void deleteEvento(int id) throws Exception {
        EventoModel eventoDelete = eventoDAL.findById(id);

        if (eventoDelete == null) {
            throw new Exception("Evento não encontrado!");
        }

        if (!eventoDAL.deleteEvento(id)) {
            throw new Exception("Erro ao deletar evento!");
        }
    }

    public List<EventoModel> getAllEventos() {
        return eventoDAL.getAll();
    }

    public List<EventoModel> getEventosProximos() {
        // Retorna eventos com data >= hoje
        return eventoDAL.getAll().stream()
                .filter(evento -> evento.getData() != null &&
                        !evento.getData().isBefore(LocalDate.now()))
                .toList();
    }

    public List<EventoModel> getEventosPassados() {
        // Retorna eventos com data < hoje
        return eventoDAL.getAll().stream()
                .filter(evento -> evento.getData() != null &&
                        evento.getData().isBefore(LocalDate.now()))
                .toList();
    }
}