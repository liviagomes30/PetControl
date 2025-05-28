package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dto.EntradaProdutoDTO;
import salvacao.petcontrol.dto.ItensEntradaDTO;
import salvacao.petcontrol.dto.ProdutoCompletoDTO;
import salvacao.petcontrol.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
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
            entradalist.add(new EntradaProdutoDTO(prod,
                    estoqueModel.getEstDAO().getByProdutoId(prod.getProduto().getIdproduto())));

        return  entradalist;
    }

    public EstoqueModel getEstoque(Integer id){
        EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(id);
        return estoque;
    }

    public boolean addRegistro(EntradaProdutoModel registro) throws Exception {
        Connection conn = SingletonDB.getConexao().getConnection();
        boolean sucesso = true;

        try {
            // Iniciar transação
            conn.setAutoCommit(false);

            MotivoMovimentacaoModel motivo = motivoMovimentacaoModel.getMotivoMovimentacaoDAO().getTipo("ENTRADA");

            // Pegar ID_usuario do token de login
            UsuarioModel usuario = usuarioModel.getUsuDAO().getId(registro.getUsuarioId());

            MovimentacaoEstoqueModel movimentacao = new MovimentacaoEstoqueModel(
                    motivo.getTipo(),
                    registro.getData(),
                    usuario.getPessoa_idpessoa(),
                    registro.getObservacao(),
                    "----");


            MovimentacaoEstoqueModel novaMovimentacao = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().gravar(movimentacao);



            if (novaMovimentacao != null)
            {
                System.out.println("AQUI 1");
                int i = 0;
                while (i < registro.getItens().size() && sucesso) {
                    ItemMovimentacaoModel item = new ItemMovimentacaoModel(
                            novaMovimentacao.getIdmovimentacao(),
                            registro.getItens().get(i).getProdutoId(),
                            registro.getItens().get(i).getQuantidade(),
                            motivo.getIdmotivo());

                    ItemMovimentacaoModel novoItem = itemMovimentacaoModel.getItemMovimentacaoDAO().gravar(item);
                    EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(registro.getItens().get(i).getProdutoId());
                    if (novoItem != null) {
                        BigDecimal valorAdd = new BigDecimal(Double.toString(registro.getItens().get(i).getQuantidade()));
                        estoque.setQuantidade(estoque.getQuantidade().add(valorAdd));

                        if (!estoqueModel.getEstDAO().alterar(estoque)) {
                            sucesso = false;
                            System.out.println("AQUI 2");
                            throw new Exception("Erro ao atualizar estoque. Rollback executado.");
                        }
                    }
                    else
                    {
                        System.out.println("AQUI 3");
                        conn.rollback(); // rollback só na movimentacao
                        throw new Exception("Erro ao gravar item de movimentação. Rollback executado.");
                    }
                    i++;
                }
                if(sucesso)
                    conn.commit();
                else
                    conn.rollback();
            } else {
                System.out.println("AQUI 4");
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
        PessoaModel pessoa=null;
        RegistroModel registro = null;
        List<RegistroModel> registros = new ArrayList<>();


        for(MovimentacaoEstoqueModel movi : movimentacoes){
            pessoa = pessoaModel.getPessoaDAO().getId(movi.getUsuarioPessoaId());

            registro = new RegistroModel(movi.getIdmovimentacao(),
                    pessoa.getNome(),
                    movi.getData(),
                    movi.getObs());
            registros.add(registro);
        }

        return  registros;
    }

    public List<RegistroModel> getRegistrosPeriodo(LocalDate ini, LocalDate fim){
        List<MovimentacaoEstoqueModel> movimentacoes = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().buscarPorPeriodo(ini,fim);
        PessoaModel pessoa=null;
        RegistroModel registro = null;
        List<RegistroModel> registros = new ArrayList<>();


        for(MovimentacaoEstoqueModel movi : movimentacoes){
            pessoa = pessoaModel.getPessoaDAO().getId(movi.getUsuarioPessoaId());

            registro = new RegistroModel(movi.getIdmovimentacao(),
                    pessoa.getNome(),
                    movi.getData(),
                    movi.getObs());
            registros.add(registro);
        }

        return  registros;
    }

    public ItensEntradaDTO getItem(Integer id){
        MovimentacaoEstoqueModel movimentacao = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().getId(id);
        List<ItemMovimentacaoModel> itens = itemMovimentacaoModel.getItemMovimentacaoDAO().getIdMovimentacao(id);
        List<ItensModel> itensModel = new ArrayList<>();
        ItensModel item = null;
        ProdutoCompletoDTO produto = null;
        PessoaModel pessoa = null;
        ItensEntradaDTO itensEntrada = null;

        for(ItemMovimentacaoModel  it:itens){
            produto = produtoModel.getProdDAO().findProdutoCompleto(it.getProdutoId());

            item = new ItensModel(produto.getProduto().getNome(),
                    produto.getTipoProduto().getDescricao(),
                    produto.getUnidadeMedida().getSigla(),
                    it.getQuantidade());

            itensModel.add(item);
        }

        pessoa = pessoaModel.getPessoaDAO().getId(movimentacao.getUsuarioPessoaId());
        itensEntrada = new ItensEntradaDTO(id,
                pessoa.getNome(),
                movimentacao.getData(),
                movimentacao.getObs(),
                itensModel);

        return  itensEntrada;
    }

    public boolean apagar(Integer id) throws Exception {
        Connection conn = SingletonDB.getConexao().getConnection();
        boolean sucesso = true;
        String erro = "";

        try {
            // Iniciar transação
            conn.setAutoCommit(false);
            EstoqueModel estoque = null;
            List<ItemMovimentacaoModel> itens = new ArrayList<>();
            itens = itemMovimentacaoModel.getItemMovimentacaoDAO().getIdMovimentacao(id);

            if(!itens.isEmpty()) {
                int i = 0;
                while (i < itens.size() && sucesso) {
                    estoque = estoqueModel.getEstDAO().getByProdutoId(itens.get(i).getProdutoId());
                    BigDecimal valorAdd = new BigDecimal(Double.toString(itens.get(i).getQuantidade()));

                    if (valorAdd.compareTo(estoque.getQuantidade()) <= 0) {
                        estoque.setQuantidade(estoque.getQuantidade().subtract(valorAdd));
                        if (!estoqueModel.getEstDAO().alterar(estoque)) {
                            sucesso = false;
                            erro = "Erro ao atualizar estoque. Rollback executado.";
                        }
                    } else {
                        sucesso = false;
                        erro = "Erro ao atualizar estoque.\nQuantidade a ser retirada menor que quantidade existente\nRollback executado.";
                    }
                    i++;
                }
            }
            if (sucesso)
            {
                if(itemMovimentacaoModel.getItemMovimentacaoDAO().apagar(id)){
                    if(!movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().apagar(id)) {
                        sucesso = false;
                        erro = "Erro ao apagar a movimentação";
                    }
                }
                else {
                    sucesso = false;
                    erro = "Erro ao apagar os itens";
                }
            }
            if(sucesso)
                conn.commit();
            else {
                throw  new Exception(erro);
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
}
