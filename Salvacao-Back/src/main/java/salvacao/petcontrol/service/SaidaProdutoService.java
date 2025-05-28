package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import salvacao.petcontrol.config.SingletonDB;


@Service
public class SaidaProdutoService {
    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel();

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


    public boolean addRegistro(EntradaProdutoModel registro) throws Exception {
        Connection conn = SingletonDB.getConexao().getConnection();
        boolean sucesso = true;

        try {
            // Iniciar transação
            conn.setAutoCommit(false);

            MotivoMovimentacaoModel motivo = motivoMovimentacaoModel.getMotivoMovimentacaoDAO().getTipo("SAIDA");

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
                        if (valorAdd.compareTo(estoque.getQuantidade()) <= 0) {
                            estoque.setQuantidade(estoque.getQuantidade().subtract(valorAdd));
                            if (!estoqueModel.getEstDAO().alterar(estoque)) {
                                sucesso = false;
                                throw new Exception("Erro ao atualizar estoque. Rollback executado.");
                            }
                        } else {
                            sucesso = false;
                            throw new Exception("Erro ao atualizar estoque.\nQuantidade a ser retirada menor que quantidade existente\nRollback executado.");
                        }
                    }
                    else
                    {
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
        List<MovimentacaoEstoqueModel> movimentacoes = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().getTipo("SAIDA");
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
        List<MovimentacaoEstoqueModel> movimentacoes = movimentacaoEstoqueModel.getMovimentacaoEstoqueDAO().buscarPorPeriodo(ini,fim, "SAIDA");
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
                    estoque.setQuantidade(estoque.getQuantidade().add(valorAdd));

                    if (!estoqueModel.getEstDAO().alterar(estoque)) {
                        sucesso = false;
                        System.out.println("AQUI 2");
                        erro = "Erro ao atualizar estoque. Rollback executado.";
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
