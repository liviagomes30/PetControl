// salvacao.petcontrol.service.AcertoEstoqueService.java
package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.dao.AcertoEstoqueDAO; // Changed from AcertoEstoqueDAO
import salvacao.petcontrol.dao.EstoqueDAO; // Changed from Estoquedao
import salvacao.petcontrol.dao.ProdutoDAO; // Changed from Produtodao
import salvacao.petcontrol.dao.UsuarioDAO; // Changed from Usuariodao
import salvacao.petcontrol.dao.TipoProdutoDAO; // Changed from TipoProdutodao
import salvacao.petcontrol.dao.UnidadeMedidaDAO; // Changed from UnidadeMedidaDAL
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
    private AcertoEstoqueDAO acertoEstoqueDAO; // Changed from AcertoEstoqueDAL

    @Autowired
    private EstoqueDAO estoqueDAO; // Changed from EstoqueDAL

    @Autowired
    private ProdutoDAO produtoDAO; // Changed from ProdutoDAL

    @Autowired
    private UsuarioDAO usuarioDAO; // Changed from UsuarioDAL

    @Autowired
    private TipoProdutoDAO tipoProdutoDAO; // Changed from TipoProdutoDAL

    @Autowired
    private UnidadeMedidaDAO unidadeMedidaDAO; // Changed from UnidadeMedidaDAL


    public AcertoEstoqueCompletoDTO getId(Integer id) throws Exception { // Renamed from getAcertoById
        AcertoEstoqueModel acerto = acertoEstoqueDAO.getId(id); // Calls DAO.getId()
        if (acerto == null) {
            throw new Exception("Acerto de estoque não encontrado");
        }

        UsuarioModel usuario = usuarioDAO.getId(acerto.getUsuario_pessoa_id()); // Calls usuarioDAO.getId()

        List<ItemAcertoEstoqueModel> itensModel = acertoEstoqueDAO.getItensAcerto(id); // Calls DAO.getItensAcerto()
        List<ItemAcertoEstoqueDTO> itensDTO = new ArrayList<>();

        for (ItemAcertoEstoqueModel item : itensModel) {
            ProdutoModel produto = produtoDAO.getId(item.getProduto_id()); // Calls produtoDAO.getId()
            TipoProdutoModel tipoProduto = tipoProdutoDAO.getId(produto.getIdtipoproduto()); // Calls tipoProdutoDAO.getId()
            UnidadeMedidaModel unidadeMedida = unidadeMedidaDAO.getId(produto.getIdunidademedida()); // Calls unidadeMedidaDAO.getId()

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


    public List<AcertoEstoqueModel> getAll() { // Renamed from getAllAcertos
        return acertoEstoqueDAO.getAll(); // Calls DAO.getAll()
    }


    public List<AcertoEstoqueModel> getByPeriodo(LocalDate dataInicio, LocalDate dataFim) { // Renamed from getAcertosByPeriodo
        return acertoEstoqueDAO.getByPeriodo(dataInicio, dataFim); // Calls DAO.getByPeriodo()
    }


    public List<AcertoEstoqueModel> getByUsuario(Integer usuarioId) { // Renamed from getAcertosByUsuario
        return acertoEstoqueDAO.getByUsuario(usuarioId); // Calls DAO.getByUsuario()
    }


    public ResultadoOperacao gravar(AcertoEstoqueRequestDTO request) throws Exception { // Renamed from efetuarAcertoEstoque
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

        UsuarioModel usuario = usuarioDAO.getId(request.getUsuario_pessoa_id()); // Calls usuarioDAO.getId()
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
            EstoqueModel estoque = estoqueDAO.getByProdutoId(itemRequest.getProduto_id()); // Calls estoqueDAO.getByProdutoId()
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
            AcertoEstoqueModel acertoRealizado = acertoEstoqueDAO.efetuarAcertoEstoque(acerto, itens); // Calls DAO.efetuarAcertoEstoque()

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