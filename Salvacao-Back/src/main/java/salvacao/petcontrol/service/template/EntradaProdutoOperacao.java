package salvacao.petcontrol.service.template;

import salvacao.petcontrol.model.*;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class EntradaProdutoOperacao extends OperacaoEstoqueTemplate {

    private final MovimentacaoEstoqueModel movimentacaoEstoqueModel = new MovimentacaoEstoqueModel();
    private final ItemMovimentacaoModel itemMovimentacaoModel = new ItemMovimentacaoModel();
    private final MotivoMovimentacaoModel motivoMovimentacaoModel = new MotivoMovimentacaoModel();
    private final EstoqueModel estoqueModel = new EstoqueModel();
    private final UsuarioModel usuarioModel = new UsuarioModel();

    public static class DadosEntradaProcessados {
        public MovimentacaoEstoqueModel movimentacao;
        public List<ItemEntradaProcessado> itens;
        public MotivoMovimentacaoModel motivo;

        public static class ItemEntradaProcessado {
            public ItemMovimentacaoModel item;
            public EstoqueModel estoqueAtual;
        }
    }

    @Override
    protected void validarDados(Object dados) throws Exception {
        super.validarDados(dados);

        if (!(dados instanceof EntradaProdutoModel)) {
            throw new Exception("Dados devem ser do tipo EntradaProdutoModel");
        }

        EntradaProdutoModel registro = (EntradaProdutoModel) dados;

        if (registro.getItens() == null || registro.getItens().isEmpty()) {
            throw new Exception("É necessário adicionar ao menos um item");
        }

        if (registro.getUsuarioId() == null) {
            throw new Exception("Usuário é obrigatório");
        }
    }

    @Override
    protected void validarRegrasNegocio(Object dados, Connection conn) throws Exception {
        EntradaProdutoModel registro = (EntradaProdutoModel) dados;

        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(registro.getUsuarioId());
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }

        for (ItensEntrada item : registro.getItens()) {
            if (item.getQuantidade() <= 0) {
                throw new Exception("Quantidade deve ser maior que zero para o produto " + item.getNome());
            }

            EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(item.getProdutoId());
            if (estoque == null) {
                throw new Exception("Produto ID " + item.getProdutoId() + " não encontrado no estoque");
            }
        }
    }

    @Override
    protected Object prepararDadosOperacao(Object dados, Connection conn) throws Exception {
        EntradaProdutoModel registro = (EntradaProdutoModel) dados;

        MotivoMovimentacaoModel motivo = motivoMovimentacaoModel.getMotivoMovimentacaoDAO().getTipo("ENTRADA");
        if (motivo == null) {
            throw new Exception("Motivo de movimentação 'ENTRADA' não encontrado");
        }

        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(registro.getUsuarioId());

        MovimentacaoEstoqueModel movimentacao = new MovimentacaoEstoqueModel(
                motivo.getTipo(),
                registro.getData(),
                usuario.getPessoa_idpessoa(),
                registro.getObservacao(),
                "----"
        );

        List<DadosEntradaProcessados.ItemEntradaProcessado> itensProcessados = new ArrayList<>();

        for (ItensEntrada itemEntrada : registro.getItens()) {
            EstoqueModel estoqueAtual = estoqueModel.getEstDAO().getByProdutoId(itemEntrada.getProdutoId());

            ItemMovimentacaoModel item = new ItemMovimentacaoModel(
                    null, // será definido após gravar a movimentação
                    itemEntrada.getProdutoId(),
                    itemEntrada.getQuantidade(),
                    motivo.getIdmotivo()
            );

            DadosEntradaProcessados.ItemEntradaProcessado itemProcessado =
                    new DadosEntradaProcessados.ItemEntradaProcessado();
            itemProcessado.item = item;
            itemProcessado.estoqueAtual = estoqueAtual;

            itensProcessados.add(itemProcessado);
        }

        DadosEntradaProcessados dadosProcessados = new DadosEntradaProcessados();
        dadosProcessados.movimentacao = movimentacao;
        dadosProcessados.itens = itensProcessados;
        dadosProcessados.motivo = motivo;

        return dadosProcessados;
    }

    @Override
    protected Object executarOperacaoPrincipal(Object dadosProcessados, Connection conn) throws Exception {
        DadosEntradaProcessados dados = (DadosEntradaProcessados) dadosProcessados;

        MovimentacaoEstoqueModel novaMovimentacao = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().gravar(dados.movimentacao);
        if (novaMovimentacao == null) {
            throw new Exception("Erro ao gravar movimentação");
        }

        for (DadosEntradaProcessados.ItemEntradaProcessado itemProcessado : dados.itens) {
            itemProcessado.item.setMovimentacaoId(novaMovimentacao.getIdmovimentacao());

            ItemMovimentacaoModel novoItem = itemMovimentacaoModel.getItemMovimentacaoDAO().gravar(itemProcessado.item);
            if (novoItem == null) {
                throw new Exception("Erro ao gravar item de movimentação");
            }
        }

        return novaMovimentacao;
    }

    @Override
    protected void atualizarEstoque(Object dadosProcessados, Object resultadoOperacao, Connection conn) throws Exception {
        DadosEntradaProcessados dados = (DadosEntradaProcessados) dadosProcessados;

        for (DadosEntradaProcessados.ItemEntradaProcessado itemProcessado : dados.itens) {
            BigDecimal valorAdd = new BigDecimal(Double.toString(itemProcessado.item.getQuantidade()));
            EstoqueModel estoque = itemProcessado.estoqueAtual;
            estoque.setQuantidade(estoque.getQuantidade().add(valorAdd));

            boolean estoqueUpdated = estoqueModel.getEstDAO().alterar(estoque);
            if (!estoqueUpdated) {
                throw new Exception("Erro ao atualizar estoque para produto ID: " + itemProcessado.item.getProdutoId());
            }
        }
    }

    @Override
    protected ResultadoOperacao criarResultadoSucesso(Object resultadoOperacao) {
        MovimentacaoEstoqueModel movimentacao = (MovimentacaoEstoqueModel) resultadoOperacao;

        ResultadoOperacao resultado = new ResultadoOperacao();
        resultado.setOperacao("entradaProduto");
        resultado.setSucesso(true);
        resultado.setMensagem("Entrada de produtos registrada com sucesso. ID: " + movimentacao.getIdmovimentacao());

        return resultado;
    }
}