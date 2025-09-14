package salvacao.petcontrol.service;

import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.EntradaProdutoModel;
import salvacao.petcontrol.service.template.EntradaProdutoOperacao;
import salvacao.petcontrol.util.ResultadoOperacao;

@Service
public class EntradaProdutoServiceV2 extends EntradaProdutoService {

    private final EntradaProdutoOperacao entradaProdutoOperacao;

    public EntradaProdutoServiceV2() {
        this.entradaProdutoOperacao = new EntradaProdutoOperacao();
    }

    /**
     * Novo método para registrar uma entrada de produtos usando o Template Method.
     */
    public ResultadoOperacao addRegistroComTemplate(EntradaProdutoModel registro) throws Exception {
        return entradaProdutoOperacao.executarOperacao(registro);
    }
}