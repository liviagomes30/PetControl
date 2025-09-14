package salvacao.petcontrol.service;

import org.springframework.stereotype.Service;
import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.service.template.AcertoEstoqueOperacao;
import salvacao.petcontrol.util.ResultadoOperacao;


@Service
public class AcertoEstoqueServiceV2 extends AcertoEstoqueService {

    private final AcertoEstoqueOperacao acertoEstoqueOperacao;


    public AcertoEstoqueServiceV2() {
        this.acertoEstoqueOperacao = new AcertoEstoqueOperacao();
    }


    public ResultadoOperacao gravarComTemplate(AcertoEstoqueRequestDTO request) throws Exception {
        return acertoEstoqueOperacao.executarOperacao(request);
    }

}