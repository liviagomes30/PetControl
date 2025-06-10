package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.EventoModal;

public class EventoCompletoDTO {

    public EventoModal evento;
    public AnimalModel animal;

    public EventoCompletoDTO() {
    }

    public EventoCompletoDTO(EventoModal evento, AnimalModel animal) {
        this.evento = evento;
        this.animal = animal;
    }

    public EventoModal getEvento() {
        return evento;
    }

    public void setEvento(EventoModal evento) {
        this.evento = evento;
    }

    public AnimalModel getAnimal() {
        return animal;
    }

    public void setAnimal(AnimalModel animal) {
        this.animal = animal;
    }
}
