package salvacao.petcontrol.service;

import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.util.Validation;

import java.util.List;

@Service
public class PessoaService {

    private PessoaModel pessoaModel;

    public PessoaService() {
        this.pessoaModel = new PessoaModel();
    }

    public PessoaModel addPessoa(PessoaModel pessoa) throws Exception {
        if (!Validation.isCpfValido(pessoa.getCpf()))
            throw new Exception("CPF inválido!");

        if (pessoaModel.getPessoaDAO().findByPessoa(pessoa.getCpf()) != null)
            throw new Exception("Usuário já cadastrado!");

        System.out.println("Adicionando pessoa: " + pessoa);
        PessoaModel pessoaFinal = pessoaModel.getPessoaDAO().addPessoa(pessoa);
        return pessoaFinal;
    }

    public PessoaModel getPessoaCpf(String cpf) {
        PessoaModel pessoa = pessoaModel.getPessoaDAO().findByPessoa(cpf);
        return pessoa;
    }

    public boolean updatePessoa(String cpf, PessoaModel pessoa) throws Exception {
        if(!Validation.isCpfValido(pessoa.getCpf()))
            throw new Exception("CPF inválido!");

        if(pessoaModel.getPessoaDAO().findByPessoa(pessoa.getCpf()) == null)
            throw new Exception("Não existe esse usuário!");

        pessoaModel.getPessoaDAO().updatePessoa(cpf, pessoa);
        return true;
    }

    public void deletePessoa(String cpf) throws Exception {
        if(!Validation.isCpfValido(cpf))
            throw new Exception("CPF inválido!");

        PessoaModel pessoaDelete = pessoaModel.getPessoaDAO().findByPessoa(cpf);

        if(pessoaDelete == null)
            throw new Exception("Não existe esse usuário!");

        if(!pessoaModel.getPessoaDAO().deleteByPessoa(cpf))
            throw new Exception("Erro ao deletar uma pessoa!");
    }

    public List<PessoaModel> getAllPessoa() {
        return pessoaModel.getPessoaDAO().getAll();
    }
}