package salvacao.petcontrol.service;

import jakarta.mail.*;
import jakarta.mail.Message;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import salvacao.petcontrol.model.AnimalModel;
import salvacao.petcontrol.model.PessoaModel;
import salvacao.petcontrol.model.PessoaPreferenciaModel;
import salvacao.petcontrol.model.PreferenciaPorteModel;
import salvacao.petcontrol.util.Validation;

import java.util.List;
import java.util.Properties;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;



@Service
public class PessoaService implements Observer {

    private PessoaModel pessoaModel;

    private AnimalService sujeito;

    @Autowired
    private PreferenciaPorteModel preferenciaPorteModel = new PreferenciaPorteModel();

    public PessoaService() {
        this.pessoaModel = new PessoaModel();
    }

    public PessoaModel addPessoa(PessoaPreferenciaModel pessoaPreferencia) throws Exception {
        PessoaModel pessoa = new PessoaModel(pessoaPreferencia.getIdpessoa(),
                pessoaPreferencia.getNome(),
                pessoaPreferencia.getCpf(),
                pessoaPreferencia.getEndereco(),
                pessoaPreferencia.getTelefone(),
                pessoaPreferencia.getEmail());

        if (!Validation.isCpfValido(pessoa.getCpf()))
            throw new Exception("CPF inválido!");

        if (pessoaModel.getPessoaDAO().findByPessoa(pessoa.getCpf()) != null)
            throw new Exception("Usuário já cadastrado!");

        System.out.println("Adicionando pessoa: " + pessoa);
        PessoaModel pessoaFinal = pessoaModel.getPessoaDAO().addPessoa(pessoa);

        if(pessoaPreferencia.getValores().length > 0) {
            sujeito = new AnimalService();
            PreferenciaPorteModel p = new PreferenciaPorteModel();
            p.setIdpessoa(pessoaFinal.getIdpessoa());

            for (int i = 0; i < pessoaPreferencia.getValores().length; i++) {
                p.setIdporte(pessoaPreferencia.getValores()[i]);
                sujeito.registrarObserver(p);
            }
        }

        return pessoaFinal;
    }

    public PessoaModel getPessoaCpf(String cpf) {
        PessoaModel pessoa = pessoaModel.getPessoaDAO().findByPessoa(cpf);
        return pessoa;
    }

    public PessoaPreferenciaModel getPessoaCpfPreferencia(String cpf) {
        PessoaModel pessoa = pessoaModel.getPessoaDAO().findByPessoa(cpf);
        PessoaPreferenciaModel pessoapre = new PessoaPreferenciaModel(pessoa.getIdpessoa(),
                pessoa.getNome(),
                pessoa.getCpf(),
                pessoa.getEndereco(),
                pessoa.getTelefone(),
                pessoa.getEmail());

        int[] vet = new int[3];
        List<PreferenciaPorteModel> preferencias = preferenciaPorteModel.getPreferenciaPorteDAO().buscarPessoa(pessoa.getIdpessoa());
        if (!preferencias.isEmpty()) {
            for (PreferenciaPorteModel p: preferencias)
                vet[p.getIdporte() - 1] = p.getIdporte();

        }

        pessoapre.setValores(vet);

        return pessoapre;
    }

    public boolean updatePessoa(String cpf, PessoaPreferenciaModel pessoaPreferencia) throws Exception {
        PessoaModel pessoa = new PessoaModel(pessoaPreferencia.getIdpessoa(),
                pessoaPreferencia.getNome(),
                pessoaPreferencia.getCpf(),
                pessoaPreferencia.getEndereco(),
                pessoaPreferencia.getTelefone(),
                pessoaPreferencia.getEmail());

        if(!Validation.isCpfValido(pessoa.getCpf()))
            throw new Exception("CPF inválido!");

        if(pessoaModel.getPessoaDAO().findByPessoa(pessoa.getCpf()) == null)
            throw new Exception("Não existe esse usuário!");


        pessoaModel.getPessoaDAO().updatePessoa(cpf, pessoa);

        int[] antigas = new int[3];
        List<PreferenciaPorteModel> preferencias = preferenciaPorteModel.getPreferenciaPorteDAO().buscarPessoa(pessoa.getIdpessoa());
        if (!preferencias.isEmpty()) {
            for (PreferenciaPorteModel p: preferencias)
                antigas[p.getIdporte() - 1] = p.getIdporte();

        }

        PreferenciaPorteModel p = new PreferenciaPorteModel();
        sujeito = new AnimalService();
        for (int i = 0; i < antigas.length; i++) {

            if (!(antigas[i] == pessoaPreferencia.getValores()[i])) {
                p.setIdpessoa(pessoaPreferencia.getIdpessoa());
                if (antigas[i] == 0) {
                    p.setIdporte(pessoaPreferencia.getValores()[i]);
                    sujeito.registrarObserver(p);
                }
                else {
                    p.setIdporte(antigas[i]);
                    sujeito.removerObserver(p);
                }
            }
        }
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

    @Override
    public void update(PreferenciaPorteModel preferenciaPorte, AnimalModel animal) {

        String host = "smtp.gmail.com";
        String remetente = "notificacaopetcontrol@gmail.com";
        String senha = "fols kbbz ztjs siea";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });

        PessoaModel p = pessoaModel.getPessoaDAO().getId(preferenciaPorte.getIdpessoa());

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(p.getEmail())
            );

            message.setSubject("Novo Animal Cadastrado: " + animal.getNome());

            String corpoEmail = "Olá " + p.getNome() + ",\n\n"
                    + "Um novo animal foi cadastrado no sistema:\n\n"
                    + "🐾 Nome: " + animal.getNome() + "\n"
                    + "📌 Espécie: " + animal.getEspecie() + "\n"
                    + "⚧ Sexo: " + animal.getSexo() + "\n"
                    + "📋 Status: " + animal.getStatus() + "\n\n"
                    + "Atenciosamente,\nEquipe de Cadastro de Animais";

            message.setText(corpoEmail);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}