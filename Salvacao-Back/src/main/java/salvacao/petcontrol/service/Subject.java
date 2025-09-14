package salvacao.petcontrol.service;

import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.PreferenciaPorteModel;

public interface Subject {
    void registrarObserver(PreferenciaPorteModel p);
    void removerObserver(PreferenciaPorteModel p);
    void notificarObservers(AnimalModel animal);
}
