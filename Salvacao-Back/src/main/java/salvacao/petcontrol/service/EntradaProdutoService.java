package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dto.EntradaProdutoDTO;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import salvacao.petcontrol.config.SingletonDB;

@Service
public class EntradaProdutoService {
    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel();

    @Autowired
    private ProdutoModel produtoModel = new ProdutoModel();

    @Autowired
    private  MotivoMovimentacaoModel motivoMovimentacaoModel = new MotivoMovimentacaoModel();

    @Autowired
    private MovimentacaoEstoqueModel movimentacaoEstoqueModel = new MovimentacaoEstoqueModel();

    @Autowired
    private ItemMovimentacaoModel itemMovimentacaoModel = new ItemMovimentacaoModel();

    @Autowired
    private  UsuarioModel usuarioModel = new UsuarioModel();

    @Autowired
    private PessoaModel pessoaModel = new PessoaModel();

    public List<EntradaProdutoDTO> getPoduto(){
        List<ProdutoCompletoDTO> produtoDTO = produtoModel.getProdDAO().getAllProdutos();
        List<EntradaProdutoDTO> entradalist = new ArrayList<>();

        for(ProdutoCompletoDTO prod : produtoDTO)
            entradalist.add(new EntradaProdutoDTO(prod.getProduto(),estoqueModel.getEstDAO().getByProdutoId(prod.getProduto().getIdproduto())));

        return  entradalist;
    }

    public List<EntradaProdutoDTO> getPodutoNome(String nome){
        List<ProdutoCompletoDTO> produtoDTO = produtoModel.getProdDAO().getByName(nome);
        List<EntradaProdutoDTO> entradalist = new ArrayList<>();

        for(ProdutoCompletoDTO prod : produtoDTO)
            entradalist.add(new EntradaProdutoDTO(prod.getProduto(),estoqueModel.getEstDAO().getByProdutoId(prod.getProduto().getIdproduto())));

        return  entradalist;
    }

    public boolean addRegistro(EntradaProdutoModel registro) throws Exception {
        Connection conn = SingletonDB.getConexao().getConnection();
        boolean sucesso = false;

        try {
            // Iniciar transação
            conn.setAutoCommit(false);

            EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(registro.getIdprod());
            MotivoMovimentacaoModel motivo = motivoMovimentacaoModel.getMotivoMovimentacaoDAO().getTipo("ENTRADA");

            // Pegar ID_usuario do token de login
            UsuarioModel usuario = usuarioModel.getUsuDAO().getId(1 /*registro.getIdusu()*/);

            MovimentacaoEstoqueModel movimentacao = new MovimentacaoEstoqueModel(
                    motivo.getTipo(),
                    registro.getDate(),
                    usuario.getPessoa_idpessoa(),
                    registro.getObservacao(),
                    registro.getFornecedor());

            MovimentacaoEstoqueModel novaMovimentacao = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().gravar(movimentacao);

            if (novaMovimentacao != null)
            {
                ItemMovimentacaoModel item = new ItemMovimentacaoModel(
                        novaMovimentacao.getIdmovimentacao(),
                        registro.getIdprod(),
                        registro.getQuantidade(),
                        motivo.getIdmotivo());

                ItemMovimentacaoModel novoItem = itemMovimentacaoModel.getItemMovimentacaoDAO().gravar(item);

                if (novoItem != null) {
                    BigDecimal valorAdd = new BigDecimal(Double.toString(registro.getQuantidade()));
                    estoque.setQuantidade(estoque.getQuantidade().add(valorAdd));

                    if (estoqueModel.getEstDAO().alterar(estoque)) {
                        conn.commit(); // Se tudo deu certo, faz commit
                        sucesso = true;
                    }
                    else
                    {
                        conn.rollback(); // rollback nas tabelas item e movimentacao
                        throw new Exception("Erro ao atualizar estoque. Rollback executado.");
                    }
                }
                else
                {
                    conn.rollback(); // rollback só na movimentacao
                    throw new Exception("Erro ao gravar item de movimentação. Rollback executado.");
                }
            } else {
                throw new Exception("Erro ao gravar movimentação.");
            }

        } catch (Exception e) {
            try {
                conn.rollback(); // Segurança extra
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e; // relança exceção pra cima
        } finally {
            try {
                conn.setAutoCommit(true); // volta o autocommit pro padrão
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return sucesso;
    }

    public List<RegistroModel> getRegistros(){
        List<MovimentacaoEstoqueModel> movimentacoes = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().getTipo("ENTRADA");
        ItemMovimentacaoModel item = null;
        ProdutoModel produto = null;
        PessoaModel pessoa=null;
        RegistroModel registro = null;
        List<RegistroModel> registros = new ArrayList<>();


        for(MovimentacaoEstoqueModel movi : movimentacoes){
            item = itemMovimentacaoModel.getItemMovimentacaoDAO().getIdMovimentacao(movi.getIdmovimentacao());
            pessoa = pessoaModel.getPessoaDAO().getId(movi.getUsuarioPessoaId());
            produto = produtoModel.getProdDAO().getId(item.getProdutoId());

            registro = new RegistroModel(pessoa.getNome(),
                    produto.getNome(),
                    movi.getData(),
                    item.getQuantidade(),
                    movi.getFornecedor());
            registros.add(registro);
        }


        return  registros;
    }

}
