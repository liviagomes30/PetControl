package salvacao.petcontrol.dto;

import salvacao.petcontrol.model.UsuarioModel;

public class UsuarioDTO {
    private UsuarioModel usuario;

    public UsuarioDTO() {
    }

    public UsuarioDTO(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }
}