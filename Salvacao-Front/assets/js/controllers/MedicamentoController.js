import MedicamentoService from "../services/MedicamentoService.js";
import MedicamentoModel from "../models/MedicamentoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class MedicamentoController {
  constructor() {
    this.service = new MedicamentoService();
    this.tipoProdutoValue = null;
    this.unidadeMedidaValue = null;
  }

  vincularLimpezaAutomaticaValidacao() {
    const campos = [
      { id: "nome", evento: "input" },
      { id: "composicao", evento: "input" },
      { id: "tipoProduto", evento: "change" },
      { id: "unidadeMedida", evento: "change" },
      { id: "preco", evento: "input" },
      { id: "estoqueMinimo", evento: "input" },
      { id: "fabricante", evento: "input" }, // Adicionado fabricante se também tiver validação de obrigatório
    ];

    campos.forEach((config) => {
      const elemento = document.getElementById(config.id);
      if (elemento) {
        elemento.addEventListener(config.evento, function () {
          if (
            (this.tagName === "SELECT" && this.value) ||
            (this.tagName !== "SELECT" && this.value.trim())
          ) {
            UIComponents.Validacao.limparErroCampo(config.id);
          }
        });
      }
    });
  }

  async inicializarListagem() {
    try {
      UIComponents.Loading.mostrar("Carregando medicamentos...");
      const medicamentos = await this.service.listarTodos();
      this.renderizarTabela(medicamentos);

      // NOVO: Adiciona event listeners para os elementos de filtro
      const searchInput = document.getElementById("searchInput");
      const filterSelect = document.getElementById("filterSelect");
      const searchButton = document.getElementById("searchButton");
      const clearSearchButton = document.getElementById("clearSearchButton");

      const applyFilter = () => {
        const termo = searchInput.value.trim();
        const filtro = filterSelect.value;
        this.filtrarMedicamentos(termo, filtro); // Chama o método de filtragem existente
      };

      if (searchInput) {
        searchInput.addEventListener("input", applyFilter); // Filtra ao digitar
        searchInput.addEventListener("keyup", (e) => {
          if (e.key === "Enter") {
            applyFilter(); // Filtra ao pressionar Enter
          }
        });
      }
      if (filterSelect) {
        filterSelect.addEventListener("change", applyFilter); // Filtra ao mudar o tipo
      }
      if (searchButton) {
        searchButton.addEventListener("click", applyFilter); // Filtra ao clicar no botão de busca
      }
      if (clearSearchButton) {
        clearSearchButton.addEventListener("click", () => {
          searchInput.value = "";
          filterSelect.value = "1"; // Reseta para o filtro "Nome"
          applyFilter(); // Aplica o filtro para mostrar todos os dados novamente
        });
      }
      // FIM NOVO BLOCO

      // Verificar mensagem na URL
      const urlParams = new URLSearchParams(window.location.search);
      const message = urlParams.get("message");
      if (message) {
        UIComponents.Toast.sucesso(message);
        // Limpa a URL para evitar que a mensagem apareça novamente ao recarregar
        const newUrl = new URL(window.location.href);
        newUrl.searchParams.delete("message");
        window.history.replaceState({}, document.title, newUrl.toString());
      }
    } catch (error) {
      console.error("Erro ao carregar medicamentos:", error);
      UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderizarTabela(medicamentos) {
    const tabela = document.getElementById("tabela-medicamentos");
    if (!tabela) return;

    tabela.innerHTML = "";

    if (medicamentos.length === 0) {
      tabela.innerHTML = `
      <tr>
        <td colspan="9" class="text-center">Nenhum medicamento encontrado.</td>
      </tr>
    `;
      return;
    }

    medicamentos.forEach((med) => {
      if (!med.produto) {
        console.warn("Produto não encontrado no medicamento:", med);
        med.produto = {};
      }

      if (!med.medicamento) {
        console.warn("Dados do medicamento não encontrados:", med);
        med.medicamento = {};
      }

      const nome = med.produto.nome || "-";

      const composicao = med.medicamento.composicao || "-";

      const tipoProduto = med.tipoProduto?.descricao || "-";

      const unidadeMedida = med.unidadeMedida?.descricao || "-";

      const fabricante = med.produto.fabricante || "-";

      let precoFormatado = "-";
      if (med.produto.preco !== null && med.produto.preco !== undefined) {
        try {
          const preco = parseFloat(med.produto.preco);
          if (!isNaN(preco)) {
            precoFormatado = `R$ ${preco.toFixed(2).replace(".", ",")}`;
          }
        } catch (error) {
          console.warn("Erro ao formatar preço:", error);
        }
      }

      let dataCadastro = "-";
      if (med.produto.dataCadastro) {
        try {
          const data = new Date(med.produto.dataCadastro);
          if (!isNaN(data.getTime())) {
            dataCadastro = data.toLocaleDateString("pt-BR");
          }
        } catch (error) {
          console.warn("Erro ao formatar data:", error);
        }
      }

      let estoqueMinimo = "-";
      if (
        med.produto.estoqueMinimo !== null &&
        med.produto.estoqueMinimo !== undefined
      ) {
        try {
          const estoque = parseInt(med.produto.estoqueMinimo);
          if (!isNaN(estoque)) {
            estoqueMinimo = estoque.toString();
          }
        } catch (error) {
          console.warn("Erro ao formatar estoque mínimo:", error);
        }
      }

      const idProduto = med.produto.idproduto || 0;

      const tr = document.createElement("tr");
      tr.innerHTML = `
      <td>${nome}</td>
      <td>${composicao}</td>
      <td>${tipoProduto}</td>
      <td>${unidadeMedida}</td>
      <td>${fabricante}</td>
      <td>${precoFormatado}</td>
      <td>${estoqueMinimo}</td>
      <td>${dataCadastro}</td>
      <td>
        <a href="editarMedicamento.html?id=${idProduto}" class="btn btn-sm btn-primary">Editar</a>
        <button onclick="medicamentoController.confirmarExclusao(${idProduto})" class="btn btn-sm btn-outline-secondary">Excluir</button>
      </td>
    `;
      tabela.appendChild(tr);
    });
  }

  confirmarExclusao(id) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar exclusão",
      MensagensPadroes.CONFIRMACAO.EXCLUSAO,
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
      UIComponents.Loading.mostrar("Excluindo medicamento...");

      try {
        const resposta = await this.service.excluir(id);

        if (resposta.sucesso) {
          if (resposta.operacao === "excluido") {
            UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
          } else if (resposta.operacao === "desativado") {
            UIComponents.Toast.sucesso(
              "Medicamento desativado com sucesso. Este item está sendo utilizado no sistema e não pode ser excluído completamente."
            );
          }

          await this.inicializarListagem();
          return resposta;
        } else {
          UIComponents.Toast.erro(
            resposta.mensagem || MensagensPadroes.ERRO.EXCLUSAO
          );
          return resposta;
        }
      } catch (error) {
        if (
          error.message &&
          error.message.includes("Cannot commit when autoCommit is enabled")
        ) {
          UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.EXCLUSAO);
          setTimeout(() => this.inicializarListagem(), 500);
          return { sucesso: true };
        } else {
          throw error;
        }
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

  async inicializarFormulario() {
    try {
      UIComponents.Validacao.limparErros("formMedicamento"); //

      const urlParams = new URLSearchParams(window.location.search); //
      const id = urlParams.get("id"); //

      const form = document.getElementById("formMedicamento");
      if (form) {
        if (id) {
          form.addEventListener("submit", (e) => this.atualizar(e, id)); //
        } else {
          form.addEventListener("submit", (e) => this.cadastrar(e)); //
          const alertaEstoque = document.getElementById("alertaEstoque"); //
          if (alertaEstoque) {
            //
            alertaEstoque.style.display = "block"; //
          }
        }
      }

      this.configurarMascarasCampos(); //
      this.vincularLimpezaAutomaticaValidacao(); // Adiciona a chamada aqui

      UIComponents.Loading.mostrar("Carregando dados..."); //
      await this.carregarSelectsTipoUnidade(); //

      if (id) {
        await this.carregarMedicamento(id); //
      }

      UIComponents.Loading.esconder(); //
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error); //
      UIComponents.ModalErro.mostrar(
        //
        "Não foi possível carregar o formulário: " + (error.message || "")
      );
      UIComponents.Loading.esconder(); //
    }
  }

  configurarMascarasCampos() {
    const campoPreco = document.getElementById("preco");
    if (campoPreco) {
      campoPreco.setAttribute("data-mask", "money");
      campoPreco.setAttribute("type", "text");
      campoPreco.setAttribute("placeholder", "0,00");
    }

    const campoEstoque = document.getElementById("estoqueMinimo");
    if (campoEstoque) {
      campoEstoque.setAttribute("data-mask", "number");
      campoEstoque.setAttribute("placeholder", "0");
    }

    const campoNome = document.getElementById("nome");
    if (campoNome) {
      campoNome.setAttribute("data-max-length", "100");
      campoNome.setAttribute("data-show-counter", "true");
    }

    const campoComposicao = document.getElementById("composicao");
    if (campoComposicao) {
      campoComposicao.setAttribute("data-max-length", "200");
      campoComposicao.setAttribute("data-show-counter", "true");
    }

    const campoFabricante = document.getElementById("fabricante");
    if (campoFabricante) {
      campoFabricante.setAttribute("data-max-length", "100");
    }

    UIComponents.InputMasks.inicializar();
  }

  limparMensagensErro() {
    UIComponents.Validacao.limparErros("formMedicamento");
  }

  async carregarSelectsTipoUnidade() {
    try {
      const tipos = await this.service.listarTiposProduto();
      const unidades = await this.service.listarUnidadesMedida();

      const selectTipo = document.getElementById("tipoProduto");
      const selectUnidade = document.getElementById("unidadeMedida");

      if (selectTipo) {
        while (selectTipo.options.length > 1) {
          selectTipo.remove(1);
        }

        tipos.forEach((tipo) => {
          const option = document.createElement("option");
          option.value = tipo.idtipoproduto;
          option.textContent = tipo.descricao;
          selectTipo.appendChild(option);
        });
      }

      if (selectUnidade) {
        while (selectUnidade.options.length > 1) {
          selectUnidade.remove(1);
        }

        unidades.forEach((unidade) => {
          const id =
            unidade.idunidademedida !== undefined
              ? unidade.idunidademedida
              : unidade.idUnidadeMedida;

          const option = document.createElement("option");
          option.value = id;
          option.textContent = unidade.descricao;
          selectUnidade.appendChild(option);
        });
      }

      return { tipos, unidades };
    } catch (error) {
      console.error("Erro ao carregar selects:", error);
      throw new Error(
        "Falha ao carregar tipos de produtos e unidades de medida: " +
          error.message
      );
    }
  }

  async carregarMedicamento(id) {
    try {
      UIComponents.Loading.mostrar("Carregando medicamento...");

      const data = await this.service.buscarPorId(id);
      console.log("Dados recebidos do backend:", data);

      if (!data || !data.produto || !data.medicamento) {
        throw new Error("Dados do medicamento não encontrados ou incompletos");
      }

      const idField = document.getElementById("idproduto");
      const nomeField = document.getElementById("nome");
      const composicaoField = document.getElementById("composicao");
      const fabricanteField = document.getElementById("fabricante");
      const tipoSelect = document.getElementById("tipoProduto");
      const unidadeSelect = document.getElementById("unidadeMedida");
      const precoField = document.getElementById("preco");
      const estoqueField = document.getElementById("estoqueMinimo");

      if (idField) idField.value = data.produto.idproduto;
      if (nomeField) nomeField.value = data.produto.nome || "";
      if (composicaoField)
        composicaoField.value = data.medicamento.composicao || "";
      if (fabricanteField)
        fabricanteField.value = data.produto.fabricante || "";

      if (precoField) {
        if (data.produto.preco !== null && data.produto.preco !== undefined) {
          precoField.value = UIComponents.InputMasks.formatarValorMonetario(
            data.produto.preco
          );
          precoField.dataset.valor = data.produto.preco;
        } else {
          precoField.value = "";
          precoField.dataset.valor = "";
        }
      }

      if (estoqueField) {
        if (
          data.produto.estoqueMinimo !== null &&
          data.produto.estoqueMinimo !== undefined
        ) {
          estoqueField.value = data.produto.estoqueMinimo;
        } else {
          estoqueField.value = "";
        }
      }

      if (tipoSelect && data.produto.idtipoproduto) {
        tipoSelect.value = data.produto.idtipoproduto;
      }

      const idUnidadeMedida =
        data.produto.idunidademedida !== undefined
          ? data.produto.idunidademedida
          : data.produto.idUnidadeMedida;

      if (unidadeSelect && idUnidadeMedida) {
        unidadeSelect.value = idUnidadeMedida;
      }

      UIComponents.Loading.esconder();
      UIComponents.Toast.sucesso("Dados carregados com sucesso");
    } catch (error) {
      console.error("Erro ao carregar medicamento:", error);
      UIComponents.Loading.esconder();
      UIComponents.ModalErro.mostrar(
        "Falha ao carregar os dados: " + error.message
      );
      throw new Error(
        "Não foi possível carregar os dados do medicamento: " + error.message
      );
    }
  }

  validarFormulario() {
    this.limparMensagensErro();

    const medicamento = this.obterDadosFormulario();
    const { valido, erros } = medicamento.validar();

    if (!valido) {
      for (const campo in erros) {
        const elementId = {
          nome: "nome",
          composicao: "composicao",
          tipoProduto: "tipoProduto",
          unidadeMedida: "unidadeMedida",
          preco: "preco",
          estoqueMinimo: "estoqueMinimo",
        }[campo];

        if (elementId) {
          UIComponents.Validacao.mostrarErro(elementId, erros[campo]);
        }
      }
    }

    return { valido, medicamento };
  }

  obterDadosFormulario() {
    const idprodutoElement = document.getElementById("idproduto");
    const nomeElement = document.getElementById("nome");
    const tipoProdutoElement = document.getElementById("tipoProduto");
    const unidadeMedidaElement = document.getElementById("unidadeMedida");
    const fabricanteElement = document.getElementById("fabricante");
    const composicaoElement = document.getElementById("composicao");
    const precoElement = document.getElementById("preco");
    const estoqueElement = document.getElementById("estoqueMinimo");

    if (
      !nomeElement ||
      !tipoProdutoElement ||
      !unidadeMedidaElement ||
      !composicaoElement
    ) {
      throw new Error(MensagensPadroes.ALERTA.CAMPOS_OBRIGATORIOS);
    }

    const idproduto = idprodutoElement ? idprodutoElement.value : null;

    let preco = UIComponents.InputMasks.obterValorNumerico(precoElement);
    let estoqueMinimo =
      UIComponents.InputMasks.obterValorNumerico(estoqueElement);

    return new MedicamentoModel({
      produto: {
        idproduto: idproduto ? parseInt(idproduto) : null,
        nome: nomeElement.value.trim(),
        idtipoproduto: parseInt(tipoProdutoElement.value) || null,
        idunidademedida: unidadeMedidaElement.value
          ? parseInt(unidadeMedidaElement.value)
          : null,
        fabricante: fabricanteElement ? fabricanteElement.value.trim() : "",
        preco: preco,
        estoqueMinimo: estoqueMinimo,
      },
      medicamento: {
        idproduto: idproduto ? parseInt(idproduto) : null,
        composicao: composicaoElement.value.trim(),
      },
    });
  }

  async cadastrar(event) {
    event.preventDefault();

    try {
      const { valido, medicamento } = this.validarFormulario();
      if (!valido) {
        return;
      }

      UIComponents.Loading.mostrar("Salvando medicamento...");
      console.log(
        "Enviando para o backend:",
        JSON.stringify(medicamento.toJSON())
      );

      const resultado = await this.service.cadastrar(medicamento.toJSON());
      console.log("Resposta do backend:", resultado);

      UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);

      setTimeout(() => {
        window.location.href =
          "listarMedicamentos.html?message=" +
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

  async atualizar(event, id) {
    event.preventDefault();

    try {
      const { valido, medicamento } = this.validarFormulario();
      if (!valido) {
        return;
      }

      UIComponents.Loading.mostrar("Atualizando medicamento...");
      console.log(
        "Enviando para o backend (atualização):",
        JSON.stringify(medicamento.toJSON())
      );

      const resultado = await this.service.atualizar(id, medicamento.toJSON());
      console.log("Resposta do backend (atualização):", resultado);

      UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);

      setTimeout(() => {
        window.location.href =
          "listarMedicamentos.html?message=" +
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

  async filtrarMedicamentos(termo, filtro) {
    console.log("Termo: " + termo);
    try {
      UIComponents.Loading.mostrar("Filtrando medicamentos...");

      const todosMedicamentos = await this.service.listarTodos();
      let medicamentosFiltrados = todosMedicamentos;

      if (termo !== "") {
        const termoBusca = termo.toLowerCase();

        switch (parseInt(filtro)) {
          case 1: // Nome
            medicamentosFiltrados = todosMedicamentos.filter((med) =>
              med.produto?.nome?.toLowerCase().includes(termoBusca)
            );
            break;
          case 2: // Composição
            medicamentosFiltrados = todosMedicamentos.filter((med) =>
              med.medicamento?.composicao?.toLowerCase().includes(termoBusca)
            );
            break;
          case 3: // Tipo
            medicamentosFiltrados = todosMedicamentos.filter((med) =>
              med.tipoProduto?.descricao?.toLowerCase().includes(termoBusca)
            );
            break;
          case 4: // Fabricante
            medicamentosFiltrados = todosMedicamentos.filter((med) =>
              med.produto?.fabricante?.toLowerCase().includes(termoBusca)
            );
            break;
          default:
            medicamentosFiltrados = todosMedicamentos;
        }
      }

      this.renderizarTabela(medicamentosFiltrados);

      const urlParams = new URLSearchParams(window.location.search);
      const message = urlParams.get("message");
      if (message) {
        UIComponents.Toast.sucesso(message);
      }
    } catch (error) {
      console.error("Erro ao filtrar medicamentos:", error);
      UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

const medicamentoController = new MedicamentoController();
window.medicamentoController = medicamentoController;

export { medicamentoController };
export default MedicamentoController;
