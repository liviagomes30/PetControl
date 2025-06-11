import MedicamentoService from "../services/MedicamentoService.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";
import MedicamentoModel from "../models/MedicamentoModel.js";

class MedicamentoController {
  constructor() {
    this.service = new MedicamentoService();
    this.tipoProdutoValue = null;
    this.unidadeMedidaValue = null;
    this.medicamentosAtivos = [];
    this.medicamentosInativos = [];
    this.inicializarListagem();
  }

  inicializarListagem() {
    this.carregarMedicamentosAtivos();
    this.carregarMedicamentosInativos();
    this.vincularEventosFiltro();

    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get("message");
    if (message) {
      UIComponents.Toast.sucesso(message);
      const newUrl = new URL(window.location.href);
      newUrl.searchParams.delete("message");
      window.history.replaceState({}, document.title, newUrl.toString());
    }
  }

  async inicializarFormulario() {
    try {
      UIComponents.Validacao.limparErros("formMedicamento");

      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");

      const form = document.getElementById("formMedicamento");
      if (form) {
        if (id) {
          form.addEventListener("submit", (e) => this.atualizar(e, id));
        } else {
          form.addEventListener("submit", (e) => this.cadastrar(e));

          const alertaEstoque = document.getElementById("alertaEstoque");
          if (alertaEstoque) {
            alertaEstoque.style.display = "block";
          }
        }
      }

      this.configurarMascarasCampos();

      UIComponents.Loading.mostrar("Carregando dados...");
      await this.carregarSelectsTipoUnidade();

      if (id) {
        await this.carregarMedicamento(id);
      }

      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error);
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar o formulário: " + (error.message || "")
      );
      UIComponents.Loading.esconder();
    }
  }

  async carregarSelectsTipoUnidade() {
    try {
      const tipos = await this.service.listarTiposProduto();
      const unidades = await this.service.listarUnidadesMedida();
      const selectTipo = document.getElementById("tipoProduto");
      const selectUnidade = document.getElementById("unidadeMedida");

      if (selectTipo) {
        tipos.forEach((tipo) => {
          const option = document.createElement("option");
          option.value = tipo.idtipoproduto;
          option.textContent = tipo.descricao;
          selectTipo.appendChild(option);
        });
      }

      if (selectUnidade) {
        unidades.forEach((unidade) => {
          const option = document.createElement("option");
          option.value = unidade.idunidademedida;
          option.textContent = unidade.descricao;
          selectUnidade.appendChild(option);
        });
      }
    } catch (error) {
      throw new Error("Falha ao carregar tipos e unidades: " + error.message);
    }
  }

  async carregarMedicamento(id) {
    try {
      UIComponents.Loading.mostrar("Carregando medicamento...");
      const data = await this.service.buscarPorId(id);
      if (!data || !data.produto || !data.medicamento) {
        throw new Error("Dados do medicamento não encontrados.");
      }

      document.getElementById("idproduto").value = data.produto.idproduto;
      document.getElementById("nome").value = data.produto.nome || "";
      document.getElementById("composicao").value =
        data.medicamento.composicao || "";
      document.getElementById("fabricante").value =
        data.produto.fabricante || "";
      document.getElementById("tipoProduto").value = data.produto.idtipoproduto;
      document.getElementById("unidadeMedida").value =
        data.produto.idunidademedida;

      const precoField = document.getElementById("preco");
      if (precoField) {
        precoField.value = data.produto.preco
          ? UIComponents.InputMasks.formatarValorMonetario(data.produto.preco)
          : "";
      }

      const estoqueField = document.getElementById("estoqueMinimo");
      if (estoqueField) {
        estoqueField.value = data.produto.estoqueMinimo || "";
      }
    } catch (error) {
      throw new Error(
        "Não foi possível carregar os dados do medicamento: " + error.message
      );
    }
  }

  vincularEventosFiltro() {
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const clearSearchButton = document.getElementById("clearSearchButton");

    if (searchButton)
      searchButton.addEventListener("click", () =>
        this.filtrarMedicamentosAtivos()
      );
    if (searchInput)
      searchInput.addEventListener("keyup", (e) => {
        if (e.key === "Enter") this.filtrarMedicamentosAtivos();
      });
    if (clearSearchButton)
      clearSearchButton.addEventListener("click", () => {
        searchInput.value = "";
        this.renderizarTabelaAtivos(this.medicamentosAtivos);
      });
  }

  async carregarMedicamentosAtivos() {
    try {
      UIComponents.Loading.mostrar("Carregando medicamentos ativos...");
      this.medicamentosAtivos = await this.service.listarTodos();
      this.renderizarTabelaAtivos(this.medicamentosAtivos);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar medicamentos ativos: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async carregarMedicamentosInativos() {
    try {
      UIComponents.Loading.mostrar("Carregando medicamentos inativos...");
      this.medicamentosInativos = await this.service.listarTodosInativos();
      this.renderizarTabelaInativos(this.medicamentosInativos);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar medicamentos inativos: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderizarTabelaAtivos(medicamentos) {
    const tabela = document.getElementById("tabela-medicamentos");
    if (!tabela) return;
    tabela.innerHTML = "";
    if (medicamentos.length === 0) {
      tabela.innerHTML = `<tr><td colspan="6" class="text-center">Nenhum medicamento ativo encontrado.</td></tr>`;
      return;
    }
    medicamentos.forEach((med) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${med.produto.nome || "-"}</td>
        <td>${med.medicamento.composicao || "-"}</td>
        <td>${med.tipoProduto?.descricao || "-"}</td>
        <td>${med.unidadeMedida?.descricao || "-"}</td>
        <td>${med.produto.fabricante || "-"}</td>
        <td>
          <a href="editarMedicamento.html?id=${
            med.produto.idproduto
          }" class="btn btn-sm btn-primary" title="Editar"><i class="bi bi-pencil-fill"></i></a>
          <button onclick="medicamentoController.confirmarExclusao(${
            med.produto.idproduto
          })" class="btn btn-sm btn-outline-danger" title="Desativar"><i class="bi bi-trash-fill"></i></button>
        </td>`;
      tabela.appendChild(tr);
    });
  }

  renderizarTabelaInativos(medicamentos) {
    const tabela = document.getElementById("tabela-medicamentos-inativos");
    if (!tabela) return;
    tabela.innerHTML = "";
    if (medicamentos.length === 0) {
      tabela.innerHTML = `<tr><td colspan="4" class="text-center">Nenhum medicamento inativo encontrado.</td></tr>`;
      return;
    }
    medicamentos.forEach((med) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
            <td>${med.produto.nome || "-"}</td>
            <td>${med.medicamento.composicao || "-"}</td>
            <td>${med.tipoProduto?.descricao || "-"}</td>
            <td>
                <button onclick="medicamentoController.confirmarReativacao(${
                  med.produto.idproduto
                })" class="btn btn-sm btn-success" title="Reativar"><i class="bi bi-arrow-counterclockwise"></i> Reativar</button>
            </td>`;
      tabela.appendChild(tr);
    });
  }

  filtrarMedicamentosAtivos() {
    const termo = document
      .getElementById("searchInput")
      .value.trim()
      .toLowerCase();
    const filtro = document.getElementById("filterSelect").value;

    if (!termo) {
      this.renderizarTabelaAtivos(this.medicamentosAtivos);
      return;
    }

    const filtrados = this.medicamentosAtivos.filter((med) => {
      switch (filtro) {
        case "1":
          return med.produto.nome.toLowerCase().includes(termo);
        case "2":
          return med.medicamento.composicao.toLowerCase().includes(termo);
        case "3":
          return med.tipoProduto.descricao.toLowerCase().includes(termo);
        case "4":
          return med.produto.fabricante.toLowerCase().includes(termo);
        default:
          return true;
      }
    });
    this.renderizarTabelaAtivos(filtrados);
  }

  confirmarExclusao(id) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Desativação",
      "Tem certeza que deseja desativar este medicamento? Ele não poderá ser usado em novas operações, mas o histórico será mantido.",
      () => this.desativar(id)
    );
  }

  async desativar(id) {
    UIComponents.Loading.mostrar("Desativando...");
    try {
      const resposta = await this.service.excluir(id);
      if (resposta.sucesso) {
        UIComponents.Toast.sucesso(
          resposta.mensagem || "Medicamento desativado com sucesso."
        );
        this.inicializarListagem(); // Recarrega ambas as listas
      } else {
        UIComponents.Toast.erro(
          resposta.mensagem || MensagensPadroes.ERRO.EXCLUSAO
        );
      }
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        MensagensPadroes.ERRO.EXCLUSAO +
          (error.message ? `: ${error.message}` : "")
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  confirmarReativacao(id) {
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Reativação",
      "Deseja reativar este medicamento? Ele voltará a aparecer nas listas de seleção.",
      () => this.reativar(id)
    );
  }

  async reativar(id) {
    UIComponents.Loading.mostrar("Reativando...");
    try {
      await this.service.reativar(id);
      UIComponents.Toast.sucesso("Medicamento reativado com sucesso!");
      this.inicializarListagem(); // Recarrega ambas as listas
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao reativar: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
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
window.medicamentoController = medicamentoController; // Expondo para o HTML
export { medicamentoController };
