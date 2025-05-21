package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dal.AcertoEstoqueDAL;
import salvacao.petcontrol.dal.EstoqueDAL;
import salvacao.petcontrol.dal.ProdutoDAL;
import salvacao.petcontrol.dal.UsuarioDAL;
import salvacao.petcontrol.dal.TipoProdutoDAL;
import salvacao.petcontrol.dal.UnidadeMedidaDAL;
import salvacao.petcontrol.dto.AcertoEstoqueCompletoDTO;
import salvacao.petcontrol.dto.AcertoEstoqueRequestDTO;
import salvacao.petcontrol.dto.ItemAcertoEstoqueDTO;
import salvacao.petcontrol.model.AcertoEstoqueModel;
import salvacao.petcontrol.model.ItemAcertoEstoqueModel;
import salvacao.petcontrol.model.EstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.UsuarioModel;
import salvacao.petcontrol.model.TipoProdutoModel;
import salvacao.petcontrol.model.UnidadeMedidaModel;
import salvacao.petcontrol.util.ResultadoOperacao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class AcertoEstoqueService {

    @Autowired
    private AcertoEstoqueDAL acertoEstoqueDAL;

    @Autowired
    private EstoqueDAL estoqueDAL;

    @Autowired
    private ProdutoDAL produtoDAL;

    @Autowired
    private UsuarioDAL usuarioDAL;

    @Autowired
    private TipoProdutoDAL tipoProdutoDAL;

    @Autowired
    private UnidadeMedidaDAL unidadeMedidaDAL;


    public AcertoEstoqueCompletoDTO getAcertoById(Integer id) throws Exception {
        AcertoEstoqueModel acerto = acertoEstoqueDAL.findById(id);
        if (acerto == null) {
            throw new Exception("Acerto de estoque não encontrado");
        }

        UsuarioModel usuario = usuarioDAL.findById(acerto.getUsuario_pessoa_id());

        List<ItemAcertoEstoqueModel> itensModel = acertoEstoqueDAL.findItensAcerto(id);
        List<ItemAcertoEstoqueDTO> itensDTO = new ArrayList<>();

        for (ItemAcertoEstoqueModel item : itensModel) {
            ProdutoModel produto = produtoDAL.findById(item.getProduto_id());
            TipoProdutoModel tipoProduto = tipoProdutoDAL.findById(produto.getIdtipoproduto());
            UnidadeMedidaModel unidadeMedida = unidadeMedidaDAL.findById(produto.getIdunidademedida());

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


    public List<AcertoEstoqueModel> getAllAcertos() {
        return acertoEstoqueDAL.findAll();
    }


    public List<AcertoEstoqueModel> getAcertosByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return acertoEstoqueDAL.findByPeriodo(dataInicio, dataFim);
    }


    public List<AcertoEstoqueModel> getAcertosByUsuario(Integer usuarioId) {
        return acertoEstoqueDAL.findByUsuario(usuarioId);
    }


    public ResultadoOperacao efetuarAcertoEstoque(AcertoEstoqueRequestDTO request) throws Exception {
        // Validações
        if (request.getUsuario_pessoa_id() == null) {
            throw new Exception("Usuário é obrigatório");
        }

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new Exception("Motivo é obrigatório");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new Exception("É necessário ao menos um item para acerto de estoque");
        }

        UsuarioModel usuario = usuarioDAL.findById(request.getUsuario_pessoa_id());
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }

        AcertoEstoqueModel acerto = new AcertoEstoqueModel();
        acerto.setData(LocalDate.now());
        acerto.setUsuario_pessoa_id(request.getUsuario_pessoa_id());
        acerto.setMotivo(request.getMotivo());
        acerto.setObservacao(request.getObservacao());

        List<ItemAcertoEstoqueModel> itens = new ArrayList<>();

        for (AcertoEstoqueRequestDTO.ItemAcertoRequestDTO itemRequest : request.getItens()) {
            EstoqueModel estoque = estoqueDAL.findByProdutoId(itemRequest.getProduto_id());
            if (estoque == null) {
                throw new Exception("Produto ID " + itemRequest.getProduto_id() + " não encontrado no estoque");
            }

            if (itemRequest.getQuantidade_nova() == null || itemRequest.getQuantidade_nova().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Quantidade deve ser maior ou igual a zero para o produto ID " + itemRequest.getProduto_id());
            }

            ItemAcertoEstoqueModel item = new ItemAcertoEstoqueModel();
            item.setProduto_id(itemRequest.getProduto_id());

            item.setQuantidade_depois(itemRequest.getQuantidade_nova());

            itens.add(item);
        }

        try {
            AcertoEstoqueModel acertoRealizado = acertoEstoqueDAL.efetuarAcertoEstoque(acerto, itens);

            // Retornar resultado positivo
            ResultadoOperacao resultado = new ResultadoOperacao();
            resultado.setOperacao("acertoEstoque");
            resultado.setSucesso(true);
            resultado.setMensagem("Acerto de estoque realizado com sucesso. ID: " + acertoRealizado.getIdacerto());

            return resultado;
        } catch (SQLException e) {
            throw new Exception("Erro ao efetuar acerto de estoque: " + e.getMessage());
        }
    }
}