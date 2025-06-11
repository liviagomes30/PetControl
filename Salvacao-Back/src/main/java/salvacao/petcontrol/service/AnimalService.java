package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.AnimalDAO;
import salvacao.petcontrol.model.AnimalModel;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnimalService {

    private final AnimalDAO animalDAO;

    @Autowired
    public AnimalService(AnimalDAO animalDAO) {
        this.animalDAO = animalDAO;
    }

    public List<AnimalModel> listarAnimais() {
        return animalDAO.getAll();
    }

    public AnimalModel salvarAnimal(AnimalModel animalModel) throws Exception {
        validarDataNaoFutura(animalModel.getDatanascimento());
        validarDataNaoFutura(animalModel.getDataresgate());
        validarDataMenorOuIgual(animalModel.getDatanascimento(), animalModel.getDataresgate());
        if(animalModel.getNome().isEmpty() || animalModel.getEspecie().isEmpty() || animalModel.getSexo().isEmpty()){
            throw new Exception("Dados inclompletos");
        }
        return animalDAO.gravar(animalModel);
    }

    public AnimalModel buscarAnimalPorId(Integer id) {
        return animalDAO.getId(id);
    }

    public List<AnimalModel> buscarAnimalPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return animalDAO.getNome(nome);
    }

    public AnimalModel atualizarAnimal(Integer id, AnimalModel animalDetails) throws Exception {
        validarDataNaoFutura(animalDetails.getDatanascimento());
        validarDataNaoFutura(animalDetails.getDataresgate());
        validarDataMenorOuIgual(animalDetails.getDatanascimento(),animalDetails.getDataresgate());

        AnimalModel animalExistente = animalDAO.getId(id);
        if (animalExistente != null) {
            animalExistente.setNome(animalDetails.getNome());
            animalExistente.setEspecie(animalDetails.getEspecie());
            animalExistente.setDatanascimento(animalDetails.getDatanascimento());
            animalExistente.setRaca(animalDetails.getRaca());
            animalExistente.setPorte(animalDetails.getPorte());
            animalExistente.setSexo(animalDetails.getSexo());
            animalExistente.setStatus(animalDetails.getStatus());
            animalExistente.setDataresgate(animalDetails.getDataresgate());
            animalExistente.setFoto(animalDetails.getFoto());
            animalExistente.setCastrado(animalDetails.isCastrado());
            animalExistente.setCor(animalDetails.getCor());

            if (animalDAO.alterar(animalExistente)) {
                return animalExistente;
            }
        }
        return null;
    }

    public void deletarAnimal(Integer id) throws Exception {
        AnimalModel animalExistente = animalDAO.getId(id);
        if (animalExistente == null) {
            throw new Exception("Animal com ID " + id + " não encontrado para exclusão.");
        }

        if (animalDAO.buscarAdocaoPorAnimal(id) ||
                animalDAO.buscarAgendamentoVacinacaoPorAnimal(id) ||
                animalDAO.buscarEventoPorAnimal(id) ||
                animalDAO.buscarHistoricoPorAnimal(id) ||
                animalDAO.buscarMedicacaoPorAnimal(id) ||
                animalDAO.buscarReceitaMedicamentoPorAnimal(id) ||
                animalDAO.buscarVacinacaoPorAnimal(id)) {
            throw new Exception("Animal não pode ser excluído pois possui registros associados (adoções, vacinações, etc.).");
        }

        if (!animalDAO.apagar(animalExistente)) {
            throw new Exception("Falha ao apagar o animal do banco de dados.");
        }
    }



    @Autowired
    private AnimalModel animal_Model = new AnimalModel();

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
        else
            return animal_Model.getAnimalDAO().gravar(animalModel);
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
}