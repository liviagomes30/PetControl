import ProdutoService from "../services/ProdutoService.js";
import TipoProdutoService from "../services/TipoProdutoService.js";
import UnidadeMedidaService from "../services/UnidadeMedidaService.js";
import UIComponents from "../components/uiComponents.js";

class ProdutoController {
  constructor() {
    this.produtoService = new ProdutoService();
    this.tipoProdutoService = new TipoProdutoService();
    this.unidadeMedidaService = new UnidadeMedidaService();
    this.unidadesCache = [];
    this.tiposCache = [];
    this.produtos = [];
    this.produtosInativos = [];

    const path = window.location.pathname;
    if (path.includes("listar.html")) {
      this.inicializarListagem();
    } else if (path.includes("novo.html") || path.includes("editar.html")) {
      this.inicializarFormulario();
    }
  }

  async inicializarListagem() {
    UIComponents.Loading.mostrar("Carregando...");
    try {
      await Promise.all([
        this.carregarFiltroTipos(document.getElementById("filtroTipo")),
        this.carregarProdutosAtivos(),
        this.carregarProdutosInativos(),
      ]);
      this.vincularEventosListagem();
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar a página",
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async carregarProdutosAtivos() {
    this.produtos = await this.produtoService.listarTodosComEstoque();
    this.renderizarTabelaAtivos(this.produtos);
  }

  async carregarProdutosInativos() {
    this.produtosInativos = await this.produtoService.listarTodosInativos();
    this.renderizarTabelaInativos(this.produtosInativos);
  }

  vincularEventosListagem() {
    document
      .getElementById("searchButton")
      .addEventListener("click", () => this.filtrarProdutos());
    document
      .getElementById("clearSearchButton")
      .addEventListener("click", () => this.limparFiltros());
    document
      .getElementById("filtroTipo")
      .addEventListener("change", () => this.filtrarProdutos());
    document.getElementById("searchInput").addEventListener("keyup", (e) => {
      if (e.key === "Enter") this.filtrarProdutos();
    });
  }

  limparFiltros() {
    document.getElementById("searchInput").value = "";
    document.getElementById("filtroTipo").value = "";
    document.getElementById("filterSelect").value = "1";
    // CORREÇÃO: Chamando o método correto para renderizar a tabela de ativos.
    this.renderizarTabelaAtivos(this.produtos);
  }

  // CORREÇÃO: Adicionado o botão de excluir/desativar
  renderizarTabelaAtivos(produtos) {
    const tabela = document.getElementById("tabela-produtos");
    tabela.innerHTML = "";
    if (!produtos || produtos.length === 0) {
      tabela.innerHTML = `<tr><td colspan="6" class="text-center">Nenhum produto ativo encontrado.</td></tr>`;
      return;
    }
    produtos.forEach((item) => {
      const tr = document.createElement("tr");
      const nomeProduto = item.produto?.nome.replace(/'/g, "\\'") || "-";
      tr.innerHTML = `
                <td>${item.produto?.nome || "-"}</td>
                <td>${item.tipoProduto?.descricao || "-"}</td>
                <td>${item.unidadeMedida?.sigla || "-"}</td>
                <td>${item.produto?.fabricante || "-"}</td>
                <td>${item.estoque?.quantidade || 0}</td>
                <td>
                    <a href="editar.html?id=${
                      item.produto?.idproduto
                    }" class="btn btn-sm btn-primary" title="Editar"><i class="bi bi-pencil-fill"></i></a>
                    <button onclick="produtoController.confirmarExclusao(${
                      item.produto?.idproduto
                    }, '${nomeProduto}')" class="btn btn-sm btn-outline-danger" title="Excluir/Desativar">
                        <i class="bi bi-trash-fill"></i>
                    </button>
                </td>`;
      tabela.appendChild(tr);
    });
  }

  renderizarTabelaInativos(produtos) {
    const tabela = document.getElementById("tabela-produtos-inativos");
    tabela.innerHTML = "";
    if (!produtos || produtos.length === 0) {
      tabela.innerHTML = `<tr><td colspan="4" class="text-center">Nenhum produto inativo encontrado.</td></tr>`;
      return;
    }
    produtos.forEach((item) => {
      const tr = document.createElement("tr");
      const nomeProduto = item.produto?.nome.replace(/'/g, "\\'") || "-";
      tr.innerHTML = `
                <td>${item.produto?.nome || "-"}</td>
                <td>${item.produto?.fabricante || "-"}</td>
                <td>${item.tipoProduto?.descricao || "-"}</td>
                <td>
                    <button onclick="produtoController.confirmarReativacao(${
                      item.produto?.idproduto
                    }, '${nomeProduto}')" class="btn btn-sm btn-success" title="Reativar">
                        <i class="bi bi-arrow-counterclockwise"></i> Reativar
                    </button>
                </td>`;
      tabela.appendChild(tr);
    });
  }

  filtrarProdutos() {
    const tipoId = document.getElementById("filtroTipo").value;
    const termo = document.getElementById("searchInput").value.toLowerCase();
    const filtroCampo = document.getElementById("filterSelect").value;
    let produtosFiltrados = this.produtos;

    if (tipoId) {
      produtosFiltrados = produtosFiltrados.filter(
        (item) => item.tipoProduto?.idtipoproduto == tipoId
      );
    }
    if (termo) {
      produtosFiltrados = produtosFiltrados.filter((item) => {
        let campo;
        if (filtroCampo === "1") campo = item.produto?.nome;
        else if (filtroCampo === "3") campo = item.produto?.fabricante;
        else return true;
        return campo?.toLowerCase().includes(termo);
      });
    }
    // CORREÇÃO: Chamando o método correto para renderizar a tabela de ativos.
    this.renderizarTabelaAtivos(produtosFiltrados);
  }

  // --- MÉTODOS DE EXCLUSÃO E REATIVAÇÃO ---

  confirmarExclusao(id, nome) {
    const mensagem = `Você tem certeza que deseja excluir o produto "${nome}"? Se ele estiver em uso no sistema, será desativado.`;
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Exclusão/Desativação",
      mensagem,
      () => this.excluir(id)
    );
  }

  async excluir(id) {
    UIComponents.Loading.mostrar("Processando exclusão...");
    try {
      const resultado = await this.produtoService.excluir(id);
      if (resultado.sucesso) {
        UIComponents.Toast.sucesso(resultado.mensagem);
      } else {
        UIComponents.Toast.alerta(resultado.mensagem);
      }
      // CORREÇÃO: Recarrega ambas as listas após a operação.
      await Promise.all([
        this.carregarProdutosAtivos(),
        this.carregarProdutosInativos(),
      ]);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao processar a solicitação",
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  confirmarReativacao(id, nome) {
    const mensagem = `Deseja reativar o produto "${nome}"? Ele voltará a aparecer nas listas de seleção.`;
    UIComponents.ModalConfirmacao.mostrar(
      "Confirmar Reativação",
      mensagem,
      () => this.reativar(id)
    );
  }

  async reativar(id) {
    UIComponents.Loading.mostrar("Reativando...");
    try {
      await this.produtoService.reativar(id);
      UIComponents.Toast.sucesso("Produto reativado com sucesso!");
      await Promise.all([
        this.carregarProdutosAtivos(),
        this.carregarProdutosInativos(),
      ]);
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao reativar o produto",
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  // --- MÉTODOS DO FORMULÁRIO (sem alterações) ---
  async inicializarFormulario() {
    UIComponents.Loading.mostrar("Carregando dados...");
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get("id");
    this.form = document.getElementById(
      id ? "form-editar-produto" : "form-novo-produto"
    );
    try {
      await this.carregarSelects();
      if (id) {
        document.title = "PetControl - Editar Produto";
        document.querySelector("h1").textContent = "Editar Produto";
        const produto = await this.produtoService.buscarPorId(id);
        if (!produto) throw new Error("Produto não encontrado.");
        this.preencherFormulario(produto);
      }
      this.vincularEventosFormulario();
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao inicializar formulário",
        error.message,
        () => {
          window.location.href = "listar.html";
        }
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }
  vincularEventosFormulario() {
    this.form.addEventListener("submit", (e) => {
      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      this.salvar(e, id);
    });
    document.getElementById("tipoProduto").addEventListener("change", () => {
      this.verificarSeMedicamento();
    });
  }
  preencherFormulario(data) {
    document.getElementById("nome").value = data.produto.nome || "";
    document.getElementById("fabricante").value = data.produto.fabricante || "";
    document.getElementById("descricao").value = data.produto.descricao || "";
    document.getElementById("tipoProduto").value =
      data.tipoProduto.idtipoproduto;
    document.getElementById("unidadeMedida").value =
      data.unidadeMedida.idUnidadeMedida;
  }
  async carregarSelects() {
    await this.carregarFiltroTipos(document.getElementById("tipoProduto"));
    await this.carregarUnidadesMedida(document.getElementById("unidadeMedida"));
  }
  async carregarFiltroTipos(selectElement) {
    if (!selectElement) return;
    try {
      const tipos = await this.tipoProdutoService.listarTodos();
      this.tiposCache = tipos;
      selectElement.innerHTML = `<option value="">Selecione...</option>`;
      tipos.forEach((tipo) => {
        selectElement.add(new Option(tipo.descricao, tipo.idtipoproduto));
      });
    } catch (error) {
      console.error("Falha ao carregar tipos.", error);
    }
  }
  async carregarUnidadesMedida(selectElement) {
    if (!selectElement) return;
    try {
      const unidades = await this.unidadeMedidaService.getAll();
      this.unidadesCache = unidades;
      selectElement.innerHTML = `<option value="">Selecione...</option>`;
      unidades.forEach((unidade) => {
        selectElement.add(
          new Option(
            `${unidade.descricao} (${unidade.sigla})`,
            unidade.idUnidadeMedida
          )
        );
      });
    } catch (error) {
      console.error("Falha ao carregar unidades.", error);
    }
  }
  verificarSeMedicamento() {
    const select = document.getElementById("tipoProduto");
    const selectedOption = select.options[select.selectedIndex];
    if (
      selectedOption &&
      selectedOption.text.toLowerCase().includes("medicamento")
    ) {
      UIComponents.ModalErro.mostrar(
        "Atenção: Ação Inválida",
        "Para cadastrar um medicamento, utilize a página específica. Você será redirecionado.",
        () => {
          window.location.href = "../medicamentos/cadastrarMedicamento.html";
        }
      );
      return true;
    }
    return false;
  }
  async salvar(event, id = null) {
    event.preventDefault();
    if (!this.form.checkValidity()) {
      this.form.classList.add("was-validated");
      return;
    }
    if (this.verificarSeMedicamento()) return;
    const tipoId = document.getElementById("tipoProduto").value;
    const unidadeId = document.getElementById("unidadeMedida").value;
    const tipoCompleto = this.tiposCache.find((t) => t.idtipoproduto == tipoId);
    const unidadeCompleta = this.unidadesCache.find(
      (u) => u.idUnidadeMedida == unidadeId
    );
    const produtoData = {
      produto: {
        idproduto: id ? parseInt(id) : null,
        nome: document.getElementById("nome").value.trim(),
        fabricante: document.getElementById("fabricante").value.trim(),
        descricao: document.getElementById("descricao").value.trim(),
        idtipoproduto: parseInt(tipoId),
        idunidademedida: parseInt(unidadeId),
      },
      tipoProduto: {
        idtipoproduto: parseInt(tipoId),
        descricao: tipoCompleto.descricao,
      },
      unidadeMedida: {
        idUnidadeMedida: parseInt(unidadeId),
        descricao: unidadeCompleta.descricao,
        sigla: unidadeCompleta.sigla,
      },
    };
    const acao = id ? "atualizar" : "cadastrar";
    const mensagemSucesso = `Produto "${produtoData.produto.nome}" ${
      id ? "atualizado" : "cadastrado"
    } com sucesso!`;
    UIComponents.Loading.mostrar(
      `${id ? "Atualizando" : "Salvando"} produto...`
    );
    try {
      if (acao === "cadastrar") {
        await this.produtoService.cadastrar(produtoData);
      } else {
        await this.produtoService.atualizar(id, produtoData);
      }
      localStorage.setItem("mensagemSucesso", mensagemSucesso);
      window.location.href = "listar.html";
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        `Erro ao ${acao} o produto`,
        error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

// Inicializa o controlador e o torna globalmente acessível
const produtoController = new ProdutoController();
window.produtoController = produtoController;
