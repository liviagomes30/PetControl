import MedicamentoService from "../services/MedicamentoService.js";
import MedicamentoModel from "../models/MedicamentoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class MedicamentoController {
  constructor() {
    this.service = new MedicamentoService();
    this.medicamentosAtivos = [];
    this.medicamentosInativos = [];
    this.formSubmetido = false;

    // Roteador simples para inicializar a página correta
    if (window.location.pathname.includes("listarMedicamentos")) {
      this.inicializarListagem();
    } else if (
      window.location.pathname.includes("cadastrarMedicamento") ||
      window.location.pathname.includes("editarMedicamento")
    ) {
      this.inicializarFormulario();
    }
  }

  // --- MÉTODOS DA PÁGINA DE LISTAGEM ---

  inicializarListagem() {
    this.carregarMedicamentosAtivos();
    this.carregarMedicamentosInativos();
    this.vincularEventosFiltro();

    // Esta parte está correta e será a única a exibir o toast de sucesso
    const urlParams = new URLSearchParams(window.location.search);
    const message = urlParams.get("message");
    if (message) {
      UIComponents.Toast.sucesso(message);
      const newUrl = new URL(window.location.href);
      newUrl.searchParams.delete("message");
      window.history.replaceState({}, document.title, newUrl.toString());
    }
  }

  vincularEventosFiltro() {
    const searchInput = document.getElementById("searchInput");
    const filterSelect = document.getElementById("filterSelect");
    const clearSearchButton = document.getElementById("clearSearchButton");

    if (searchInput) {
      searchInput.addEventListener("input", () =>
        this.filtrarMedicamentosAtivos()
      );
    }
    if (filterSelect) {
      filterSelect.addEventListener("change", () =>
        this.filtrarMedicamentosAtivos()
      );
    }
    if (clearSearchButton) {
      clearSearchButton.addEventListener("click", () => {
        if (searchInput) searchInput.value = "";
        if (filterSelect) filterSelect.value = "1";
        this.renderizarTabelaAtivos(this.medicamentosAtivos);
      });
    }
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
        <td>${med.produto?.nome || "-"}</td>
        <td>${med.medicamento?.composicao || "-"}</td>
        <td>${med.tipoProduto?.descricao || "-"}</td>
        <td>${med.unidadeMedida?.descricao || "-"}</td>
        <td>${med.produto?.fabricante || "-"}</td>
        <td>
          <a href="editarMedicamento.html?id=${med.produto?.idproduto}" 
             class="btn btn-sm btn-primary" title="Editar"><i class="bi bi-pencil-fill"></i></a>
          <button onclick="medicamentoController.confirmarExclusao(${
            med.produto?.idproduto
          })" 
                  class="btn btn-sm btn-outline-danger" title="Desativar"><i class="bi bi-trash-fill"></i></button>
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
        <td>${med.produto?.nome || "-"}</td>
        <td>${med.medicamento?.composicao || "-"}</td>
        <td>${med.tipoProduto?.descricao || "-"}</td>
        <td>
          <button onclick="medicamentoController.confirmarReativacao(${
            med.produto?.idproduto
          })" 
                  class="btn btn-sm btn-success" title="Reativar"><i class="bi bi-arrow-counterclockwise"></i> Reativar</button>
        </td>`;
      tabela.appendChild(tr);
    });
  }

  filtrarMedicamentosAtivos() {
    const searchInput = document.getElementById("searchInput");
    const filterSelect = document.getElementById("filterSelect");
    if (!searchInput || !filterSelect) return;
    const termo = searchInput.value.trim().toLowerCase();
    const filtro = filterSelect.value;
    if (!termo) {
      this.renderizarTabelaAtivos(this.medicamentosAtivos);
      return;
    }
    const filtrados = this.medicamentosAtivos.filter((med) => {
      switch (filtro) {
        case "1":
          return med.produto?.nome?.toLowerCase().includes(termo);
        case "2":
          return med.medicamento?.composicao?.toLowerCase().includes(termo);
        case "3":
          return med.tipoProduto?.descricao?.toLowerCase().includes(termo);
        case "4":
          return med.produto?.fabricante?.toLowerCase().includes(termo);
        default:
          return false;
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
    try {
      UIComponents.Loading.mostrar("Desativando...");
      const resposta = await this.service.excluir(id);
      if (resposta.sucesso) {
        UIComponents.Toast.sucesso(
          resposta.mensagem || "Medicamento desativado com sucesso."
        );
        this.inicializarListagem();
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
    try {
      UIComponents.Loading.mostrar("Reativando...");
      await this.service.reativar(id);
      UIComponents.Toast.sucesso("Medicamento reativado com sucesso!");
      this.inicializarListagem();
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao reativar: " + error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  // --- MÉTODOS DA PÁGINA DE FORMULÁRIO ---

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
          if (alertaEstoque) alertaEstoque.style.display = "block";
        }
      }
      this.configurarMascarasCampos();
      this.vincularLimpezaAutomaticaValidacao();
      UIComponents.Loading.mostrar("Carregando dados...");
      await this.carregarSelectsTipoUnidade();
      if (id) await this.carregarMedicamento(id);
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error);
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar o formulário: " + (error.message || "")
      );
      UIComponents.Loading.esconder();
    }
  }

  vincularLimpezaAutomaticaValidacao() {
    const campos = [
      "nome",
      "composicao",
      "tipoProduto",
      "unidadeMedida",
      "preco",
      "estoqueMinimo",
      "fabricante",
    ];
    campos.forEach((id) => {
      const elemento = document.getElementById(id);
      if (elemento) {
        const evento = elemento.tagName === "SELECT" ? "change" : "input";
        elemento.addEventListener(evento, () =>
          UIComponents.Validacao.limparErroCampo(id)
        );
      }
    });
  }

  configurarMascarasCampos() {
    const camposComMascara = [
      { id: "preco", mask: "money", placeholder: "0,00", type: "text" },
      { id: "estoqueMinimo", mask: "number", placeholder: "0" },
    ];
    camposComMascara.forEach((c) => {
      const elemento = document.getElementById(c.id);
      if (elemento) {
        elemento.setAttribute("data-mask", c.mask);
        elemento.setAttribute("placeholder", c.placeholder);
        if (c.type) elemento.setAttribute("type", c.type);
      }
    });
    const camposComContador = [
      { id: "nome", maxLength: 100 },
      { id: "composicao", maxLength: 200 },
      { id: "fabricante", maxLength: 100 },
    ];
    camposComContador.forEach((c) => {
      const elemento = document.getElementById(c.id);
      if (elemento) {
        elemento.setAttribute("data-max-length", c.maxLength);
        if (c.id !== "fabricante")
          elemento.setAttribute("data-show-counter", "true");
      }
    });
    UIComponents.InputMasks.inicializar();
  }

  async carregarSelectsTipoUnidade() {
    try {
      const tipos = await this.service.listarTiposProduto();
      const unidades = await this.service.listarUnidadesMedida();
      const selectTipo = document.getElementById("tipoProduto");
      const selectUnidade = document.getElementById("unidadeMedida");
      if (selectTipo) {
        while (selectTipo.options.length > 1) selectTipo.remove(1);
        tipos.forEach((tipo) => {
          const option = document.createElement("option");
          option.value = tipo.idtipoproduto;
          option.textContent = tipo.descricao;
          selectTipo.appendChild(option);
        });
      }
      if (selectUnidade) {
        while (selectUnidade.options.length > 1) selectUnidade.remove(1);
        unidades.forEach((unidade) => {
          const id = unidade.idunidademedida ?? unidade.idUnidadeMedida;
          const option = document.createElement("option");
          option.value = id;
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
        throw new Error("Dados do medicamento não encontrados ou incompletos");
      }
      this.preencherCamposFormulario(data);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Falha ao carregar os dados: " + error.message
      );
      throw error;
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  preencherCamposFormulario(data) {
    const campos = {
      idproduto: data.produto.idproduto,
      nome: data.produto.nome,
      composicao: data.medicamento.composicao,
      fabricante: data.produto.fabricante,
      tipoProduto: data.produto.idtipoproduto,
      unidadeMedida:
        data.produto.idunidademedida ?? data.produto.idUnidadeMedida,
      preco: data.produto.preco,
      estoqueMinimo: data.produto.estoqueMinimo,
    };
    Object.entries(campos).forEach(([id, valor]) => {
      const elemento = document.getElementById(id);
      if (elemento) {
        if (id === "preco" && valor != null) {
          elemento.value =
            UIComponents.InputMasks.formatarValorMonetario(valor);
          elemento.dataset.valor = valor;
        } else {
          elemento.value = valor ?? "";
        }
      }
    });
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
        if (elementId)
          UIComponents.Validacao.mostrarErro(elementId, erros[campo]);
      }
    }
    return { valido, medicamento };
  }

  obterDadosFormulario() {
    const getValue = (id) => document.getElementById(id)?.value.trim() || "";
    const getNumericValue = (id) =>
      UIComponents.InputMasks.obterValorNumerico(document.getElementById(id));
    return new MedicamentoModel({
      produto: {
        idproduto:
          parseInt(document.getElementById("idproduto")?.value) || null,
        nome: getValue("nome"),
        idtipoproduto: parseInt(getValue("tipoProduto")) || null,
        idunidademedida: parseInt(getValue("unidadeMedida")) || null,
        fabricante: getValue("fabricante"),
        preco: getNumericValue("preco"),
        estoqueMinimo: getNumericValue("estoqueMinimo"),
      },
      medicamento: {
        idproduto:
          parseInt(document.getElementById("idproduto")?.value) || null,
        composicao: getValue("composicao"),
      },
    });
  }

  async cadastrar(event) {
    event.preventDefault();
    const { valido, medicamento } = this.validarFormulario();
    if (!valido) return;
    try {
      UIComponents.Loading.mostrar("Salvando medicamento...");
      await this.service.cadastrar(medicamento.toJSON());
      window.location.href = `listarMedicamentos.html?message=${encodeURIComponent(
        MensagensPadroes.SUCESSO.CADASTRO
      )}`;
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        error.message || MensagensPadroes.ERRO.CADASTRO
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async atualizar(event, id) {
    event.preventDefault();
    const { valido, medicamento } = this.validarFormulario();
    if (!valido) return;
    try {
      UIComponents.Loading.mostrar("Atualizando medicamento...");
      await this.service.atualizar(id, medicamento.toJSON());
      window.location.href = `listarMedicamentos.html?message=${encodeURIComponent(
        MensagensPadroes.SUCESSO.ATUALIZACAO
      )}`;
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        error.message || MensagensPadroes.ERRO.ATUALIZACAO
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  limparMensagensErro() {
    UIComponents.Validacao.limparErros("formMedicamento");
  }
}

const medicamentoController = new MedicamentoController();
window.medicamentoController = medicamentoController;
export { medicamentoController };
