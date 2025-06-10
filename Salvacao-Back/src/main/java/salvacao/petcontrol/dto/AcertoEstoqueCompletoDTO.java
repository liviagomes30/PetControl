package salvacao.petcontrol.dto;

import java.util.List;
import salvacao.petcontrol.model.AcertoEstoqueModel;
import salvacao.petcontrol.model.ItemAcertoEstoqueModel;
import salvacao.petcontrol.model.ProdutoModel;
import salvacao.petcontrol.model.UsuarioModel;

public class AcertoEstoqueCompletoDTO {
    private AcertoEstoqueModel acertoEstoque;
    private UsuarioModel usuario;
    private List<ItemAcertoEstoqueDTO> itens;

    // Construtores
    public AcertoEstoqueCompletoDTO() {
    }

    public AcertoEstoqueCompletoDTO(AcertoEstoqueModel acertoEstoque, UsuarioModel usuario,
                                    List<ItemAcertoEstoqueDTO> itens) {
        this.acertoEstoque = acertoEstoque;
        this.usuario = usuario;
        this.itens = itens;
    }

    // Getters e Setters
    public AcertoEstoqueModel getAcertoEstoque() {
        return acertoEstoque;
    }

    public void setAcertoEstoque(AcertoEstoqueModel acertoEstoque) {
        this.acertoEstoque = acertoEstoque;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public List<ItemAcertoEstoqueDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemAcertoEstoqueDTO> itens) {
        this.itens = itens;
    }
}