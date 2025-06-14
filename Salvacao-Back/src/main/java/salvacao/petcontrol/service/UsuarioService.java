package salvacao.petcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.UsuarioModel;
import salvacao.petcontrol.util.ResultadoOperacao;
import salvacao.petcontrol.dao.PessoaDAO;
import salvacao.petcontrol.config.SingletonDB;
import java.sql.Connection;
import salvacao.petcontrol.dto.UsuarioDTO;
import salvacao.petcontrol.dto.UsuarioCompletoDTO;
import salvacao.petcontrol.model.PessoaModel;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioModel usuarioModel = new UsuarioModel();
    
    @Autowired
    private PessoaDAO pessoaDAO = new PessoaDAO();

    public UsuarioCompletoDTO getId(Integer id) {
        return usuarioModel.getUsuDAO().findUsuarioCompleto(id);
    }

    public UsuarioDTO gravar(UsuarioDTO dto) throws Exception {
        if (dto.getUsuario() == null) {
            throw new Exception("Dados do usuario incompletos");
        }

        if (dto.getUsuario().getPessoa().getNome() == null || dto.getUsuario().getPessoa().getNome().trim().isEmpty()) {
            throw new Exception("Nome do usuario é obrigatório");
        }

        try {
            return new UsuarioDTO(usuarioModel.getUsuDAO().gravar(dto.getUsuario()));
        } catch (RuntimeException e) {
            throw new Exception("Erro ao adicionar usuario: " + e.getMessage(), e);
        }
    }

    public ResultadoOperacao apagarUsuario(Integer id) throws Exception {
        UsuarioCompletoDTO existente = usuarioModel.getUsuDAO().findUsuarioCompleto(id);
        if (existente == null) {
            throw new Exception("Usuario não encontrado");
        }

        ResultadoOperacao resultado = new ResultadoOperacao();

        try {
            boolean podeExcluir = usuarioModel.getUsuDAO().usuarioPodeSerExcluido(id);

            if (podeExcluir) {
                boolean sucesso = usuarioModel.getUsuDAO().apagar(id);
                resultado.setOperacao("excluido");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem("Usuario excluído com sucesso");
                } else {
                    resultado.setMensagem("Falha ao excluir o usuario");
                }
            } else {
                boolean sucesso = usuarioModel.getUsuDAO().desativarUsuario(id);
                resultado.setOperacao("desativado");
                resultado.setSucesso(sucesso);

                if (sucesso) {
                    resultado.setMensagem(
                            "Usuario desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente.");
                } else {
                    resultado.setMensagem("Falha ao desativar o usuario");
                }
            }

            return resultado;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Erro ao processar a exclusão: " + e.getMessage());
        }
    }

    public boolean reativarUsuario(Integer id) throws Exception {
        UsuarioCompletoDTO existente = usuarioModel.getUsuDAO().findUsuarioCompleto(id);
        if (existente == null) {
            throw new Exception("Usuario não encontrado");
        }

        return usuarioModel.getUsuDAO().reativarUsuario(id);
    }

    public List<UsuarioCompletoDTO> listarUsuariosFiltrados(String filtro) throws Exception {
        try {
            return usuarioModel.getUsuDAO().listarUsuariosFiltrados(filtro);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Erro ao buscar usuários com filtro: " + e.getMessage(), e);
        }
    }

    public List<UsuarioCompletoDTO> getByFabricante(String filtro) {
        return usuarioModel.getUsuDAO().getByFabricante(filtro);
    }

    public List<UsuarioCompletoDTO> getByTipoDescricao(String filtro) {
        return usuarioModel.getUsuDAO().getByTipoDescricao(filtro);
    }

    public List<UsuarioCompletoDTO> getAll() {
        return usuarioModel.getUsuDAO().getByNome("");
    }

    public List<UsuarioCompletoDTO> getByName(String filtro) {
        return usuarioModel.getUsuDAO().getByNome(filtro);
    }

    public List<UsuarioCompletoDTO> getUsuariosByTipo(Integer idTipo) {
        return usuarioModel.getUsuDAO().getByTipoDescricao("");
    }

    public boolean alterar(Integer id, UsuarioCompletoDTO dto) throws Exception {
        if (dto.getUsuario() == null) {
            throw new Exception("Dados do usuario incompletos");
        }
        if (dto.getUsuario().getLogin() == null || dto.getUsuario().getLogin().trim().isEmpty()) {
            throw new Exception("Login do usuario é obrigatório");
        }

        UsuarioCompletoDTO existente = usuarioModel.getUsuDAO().findUsuarioCompleto(id);
        if (existente == null) {
            throw new Exception("Usuario não encontrado");
        }

        dto.getUsuario().setPessoa_idpessoa(id);

        return usuarioModel.getUsuDAO().alterar(dto.getUsuario());
    }

    public UsuarioModel gravar(UsuarioCompletoDTO dto) throws Exception {
        if (dto.getUsuario() == null) {
            throw new Exception("Dados do usuario incompletos");
        }

        if (dto.getUsuario().getLogin() == null || dto.getUsuario().getLogin().trim().isEmpty()) {
            throw new Exception("Login do usuario é obrigatório");
        }

        if (dto.getPessoa() == null) {
            throw new Exception("Dados da pessoa são obrigatórios");
        }

        if (usuarioModel.getUsuDAO().loginExists(dto.getUsuario().getLogin())) {
            throw new Exception("Login já existe. Escolha outro login.");
        }

        Connection conn = null;
        try {
            conn = SingletonDB.getConexao().getConnection();
            conn.setAutoCommit(false);

            Integer pessoaId;

            if (dto.getUsuario().getPessoa_idpessoa() == null) {
                PessoaModel novaPessoa = pessoaDAO.gravar(dto.getPessoa(), conn);
                pessoaId = novaPessoa.getIdpessoa();
            } else {
                pessoaId = dto.getUsuario().getPessoa_idpessoa();
            }

            dto.getUsuario().setPessoa_idpessoa(pessoaId);

            UsuarioModel novoUsuario = usuarioModel.getUsuDAO().gravar(dto.getUsuario(), conn);
            
            conn.commit();
            return novoUsuario;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new Exception("Erro ao adicionar usuario: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public UsuarioCompletoDTO authenticate(String login, String senha) throws Exception {
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("Login é obrigatório");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new Exception("Senha é obrigatória");
        }

        boolean isValid = usuarioModel.getUsuDAO().validateCredentials(login, senha);
        if (isValid) {
            return usuarioModel.getUsuDAO().findByLogin(login);
        }
        return null;
    }

    public UsuarioCompletoDTO findByLogin(String login) throws Exception {
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("Login é obrigatório");
        }
        return usuarioModel.getUsuDAO().findByLogin(login);
    }

    public boolean updatePassword(Integer id, String novaSenha) throws Exception {
        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            throw new Exception("Nova senha é obrigatória");
        }

        UsuarioCompletoDTO existente = usuarioModel.getUsuDAO().findUsuarioCompleto(id);
        if (existente == null) {
            throw new Exception("Usuario não encontrado");
        }

        return usuarioModel.getUsuDAO().updatePassword(id, novaSenha);
    }

    public boolean loginExists(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }
        return usuarioModel.getUsuDAO().loginExists(login);
    }
}