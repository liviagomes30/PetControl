package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.UsuarioModel;
import salvacao.petcontrol.model.PessoaModel;

public class UsuarioCompletoDTO {
    private UsuarioModel usuario;
    private PessoaModel pessoa;

    public UsuarioCompletoDTO() {
    }

    public UsuarioCompletoDTO(UsuarioModel usuario, PessoaModel pessoa) {
        this.usuario = usuario;
        this.pessoa = pessoa;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public PessoaModel getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaModel pessoa) {
        this.pessoa = pessoa;
    }
}