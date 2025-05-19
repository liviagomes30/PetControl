package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.AnimalDAL;
import salvacao.petcontrol.model.AnimalModel;

import java.util.List;

@Service
public class AnimalService {
    @Autowired
    private AnimalDAL animalDAL;

    public AnimalModel getId(Integer id){
        return animalDAL.getId(id);
    }

    public List<AnimalModel> getAll(){
        return animalDAL.getAll();
    }
    public List<AnimalModel> getNome(String filtro){
        return animalDAL.getNome(filtro);
    }
    public List<AnimalModel> getEspecie(String filtro){
        return animalDAL.getEspecie(filtro);
    }
    public List<AnimalModel> getRaca(String filtro){
        return animalDAL.getRaca(filtro);
    }

    public AnimalModel addAnimal(AnimalModel animalModel) throws Exception{
        if(animalModel.getNome() == null || animalModel.getEspecie() == null || animalModel.getSexo() == null){
            throw new Exception("Dados inclompletos");
        }
        else
            return animalDAL.gravar(animalModel);
    }

    public boolean uptAnimal(AnimalModel animalModel) throws Exception{
        if(animalModel.getNome() == null || animalModel.getEspecie() == null || animalModel.getSexo() == null){
            throw new Exception("Dados inclompletos");
        }
        else
            return animalDAL.alterar(animalModel);
    }

    public boolean apagarAnimal(Integer id) throws Exception{
        AnimalModel animal = animalDAL.getId(id);
        if(animal != null) {
            if (animalDAL.buscarAdocaoPorAnimal(id))
                throw new Exception("Animal encontrado em adoção");
            if (animalDAL.buscarAgendamentoVacinacaoPorAnimal(id))
                throw new Exception("Animal encontrado em agendamento de vacinação");
            if (animalDAL.buscarEventoPorAnimal(id))
                throw new Exception("Animal encontrado em eventos");
            if (animalDAL.buscarHistoricoPorAnimal(id))
                throw new Exception("Animal possui historico");
            if (animalDAL.buscarMedicacaoPorAnimal(id))
                throw new Exception("Animal encontrado em medicação");
            if (animalDAL.buscarReceitaMedicamentoPorAnimal(id))
                throw new Exception("Animal possui receita de medicameto");
            if (animalDAL.buscarVacinacaoPorAnimal(id))
                throw new Exception("Animal encontrado em vacinação");

            return animalDAL.apagar(animal);
        }
        else
            throw new Exception("Animal não encontrado");
    }
}
