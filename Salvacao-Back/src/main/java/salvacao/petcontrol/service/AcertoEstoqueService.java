package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import salvacao.petcontrol.config.SingletonDB; // Import SingletonDB
import salvacao.petcontrol.dto.AcertoEstoqueCompletoDTO;
import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.dto.ItemAcertoEstoqueDTO;
import salvacao.petcontrol.model.*;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class AcertoEstoqueService {

    @Autowired
    private AcertoEstoqueModel acertoEstoqueModel = new AcertoEstoqueModel(); // Autowire the Model

    @Autowired
    private EstoqueModel estoqueModel = new EstoqueModel(); // Autowire the Model

    @Autowired
    private ProdutoModel produtoModel = new ProdutoModel(); // Autowire the Model

    @Autowired
    private UsuarioModel usuarioModel = new UsuarioModel(); // Autowire the Model

    @Autowired
    private TipoProdutoModel tipoProdutoModel = new TipoProdutoModel(); // Autowire the Model

    @Autowired
    private UnidadeMedidaModel unidadeMedidaModel = new UnidadeMedidaModel(); // Autowire the Model
    @Autowired
    private PessoaModel pessoaModel;


    public AcertoEstoqueCompletoDTO getId(Integer id) throws Exception {
        AcertoEstoqueModel acerto = acertoEstoqueModel.getAcDAO().getId(id);
        if (acerto == null) {
            throw new Exception("Acerto de estoque não encontrado");
        }

        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(acerto.getUsuario_pessoa_id(), pessoaModel);

        List<ItemAcertoEstoqueModel> itensModel = acertoEstoqueModel.getAcDAO().getItensAcerto(id);
        List<ItemAcertoEstoqueDTO> itensDTO = new ArrayList<>();

        for (ItemAcertoEstoqueModel item : itensModel) {
            ProdutoModel produto = produtoModel.getProdDAO().getId(item.getProduto_id());
            // Access DAOs via Model instances
            TipoProdutoModel tipoProduto = tipoProdutoModel.getTpDAO().getId(produto.getIdtipoproduto());
            UnidadeMedidaModel unidadeMedida = unidadeMedidaModel.getUnDAO().getId(produto.getIdunidademedida());

            ItemAcertoEstoqueDTO itemDTO = new ItemAcertoEstoqueDTO(
                    item,
                    produto,
                    tipoProduto.getDescricao(),
                    unidadeMedida.getSigla()
            );

            itensDTO.add(itemDTO);
        }

        return new AcertoEstoqueCompletoDTO(acerto, usuario, itensDTO);
    }


    public List<AcertoEstoqueModel> getAll() {
        return acertoEstoqueModel.getAcDAO().getAll();
    }


    public List<AcertoEstoqueModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return acertoEstoqueModel.getAcDAO().getByPeriodo(dataInicio, dataFim);
    }


    public List<AcertoEstoqueModel> getByUsuario(Integer usuarioId) {
        return acertoEstoqueModel.getAcDAO().getByUsuario(usuarioId);
    }


    public ResultadoOperacao gravar(AcertoEstoqueRequestDTO request) throws Exception {
        if (request.getUsuario_pessoa_id() == null) {
            throw new Exception("Usuário é obrigatório");
        }

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new Exception("Motivo é obrigatório");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new Exception("É necessário ao menos um item para acerto de estoque");
        }

        // Access DAO via Model instance
        UsuarioModel usuario = usuarioModel.getUsuDAO().getId(request.getUsuario_pessoa_id(), pessoaModel);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }

        AcertoEstoqueModel acertoEstoque = new AcertoEstoqueModel(); // Create new instance for recording
        acertoEstoque.setData(LocalDate.now());
        acertoEstoque.setUsuario_pessoa_id(request.getUsuario_pessoa_id());
        acertoEstoque.setMotivo(request.getMotivo());
        acertoEstoque.setObservacao(request.getObservacao());

        List<ItemAcertoEstoqueModel> itensParaGravar = new ArrayList<>();

        for (AcertoEstoqueRequestDTO.ItemAcertoRequestDTO itemRequest : request.getItens()) {
            // Access DAO via Model instance
            EstoqueModel estoque = estoqueModel.getEstDAO().getByProdutoId(itemRequest.getProduto_id());
            if (estoque == null) {
                throw new Exception("Produto ID " + itemRequest.getProduto_id() + " não encontrado no estoque");
            }

            if (itemRequest.getQuantidade_nova() == null || itemRequest.getQuantidade_nova().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Quantidade deve ser maior ou igual a zero para o produto ID " + itemRequest.getProduto_id());
            }

            ItemAcertoEstoqueModel item = new ItemAcertoEstoqueModel();
            item.setProduto_id(itemRequest.getProduto_id());
            item.setQuantidade_depois(itemRequest.getQuantidade_nova());
            item.setQuantidade_antes(estoque.getQuantidade()); // Store current quantity before adjustment

            if (item.getQuantidade_depois().compareTo(item.getQuantidade_antes()) > 0) {
                item.setTipoajuste("ENTRADA");
            } else {
                item.setTipoajuste("SAIDA");
            }

            itensParaGravar.add(item);
        }

        Connection conn = null;
        boolean autoCommitOriginal = true;
        try {
            conn = SingletonDB.getConexao().getConnection();
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // 1. Gravar o cabeçalho do acerto de estoque
            AcertoEstoqueModel acertoInserido = acertoEstoqueModel.getAcDAO().gravarAcerto(acertoEstoque, conn);

            // 2. Gravar os itens e atualizar o estoque
            for (ItemAcertoEstoqueModel item : itensParaGravar) {
                item.setAcerto_id(acertoInserido.getIdacerto());

                // Update stock using EstoqueDAO, ensuring it uses the same connection
                // Assuming EstoqueDAO.alterar accepts a Connection
                EstoqueModel estoqueAtualizado = new EstoqueModel(
                        null, // idEstoque - not needed for update by product_id if `alterar` updates by product ID
                        item.getProduto_id(),
                        item.getQuantidade_depois()
                );
                // Need to get the actual idestoque for the update.
                EstoqueModel currentEstoque = estoqueModel.getEstDAO().getByProdutoId(item.getProduto_id());
                if (currentEstoque == null) {
                    throw new SQLException("Estoque não encontrado para o produto " + item.getProduto_id() + " durante a atualização.");
                }
                estoqueAtualizado.setIdestoque(currentEstoque.getIdestoque());


                boolean estoqueUpdated = estoqueModel.getEstDAO().alterar(estoqueAtualizado); // Use alter method that takes EstoqueModel
                if (!estoqueUpdated) {
                    throw new SQLException("Falha ao atualizar estoque para o produto: " + item.getProduto_id());
                }

                // Gravar o item do acerto
                acertoEstoqueModel.getAcDAO().gravarItemAcerto(item, conn);
            }

            conn.commit(); // Commit transaction
            ResultadoOperacao resultado = new ResultadoOperacao();
            resultado.setOperacao("acertoEstoque");
            resultado.setSucesso(true);
            resultado.setMensagem("Acerto de estoque realizado com sucesso. ID: " + acertoInserido.getIdacerto());

            return resultado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Erro ao efetuar acerto de estoque: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommitOriginal); // Restore auto-commit state
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}