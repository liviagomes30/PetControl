package salvacao.petcontrol.service;

import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.AdocaoModel;
import salvacao.petcontrol.util.Validation;

import java.util.List;

@Service
public class AdocaoService {

    private AdocaoModel adocaoModel;

    public AdocaoService() {
        this.adocaoModel = new AdocaoModel();
    }

    public AdocaoModel addAdocao(AdocaoModel adocao) throws Exception {
        // Validações
        if (!Validation.isDataAdocaoValida(adocao.getDataAdocao()))
            throw new Exception("Data de adoção inválida!");

        if (!Validation.isAnimalDisponivel(adocao.getIdAnimal()))
            throw new Exception("Animal não está disponível para adoção!");

        if (!Validation.isAdotanteValido(adocao.getIdAdotante()))
            throw new Exception("Adotante inválido!");

        // Verifica se o animal já foi adotado
        List<AdocaoModel> adocoesAnimal = adocaoModel.getDAL().findByAnimal(adocao.getIdAnimal());
        if (!adocoesAnimal.isEmpty()) {
            throw new Exception("Este animal já foi adotado!");
        }

        AdocaoModel adocaoFinal = adocaoModel.getDAL().addAdocao(adocao);
        return adocaoFinal;
    }

    public AdocaoModel getAdocaoById(int id) {
        return adocaoModel.getDAL().findById(id);
    }

    public List<AdocaoModel> getAdocoesByAnimal(int idAnimal) {
        return adocaoModel.getDAL().findByAnimal(idAnimal);
    }

    public List<AdocaoModel> getAdocoesByAdotante(int idAdotante) {
        return adocaoModel.getDAL().findByAdotante(idAdotante);
    }

    public boolean updateAdocao(int id, AdocaoModel adocao) throws Exception {
        if (!Validation.isDataAdocaoValida(adocao.getDataAdocao()))
            throw new Exception("Data de adoção inválida!");

        if (adocaoModel.getDAL().findById(id) == null)
            throw new Exception("Adoção não encontrada!");

        return adocaoModel.getDAL().updateAdocao(id, adocao);
    }

    public void deleteAdocao(int id) throws Exception {
        AdocaoModel adocaoDelete = adocaoModel.getDAL().findById(id);

        if (adocaoDelete == null)
            throw new Exception("Adoção não encontrada!");

        if (!adocaoModel.getDAL().deleteAdocao(id))
            throw new Exception("Erro ao deletar adoção!");
    }

    public List<AdocaoModel> getAllAdocao() {
        return adocaoModel.getDAL().getAll();
    }
}