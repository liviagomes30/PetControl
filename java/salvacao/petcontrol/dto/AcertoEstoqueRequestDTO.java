package salvacao.petcontrol.dto;

import java.util.List;
import java.math.BigDecimal;

// Esta classe é usada para receber dados do cliente (frontend) quando se deseja realizar um acerto de estoque.
// apenas os dados essenciais para criar um acerto

public class AcertoEstoqueRequestDTO {
    private Integer usuario_pessoa_id;
    private String motivo;
    private String observacao;
    private List<ItemAcertoRequestDTO> itens;

    // Contém dados completos do item, incluindo quantidades antes/depois, tipo de ajuste, e informações do produto
    public static class ItemAcertoRequestDTO {
        private Integer produto_id;
        private BigDecimal quantidade_nova;

        public ItemAcertoRequestDTO() {
        }

        public ItemAcertoRequestDTO(Integer produto_id, BigDecimal quantidade_nova) {
            this.produto_id = produto_id;
            this.quantidade_nova = quantidade_nova;
        }

        public Integer getProduto_id() {
            return produto_id;
        }

        public void setProduto_id(Integer produto_id) {
            this.produto_id = produto_id;
        }

        public BigDecimal getQuantidade_nova() {
            return quantidade_nova;
        }

        public void setQuantidade_nova(BigDecimal quantidade_nova) {
            this.quantidade_nova = quantidade_nova;
        }
    }

    public AcertoEstoqueRequestDTO() {
    }

    public AcertoEstoqueRequestDTO(Integer usuario_pessoa_id, String motivo, String observacao, List<ItemAcertoRequestDTO> itens) {
        this.usuario_pessoa_id = usuario_pessoa_id;
        this.motivo = motivo;
        this.observacao = observacao;
        this.itens = itens;
    }

    public Integer getUsuario_pessoa_id() {
        return usuario_pessoa_id;
    }

    public void setUsuario_pessoa_id(Integer usuario_pessoa_id) {
        this.usuario_pessoa_id = usuario_pessoa_id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemAcertoRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemAcertoRequestDTO> itens) {
        this.itens = itens;
    }
}