package salvacao.petcontrol.service.template;

import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.model.*;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AcertoEstoqueOperacao extends OperacaoEstoqueTemplate {

    private final AcertoEstoqueModel acertoEstoqueModel = new AcertoEstoqueModel();
    private final EstoqueModel estoqueModel = new EstoqueModel();
    private final UsuarioModel usuarioModel = new UsuarioModel();

    public static class DadosAcertoProcessados {
        public AcertoEstoqueModel acerto;
        public List<ItemAcertoProcessado> itens;

        public static class ItemAcertoProcessado {
            public ItemAcertoEstoqueModel item;
            public EstoqueModel estoqueAtual;
        }
    }

    @Override
    protected void validarDados(Object dados) throws Exception {
        super.validarDados(dados);

        if (!(dados instanceof AcertoEstoqueRequestDTO)) {
            throw new Exception("Dados devem ser do tipo AcertoEstoqueRequestDTO");
        }

        AcertoEstoqueRequestDTO request = (AcertoEstoqueRequestDTO) dados;

        if (request.getUsuario_pessoa_id() == null) {
            throw new Exception("Usuário é obrigatório");
        }

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new Exception("Motivo é obrigatório");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new Exception("É necessário ao menos um item para acerto de estoque");
        }
    }

    @Override
    protected void validarRegrasNegocio(Object dados, Connection conn) throws Exception {
        AcertoEstoqueRequestDTO request = (AcertoEstoqueRequestDTO) dados;

        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(request.getUsuario_pessoa_id());
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }

        for (AcertoEstoqueRequestDTO.ItemAcertoRequestDTO item : request.getItens()) {
            EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(item.getProduto_id());
            if (estoque == null) {
                throw new Exception("Produto ID " + item.getProduto_id() + " não encontrado no estoque");
            }

            if (item.getQuantidade_nova() == null || item.getQuantidade_nova().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Quantidade deve ser maior ou igual a zero para o produto ID " + item.getProduto_id());
            }
        }
    }

    @Override
    protected Object prepararDadosOperacao(Object dados, Connection conn) throws Exception {
        AcertoEstoqueRequestDTO request = (AcertoEstoqueRequestDTO) dados;

        AcertoEstoqueModel acerto = new AcertoEstoqueModel();
        acerto.setData(LocalDate.now());
        acerto.setUsuario_pessoa_id(request.getUsuario_pessoa_id());
        acerto.setMotivo(request.getMotivo());
        acerto.setObservacao(request.getObservacao());

        List<DadosAcertoProcessados.ItemAcertoProcessado> itensProcessados = new ArrayList<>();

        for (AcertoEstoqueRequestDTO.ItemAcertoRequestDTO itemRequest : request.getItens()) {
            EstoqueModel estoqueAtual = estoqueModel.getEstDAO().getByProdutoId(itemRequest.getProduto_id());

            ItemAcertoEstoqueModel item = new ItemAcertoEstoqueModel();
            item.setProduto_id(itemRequest.getProduto_id());
            item.setQuantidade_depois(itemRequest.getQuantidade_nova());
            item.setQuantidade_antes(estoqueAtual.getQuantidade());

            if (item.getQuantidade_depois().compareTo(item.getQuantidade_antes()) > 0) {
                item.setTipoajuste("ENTRADA");
            } else {
                item.setTipoajuste("SAIDA");
            }

            DadosAcertoProcessados.ItemAcertoProcessado itemProcessado =
                    new DadosAcertoProcessados.ItemAcertoProcessado();
            itemProcessado.item = item;
            itemProcessado.estoqueAtual = estoqueAtual;

            itensProcessados.add(itemProcessado);
        }

        DadosAcertoProcessados dadosProcessados = new DadosAcertoProcessados();
        dadosProcessados.acerto = acerto;
        dadosProcessados.itens = itensProcessados;

        return dadosProcessados;
    }

    @Override
    protected Object executarOperacaoPrincipal(Object dadosProcessados, Connection conn) throws Exception {
        DadosAcertoProcessados dados = (DadosAcertoProcessados) dadosProcessados;

        AcertoEstoqueModel acertoInserido = acertoEstoqueModel.getAcDAO().gravarAcerto(dados.acerto, conn);

        for (DadosAcertoProcessados.ItemAcertoProcessado itemProcessado : dados.itens) {
            itemProcessado.item.setAcerto_id(acertoInserido.getIdacerto());
            acertoEstoqueModel.getAcDAO().gravarItemAcerto(itemProcessado.item, conn);
        }

        return acertoInserido;
    }

    @Override
    protected void atualizarEstoque(Object dadosProcessados, Object resultadoOperacao, Connection conn) throws Exception {
        DadosAcertoProcessados dados = (DadosAcertoProcessados) dadosProcessados;

        for (DadosAcertoProcessados.ItemAcertoProcessado itemProcessado : dados.itens) {
            EstoqueModel estoqueAtualizado = new EstoqueModel(
                    itemProcessado.estoqueAtual.getIdestoque(),
                    itemProcessado.item.getProduto_id(),
                    itemProcessado.item.getQuantidade_depois()
            );

            boolean estoqueUpdated = estoqueModel.getEstDAO().alterar(estoqueAtualizado);
            if (!estoqueUpdated) {
                throw new Exception("Falha ao atualizar estoque para o produto: " + itemProcessado.item.getProduto_id());
            }
        }
    }

    @Override
    protected ResultadoOperacao criarResultadoSucesso(Object resultadoOperacao) {
        AcertoEstoqueModel acertoInserido = (AcertoEstoqueModel) resultadoOperacao;

        ResultadoOperacao resultado = new ResultadoOperacao();
        resultado.setOperacao("acertoEstoque");
        resultado.setSucesso(true);
        resultado.setMensagem("Acerto de estoque realizado com sucesso. ID: " + acertoInserido.getIdacerto());

        return resultado;
    }
}