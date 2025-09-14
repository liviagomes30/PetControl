package salvacao.petcontrol.service;

import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.PreferenciaPorteModel;

public interface Observer {
    void update(PreferenciaPorteModel preferenciaPorte, AnimalModel animal);
}
