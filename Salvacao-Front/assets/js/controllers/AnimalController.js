import AnimalService from "../services/AnimalService.js";
import AnimalModel from "../models/AnimalModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class AnimalController {
    constructor() {
        this.service = new AnimalService();
    }

    async inicializarListagem() {
        try {
            UIComponents.Loading.mostrar("Carregando animais...");
            const animais = await this.service.listarTodos();
            this.renderizarTabela(animais);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(message);
            }
            } catch (error) {
            console.error("Erro ao carregar animais:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
            } finally {
            UIComponents.Loading.esconder();
        }
    }

    async filtrarAnimais(termo, filtro) {
        console.log("Termo: "+termo);
        try {
            UIComponents.Loading.mostrar("Carregando animais...");
            var animais;
            if(termo == '')
                animais = await this.service.listarTodos();
            else{
                if(filtro == 1){
                    animais = await this.service.listarPorNome(termo);
                }
                else
                {
                    if(filtro == 2){
                        animais = await this.service.listarPorEspecie(termo);
                    }
                    else
                    {
                        animais = await this.service.listarPorRaca(termo);
                    }
                }
            }   

            this.renderizarTabela(animais);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(message);
            }
            } catch (error) {
                console.error("Erro ao carregar animais:", error);
                UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
            } finally {
                UIComponents.Loading.esconder();
        }
    }

    formatarDataLocal(dataISO) {
    if (!dataISO) return "-";
    
    const partes = dataISO.split("-");
    if (partes.length !== 3) return "-";

    const [ano, mes, dia] = partes;
    return `${dia}/${mes}/${ano}`; // formato pt-BR
    }

    renderizarTabela(animais) {
        const tabela = document.getElementById("tabela-animais");
        if (!tabela) return;

        tabela.innerHTML = "";

        if (animais.length === 0) {
        tabela.innerHTML = `
        <tr>
            <td colspan="9" class="text-center">Nenhum animal encontrado.</td>
        </tr>
        `;
        return;
        }

        animais.forEach((ani) => {
        
        let datanasc = "-";
        if (ani.datanascimento) {
            datanasc = this.formatarDataLocal(ani.datanascimento);
        }

        let dataresg = "-";
        if (ani.dataresgate) {
            dataresg = this.formatarDataLocal(ani.dataresgate);
        }


        let castrado = "-";
        if(ani.castrado){
            castrado = "Sim";
        }
        else
            castrado = "Não";

        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${ani.nome || "-"}</td>
        <td>${ani.especie || "-"}</td>
        <td>${ani.raca || "-"}</td>
        <td>${ani.porte || "-"}</td>
        <td>${ani.sexo || "-"}</td>
        <td>${ani.cor || "-"}</td>
        <td>${castrado}</td>
        <td>${datanasc}</td>
        <td>${dataresg}</td>
        <td>${ani.status || "-"}</td>
        <td>${ani.foto || "-"}</td>
        
        <td>
            <a href="editarAnimal.html?id=${
            ani.id
            }" class="btn btn-sm btn-primary">Editar</a>
            <button onclick="animalController.confirmarExclusao(${
            ani.id
            })" class="btn btn-sm btn-outline-secondary">Excluir</button>
        </td>
        `;
        tabela.appendChild(tr);
        });
    }


    obterDadosFormulario() {
        const nomeElement = document.getElementById("nome");
        const especieElement = document.getElementById("especie");
        const datanascimentoElement = document.getElementById("datanascimento");
        const dataresgateElement = document.getElementById("dataresgate");
        const racaElement = document.getElementById("raca");
        const porteElement = document.getElementById("porte");
        const sexoElement = document.getElementById("sexo");
        const statusElement = document.getElementById("status");
        const fotoElement = document.getElementById("foto");
        const castradoElement = document.getElementById("castrado");
        const corElement = document.getElementById("cor");

        

        // Verificação de campos obrigatórios
        if (!nomeElement || !especieElement || !sexoElement || !statusElement) {
            throw new Error("Campos obrigatórios não preenchidos.");
        }


       var animal = new AnimalModel({
            nome: nomeElement.value.trim(),
            especie: especieElement.value,
            datanascimento: datanascimentoElement.value || null,
            raca: racaElement.value.trim(),
            porte: porteElement.value,
            sexo: sexoElement.value,
            status: statusElement.value,
            dataresgate: dataresgateElement.value || null,
            foto: fotoElement.value.trim(),
            castrado: castradoElement.value === "true",
            cor: corElement.value.trim(),
        });

        return animal;
    }

    async cadastrar(event) {
        event.preventDefault();
        try {
                const animal = this.obterDadosFormulario();
                

                const resultado = await this.service.cadastrar(animal);
                console.log("Resposta do backend:", resultado);

                UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);

                // Redirecionar após 2 segundos
                setTimeout(() => {
                    window.location.href =
                    "gerenciarAnimal.html?" +
                    encodeURIComponent(MensagensPadroes.SUCESSO.CADASTRO);
                    }, 2000);

            } catch (error) {
                console.error("Erro ao cadastrar:", error);
                UIComponents.ModalErro.mostrar(
                    error.message || MensagensPadroes.ERRO.CADASTRO
                );
            } finally {
            UIComponents.Loading.esconder();
        }
    }

    async inicializarFormulario(id){
        try {

        

        var animal = await this.service.getId(id);

        document.getElementById("nome").value = animal.nome || "";
        document.getElementById("especie").value = animal.especie || "";
        document.getElementById("datanascimento").value = animal.datanascimento || "";
        document.getElementById("dataresgate").value = animal.dataresgate || "";
        document.getElementById("raca").value = animal.raca || "";
        document.getElementById("porte").value = animal.porte || "";
        document.getElementById("sexo").value = animal.sexo || "";
        document.getElementById("cor").value = animal.cor || "";
        document.getElementById("status").value = animal.status || "";
        document.getElementById("castrado").value = animal.castrado ? "true" : "false";
        document.getElementById("foto").value = animal.foto || "";


        UIComponents.Loading.mostrar("Carregando dados...");
        UIComponents.Loading.esconder();

        } catch (error) {
        console.error("Erro ao inicializar formulário:", error);
        UIComponents.ModalErro.mostrar(
            "Não foi possível carregar o formulário: " + (error.message || "")
        );
        UIComponents.Loading.esconder();
        }
    }

    obterDadosAtualizacao(id) {

        const nomeElement = document.getElementById("nome");
        const especieElement = document.getElementById("especie");
        const datanascimentoElement = document.getElementById("datanascimento");
        const dataresgateElement = document.getElementById("dataresgate");
        const racaElement = document.getElementById("raca");
        const porteElement = document.getElementById("porte");
        const sexoElement = document.getElementById("sexo");
        const statusElement = document.getElementById("status");
        const fotoElement = document.getElementById("foto");
        const castradoElement = document.getElementById("castrado");
        const corElement = document.getElementById("cor");

        

        // Verificação de campos obrigatórios
        if (!nomeElement || !especieElement || !sexoElement || !statusElement) {
            throw new Error("Campos obrigatórios não preenchidos.");
        }


       var animal = new AnimalModel({
            id: id,
            nome: nomeElement.value.trim(),
            especie: especieElement.value,
            datanascimento: datanascimentoElement.value || null,
            raca: racaElement.value.trim(),
            porte: porteElement.value,
            sexo: sexoElement.value,
            status: statusElement.value,
            dataresgate: dataresgateElement.value || null,
            foto: fotoElement.value.trim(),
            castrado: castradoElement.value === "true",
            cor: corElement.value.trim(),
        });

        return animal;
    }

    async atualizar(idAnimal) {
            try {
        
                console.log("IDAnimal: "+idAnimal);
                const animal = this.obterDadosAtualizacao(idAnimal);
            
                UIComponents.Loading.mostrar("Atualizando animal...");
                console.log(
                    "Enviando para o backend (atualização):",
                    JSON.stringify(animal)
                );

                const resultado = await this.service.atualizar(animal);
                console.log("Resposta do backend (atualização):", resultado);

                UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);

                // Redirecionar após 2 segundos
                setTimeout(() => {
                    window.location.href =
                    "gerenciarAnimal.html?message=" +
                    encodeURIComponent(MensagensPadroes.SUCESSO.ATUALIZACAO);
                }, 2000);
                } catch (error) {
                console.error("Erro ao atualizar:", error);
                UIComponents.ModalErro.mostrar(
                    error.message || MensagensPadroes.ERRO.ATUALIZACAO
                );
                } finally {
                UIComponents.Loading.esconder();
            }
  }

  confirmarExclusao(id) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
      // Callback de confirmação
      () => {
        this.excluir(id).catch((error) => {
          console.error("Erro ao excluir:", error);
          UIComponents.Toast.erro(
            MensagensPadroes.ERRO.EXCLUSAO +
              (error.message ? `: ${error.message}` : "")
          );
        });
      }
    );
  }

  async excluir(id) {
    try {
      UIComponents.Loading.mostrar("Excluindo animal...");
      const resposta = await this.service.excluir(id);

      console.log("Excuiu? "+resposta.ok);

      if (resposta.ok) {
          UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
      } 
      else 
      {
        UIComponents.Toast.erro(resposta.mensagem || MensagensPadroes.ERRO.EXCLUSAO);
        return resposta;
      }

    } catch (error) {
      console.error("Erro ao processar:", error);
      UIComponents.Toast.erro(
        MensagensPadroes.ERRO.EXCLUSAO +
          (error.message ? `: ${error.message}` : "")
      );
      throw error;
    } finally {
      UIComponents.Loading.esconder();
    }
  }


}

// Tornar o controlador disponível globalmente para os botões da tabela
const animalController = new AnimalController();
window.animalController = animalController;

export { animalController };
export default AnimalController;