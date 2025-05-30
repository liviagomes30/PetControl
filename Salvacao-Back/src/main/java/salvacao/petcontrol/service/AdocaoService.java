package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.AdocaoDAL;
import salvacao.petcontrol.model.AdocaoModel;
import salvacao.petcontrol.util.Validation;

import java.util.List;

@Service
public class AdocaoService {

    @Autowired
    private AdocaoDAL adocaoDAL;

    public AdocaoModel addAdocao(AdocaoModel adocao) throws Exception {
        // Validações
        if (!Validation.isDataAdocaoValida(adocao.getDataAdocao()))
            throw new Exception("Data de adoção inválida!");

        if (!Validation.isAnimalDisponivel(adocao.getIdAnimal()))
            throw new Exception("Animal não está disponível para adoção!");

        if (!Validation.isAdotanteValido(adocao.getIdAdotante()))
            throw new Exception("Adotante inválido!");

        // Verifica se o animal já foi adotado
        List<AdocaoModel> adocoesAnimal = adocaoDAL.findByAnimal(adocao.getIdAnimal());
        if (!adocoesAnimal.isEmpty()) {
            throw new Exception("Este animal já foi adotado!");
        }

        AdocaoModel adocaoFinal = adocaoDAL.addAdocao(adocao);
        return adocaoFinal;
    }

    public AdocaoModel getAdocaoById(int id) {
        return adocaoDAL.findById(id);
    }

    public List<AdocaoModel> getAdocoesByAnimal(int idAnimal) {
        return adocaoDAL.findByAnimal(idAnimal);
    }

    public List<AdocaoModel> getAdocoesByAdotante(int idAdotante) {
        return adocaoDAL.findByAdotante(idAdotante);
    }

    public boolean updateAdocao(int id, AdocaoModel adocao) throws Exception {
        if (!Validation.isDataAdocaoValida(adocao.getDataAdocao()))
            throw new Exception("Data de adoção inválida!");

        if (adocaoDAL.findById(id) == null)
            throw new Exception("Adoção não encontrada!");

        return adocaoDAL.updateAdocao(id, adocao);
    }

    public void deleteAdocao(int id) throws Exception {
        AdocaoModel adocaoDelete = adocaoDAL.findById(id);

        if (adocaoDelete == null)
            throw new Exception("Adoção não encontrada!");

        if (!adocaoDAL.deleteAdocao(id))
            throw new Exception("Erro ao deletar adoção!");
    }

    public List<AdocaoModel> getAllAdocao() {
        return adocaoDAL.getAll();
    }
}