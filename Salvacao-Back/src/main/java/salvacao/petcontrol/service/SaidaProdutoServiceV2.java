package salvacao.petcontrol.service;

import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.EntradaProdutoModel;
import salvacao.petcontrol.service.template.SaidaProdutoOperacao;
import salvacao.petcontrol.util.ResultadoOperacao;

@Service
public class SaidaProdutoServiceV2 extends SaidaProdutoService {

    private final SaidaProdutoOperacao saidaProdutoOperacao;

    public SaidaProdutoServiceV2() {
        this.saidaProdutoOperacao = new SaidaProdutoOperacao();
    }

    public ResultadoOperacao registrarSaidaComTemplate(EntradaProdutoModel registro) throws Exception {
        return saidaProdutoOperacao.executarOperacao(registro);
    }
}