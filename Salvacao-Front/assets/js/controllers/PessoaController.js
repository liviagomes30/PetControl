import PessoaService from "../services/PessoaService.js";
import PessoaModel from "../models/PessoaModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class PessoaController{
    constructor(){
        this.service = new PessoaService();
    }
    
    async inicializarListagem() {
        try {
            UIComponents.Loading.mostrar("Carregando adotantes...");
            const pessoas = await this.service.listarTodos();
            this.renderizarTabela(pessoas);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
            UIComponents.Toast.sucesso(message);
            }
        } catch (error) {
            console.error("Erro ao carregar adotantes:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async renderizarTabela(pessoas) {
        const tabela = document.getElementById("tabela-pessoas");
        tabela.innerHTML = ""; // Limpa a tabela antes de renderizar

        if (!pessoas || pessoas.length === 0) {
            tabela.innerHTML = `<tr><td colspan="6">Nenhum adotante encontrado.</td></tr>`;
            return;
        }

        pessoas.forEach(pessoa => {
                const linha = document.createElement("tr");

                linha.innerHTML = `
                <td>${pessoa.nome}</td>
                <td>${pessoa.cpf}</td>
                <td>${pessoa.email}</td>
                <td>${pessoa.telefone}</td>
                <td>${pessoa.endereco}</td>
                <td>
                    <button onclick="editarPessoa(${pessoa.idPessoa})">Editar</button>
                    <button onclick="excluirPessoa(${pessoa.idPessoa})">Excluir</button>
                </td>
                `;

                tabela.appendChild(linha);
        });
    }

  confirmarExclusao(cpf) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      // Callback de confirmação
      () => {
        this.excluir(cpf).catch((error) => {
          console.error("Erro ao excluir:", error);
          UIComponents.Toast.erro(
            MensagensPadroes.ERRO.EXCLUSAO +
              (error.message ? `: ${error.message}` : "")
          );
        });
      }
    );
  }

}

const pessoaController = new PessoaController();
window.PessoaController = pessoaController;
export{pessoaController};
export default PessoaController;