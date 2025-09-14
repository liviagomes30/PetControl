package salvacao.petcontrol.service.template;

import salvacao.petcontrol.model.*;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class SaidaProdutoOperacao extends OperacaoEstoqueTemplate {

    private final MovimentacaoEstoqueModel movimentacaoEstoqueModel = new MovimentacaoEstoqueModel();
    private final ItemMovimentacaoModel itemMovimentacaoModel = new ItemMovimentacaoModel();
    private final MotivoMovimentacaoModel motivoMovimentacaoModel = new MotivoMovimentacaoModel();
    private final EstoqueModel estoqueModel = new EstoqueModel();
    private final UsuarioModel usuarioModel = new UsuarioModel();

    public static class DadosSaidaProcessados {
        public MovimentacaoEstoqueModel movimentacao;
        public List<ItemSaidaProcessado> itens;

        public static class ItemSaidaProcessado {
            public ItemMovimentacaoModel item;
            public EstoqueModel estoqueAtual;
        }
    }

    @Override
    protected void validarDados(Object dados) throws Exception {
        super.validarDados(dados);
        if (!(dados instanceof EntradaProdutoModel)) { // Usando o modelo existente
            throw new Exception("Dados devem ser do tipo EntradaProdutoModel");
        }
        EntradaProdutoModel registro = (EntradaProdutoModel) dados;
        if (registro.getItens() == null || registro.getItens().isEmpty()) {
            throw new Exception("É necessário adicionar ao menos um item para a saída.");
        }
        if (registro.getUsuarioId() == null) {
            throw new Exception("Usuário é obrigatório.");
        }
        if (registro.getData() == null) {
            throw new Exception("Data de uso precisa ser informada.");
        }
        if (registro.getData().isAfter(LocalDate.now())) {
            throw new Exception("A data não pode ser no futuro.");
        }
    }

    @Override
    protected void validarRegrasNegocio(Object dados, Connection conn) throws Exception {
        EntradaProdutoModel registro = (EntradaProdutoModel) dados;

        if (usuarioModel.getUsuDAO().getId(registro.getUsuarioId()) == null) {
            throw new Exception("Usuário não encontrado.");
        }

        for (ItensEntrada item : registro.getItens()) {
            if (item.getQuantidade() <= 0) {
                throw new Exception("A quantidade para o produto " + item.getNome() + " deve ser maior que zero.");
            }
            boolean temEstoque = estoqueModel.getEstDAO().verificarEstoqueSuficiente(item.getProdutoId(), (int) item.getQuantidade());
            if (!temEstoque) {
                throw new Exception("Estoque insuficiente para o produto: " + item.getNome());
            }
        }
    }

    @Override
    protected Object prepararDadosOperacao(Object dados, Connection conn) throws Exception {
        EntradaProdutoModel registro = (EntradaProdutoModel) dados;

        MotivoMovimentacaoModel motivo = motivoMovimentacaoModel.getMotivoMovimentacaoDAO().getTipo("SAIDA");
        if (motivo == null) {
            throw new Exception("Motivo de movimentação 'SAIDA' não encontrado.");
        }
        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(registro.getUsuarioId());

        MovimentacaoEstoqueModel movimentacao = new MovimentacaoEstoqueModel(
                motivo.getTipo(),
                registro.getData(),
                usuario.getPessoa_idpessoa(),
                registro.getObservacao(),
                "----"
        );

        List<DadosSaidaProcessados.ItemSaidaProcessado> itensProcessados = new ArrayList<>();
        for (ItensEntrada itemSaida : registro.getItens()) {
            EstoqueModel estoqueAtual = estoqueModel.getEstDAO().getByProdutoId(itemSaida.getProdutoId());
            ItemMovimentacaoModel item = new ItemMovimentacaoModel(
                    null,
                    itemSaida.getProdutoId(),
                    itemSaida.getQuantidade(),
                    motivo.getIdmotivo()
            );

            DadosSaidaProcessados.ItemSaidaProcessado itemProcessado = new DadosSaidaProcessados.ItemSaidaProcessado();
            itemProcessado.item = item;
            itemProcessado.estoqueAtual = estoqueAtual;
            itensProcessados.add(itemProcessado);
        }

        DadosSaidaProcessados dadosProcessados = new DadosSaidaProcessados();
        dadosProcessados.movimentacao = movimentacao;
        dadosProcessados.itens = itensProcessados;
        return dadosProcessados;
    }

    @Override
    protected Object executarOperacaoPrincipal(Object dadosProcessados, Connection conn) throws Exception {
        DadosSaidaProcessados dados = (DadosSaidaProcessados) dadosProcessados;
        MovimentacaoEstoqueModel novaMovimentacao = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().gravar(dados.movimentacao);

        for (DadosSaidaProcessados.ItemSaidaProcessado itemProcessado : dados.itens) {
            itemProcessado.item.setMovimentacaoId(novaMovimentacao.getIdmovimentacao());
            itemMovimentacaoModel.getItemMovimentacaoDAO().gravar(itemProcessado.item);
        }
        return novaMovimentacao;
    }

    @Override
    protected void atualizarEstoque(Object dadosProcessados, Object resultadoOperacao, Connection conn) throws Exception {
        DadosSaidaProcessados dados = (DadosSaidaProcessados) dadosProcessados;

        for (DadosSaidaProcessados.ItemSaidaProcessado itemProcessado : dados.itens) {
            BigDecimal valorSubtrair = new BigDecimal(Double.toString(itemProcessado.item.getQuantidade()));
            EstoqueModel estoque = itemProcessado.estoqueAtual;
            // A lógica principal: SUBTRAIR do estoque
            estoque.setQuantidade(estoque.getQuantidade().subtract(valorSubtrair));

            boolean estoqueUpdated = estoqueModel.getEstDAO().alterar(estoque);
            if (!estoqueUpdated) {
                throw new Exception("Erro ao atualizar o estoque para o produto ID: " + itemProcessado.item.getProdutoId());
            }
        }
    }

    @Override
    protected ResultadoOperacao criarResultadoSucesso(Object resultadoOperacao) {
        MovimentacaoEstoqueModel movimentacao = (MovimentacaoEstoqueModel) resultadoOperacao;
        return new ResultadoOperacao("saidaProduto", true, "Saída de produtos registrada com sucesso. ID: " + movimentacao.getIdmovimentacao());
    }
}