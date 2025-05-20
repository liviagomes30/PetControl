package salvacao.petcontrol.model;

public class UsuarioModel {
    private String login;
    private String senha;
    private Integer pessoa_idpessoa;
    private PessoaModel pessoa;

    public UsuarioModel() {
    }

    public UsuarioModel(String login, String senha, Integer pessoa_idpessoa) {
        this.login = login;
        this.senha = senha;
        this.pessoa_idpessoa = pessoa_idpessoa;
    }

    public UsuarioModel(String login, String senha, Integer pessoa_idpessoa, PessoaModel pessoa) {
        this.login = login;
        this.senha = senha;
        this.pessoa_idpessoa = pessoa_idpessoa;
        this.pessoa = pessoa;
    }

    // Getters e Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getPessoa_idpessoa() {
        return pessoa_idpessoa;
    }

    public void setPessoa_idpessoa(Integer pessoa_idpessoa) {
        this.pessoa_idpessoa = pessoa_idpessoa;
    }

    public PessoaModel getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaModel pessoa) {
        this.pessoa = pessoa;
    }
}