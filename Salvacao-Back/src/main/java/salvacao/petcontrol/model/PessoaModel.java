package salvacao.petcontrol.model;

import salvacao.petcontrol.dao.PessoaDAO;

public class PessoaModel {
    private Integer idpessoa;
    private String nome;
    private String cpf;
    private String endereco;
    private String telefone;
    private String email;
    private PessoaDAO pessoaDAO;

    public PessoaModel() {
        pessoaDAO = new PessoaDAO();
    }

    public PessoaModel(Integer idpessoa, String nome, String cpf, String endereco, String telefone, String email) {
        this.idpessoa = idpessoa;
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    public Integer getIdpessoa() {
        return idpessoa;
    }

    public void setIdpessoa(Integer idpessoa) {
        this.idpessoa = idpessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}