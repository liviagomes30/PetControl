package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.PreferenciaPorteModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalService implements Subject{
    @Autowired
    private AnimalModel animal_Model = new AnimalModel();

    @Autowired
    private PreferenciaPorteModel preferenciaPorteModel = new PreferenciaPorteModel();

    private PessoaService observer;

    private List<PreferenciaPorteModel> listaPreferenciaPorte;

    public AnimalModel getId(Integer id){
        return animal_Model.getAnimalDAO().getId(id);
    }

    public List<AnimalModel> getAll(){
        return animal_Model.getAnimalDAO().getAll();
    }
    public List<AnimalModel> getNome(String filtro){
        return animal_Model.getAnimalDAO().getNome(filtro);
    }
    public List<AnimalModel> getEspecie(String filtro){
        return animal_Model.getAnimalDAO().getEspecie(filtro);
    }
    public List<AnimalModel> getRaca(String filtro){
        return animal_Model.getAnimalDAO().getRaca(filtro);
    }

    public AnimalModel addAnimal(AnimalModel animalModel) throws Exception{
        validarDataNaoFutura(animalModel.getDatanascimento());
        validarDataNaoFutura(animalModel.getDataresgate());
        validarDataMenorOuIgual(animalModel.getDatanascimento(),animalModel.getDataresgate());
        if(animalModel.getNome().isEmpty() || animalModel.getEspecie().isEmpty() || animalModel.getSexo().isEmpty()){
            throw new Exception("Dados inclompletos");
        }
        else {
            if(!animalModel.getPorte().isEmpty()) {
                int porte;

                if (animalModel.getPorte().equals("Pequeno"))
                    porte = 1;
                else
                    if(animalModel.getPorte().equals("Médio"))
                        porte = 2;
                    else
                        porte = 3;

                listaPreferenciaPorte = preferenciaPorteModel.getPreferenciaPorteDAO().buscarPorte(porte);
            }

            AnimalModel animal = animal_Model.getAnimalDAO().gravar(animalModel);
            if(!animalModel.getPorte().isEmpty())
                notificarObservers(animal);
            return animal;
        }
    }

    public boolean uptAnimal(AnimalModel animalModel) throws Exception{
        validarDataNaoFutura(animalModel.getDatanascimento());
        validarDataNaoFutura(animalModel.getDataresgate());
        validarDataMenorOuIgual(animalModel.getDatanascimento(),animalModel.getDataresgate());
        if(animalModel.getNome().isEmpty() || animalModel.getEspecie().isEmpty() || animalModel.getSexo().isEmpty()){
            throw new Exception("Dados inclompletos");
        }
        else
            return animal_Model.getAnimalDAO().alterar(animalModel);
    }

    public boolean apagarAnimal(Integer id) throws Exception{
        AnimalModel animal = animal_Model.getAnimalDAO().getId(id);
        if(animal != null) {
            if (animal_Model.getAnimalDAO().buscarAdocaoPorAnimal(id))
                throw new Exception("Animal encontrado em adoção");
            if (animal_Model.getAnimalDAO().buscarAgendamentoVacinacaoPorAnimal(id))
                throw new Exception("Animal encontrado em agendamento de vacinação");
            if (animal_Model.getAnimalDAO().buscarEventoPorAnimal(id))
                throw new Exception("Animal encontrado em eventos");
            if (animal_Model.getAnimalDAO().buscarHistoricoPorAnimal(id))
                throw new Exception("Animal possui historico");
            if (animal_Model.getAnimalDAO().buscarMedicacaoPorAnimal(id))
                throw new Exception("Animal encontrado em medicação");
            if (animal_Model.getAnimalDAO().buscarReceitaMedicamentoPorAnimal(id))
                throw new Exception("Animal possui receita de medicameto");
            if (animal_Model.getAnimalDAO().buscarVacinacaoPorAnimal(id))
                throw new Exception("Animal encontrado em vacinação");

            return animal_Model.getAnimalDAO().apagar(animal);
        }
        else
            throw new Exception("Animal não encontrado");
    }

    private void validarDataNaoFutura(LocalDate data) throws Exception{
        LocalDate hoje = LocalDate.now();
        if (data != null && data.isAfter(hoje)) {
            throw new Exception("Erro: A data não pode ser no futuro.");
        }
    }

    private void validarDataMenorOuIgual(LocalDate data1, LocalDate data2) throws Exception{
        if (data1 != null && data2 != null && data1.isAfter(data2)) {
            throw new Exception("Erro: A data de nascimento não pode ser maior que a data de resgate.");
        }
    }

    @Override
    public void registrarObserver(PreferenciaPorteModel p) {
        if(!preferenciaPorteModel.getPreferenciaPorteDAO().buscarPessoaPorte(p))
            preferenciaPorteModel.getPreferenciaPorteDAO().gravar(p);
    }

    @Override
    public void removerObserver(PreferenciaPorteModel p) {
        if(preferenciaPorteModel.getPreferenciaPorteDAO().buscarPessoaPorte(p)) {
            preferenciaPorteModel.getPreferenciaPorteDAO().apagar(p);
        }
    }

    @Override
    public void notificarObservers(AnimalModel animal) {
        observer = new PessoaService();
        for(PreferenciaPorteModel p : listaPreferenciaPorte)
            observer.update(p,animal);
    }
}
