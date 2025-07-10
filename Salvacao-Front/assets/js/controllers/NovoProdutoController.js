import ProdutoService from "../services/ProdutoService.js";
import TipoProdutoService from "../services/TipoProdutoService.js";
import UnidadeMedidaService from "../services/UnidadeMedidaService.js";
import UIComponents from "../components/uiComponents.js";

class NovoProdutoController {
  constructor() {
    this.produtoService = new ProdutoService();
    this.tipoProdutoService = new TipoProdutoService();
    this.unidadeMedidaService = new UnidadeMedidaService();

    this.form = document.getElementById("form-novo-produto");
    this.selectTipoProduto = document.getElementById("tipoProduto");
    this.selectUnidadeMedida = document.getElementById("unidadeMedida");

    // Caches para guardar os dados completos dos selects
    this.unidadesCache = [];
    this.tiposCache = [];

    this.inicializar();
  }

  async inicializar() {
    UIComponents.Loading.mostrar("Carregando dados...");
    try {
      await this.carregarTiposProduto();
      await this.carregarUnidadesMedida();
      this.vincularEventos();
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Erro ao inicializar",
        "Não foi possível carregar os dados necessários: " + error.message
      );
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  vincularEventos() {
    this.form.addEventListener("submit", (event) => {
      event.preventDefault();
      this.salvarProduto();
    });

    this.selectTipoProduto.addEventListener("change", () => {
      this.verificarSeMedicamento();
    });
  }

  verificarSeMedicamento() {
    const selectedOption =
      this.selectTipoProduto.options[this.selectTipoProduto.selectedIndex];
    if (
      selectedOption &&
      selectedOption.text.toLowerCase().includes("medicamento")
    ) {
      UIComponents.ModalErro.mostrar(
        "Atenção: Cadastro de Medicamentos",
        "Para cadastrar um medicamento, utilize a página específica. Você será redirecionado."
      );
      setTimeout(() => {
        window.location.href = "../medicamentos/cadastrarMedicamento.html";
      }, 2500);
      return true;
    }
    return false;
  }

  async carregarTiposProduto() {
    try {
      const tipos = await this.tipoProdutoService.listarTodos();
      this.tiposCache = tipos; // Salva no cache
      this.selectTipoProduto.innerHTML =
        '<option value="">Selecione um tipo</option>';
      tipos.forEach((tipo) => {
        const option = new Option(tipo.descricao, tipo.idtipoproduto);
        this.selectTipoProduto.add(option);
      });
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Falha ao carregar tipos de produto.",
        error.message
      );
    }
  }

  async carregarUnidadesMedida() {
    try {
      const unidades = await this.unidadeMedidaService.getAll();
      this.unidadesCache = unidades;
      this.selectUnidadeMedida.innerHTML =
        '<option value="">Selecione uma unidade</option>';
      unidades.forEach((unidade) => {
        // CORREÇÃO: Usando a propriedade correta 'idUnidadeMedida'
        const option = new Option(
          `${unidade.descricao} (${unidade.sigla})`,
          unidade.idUnidadeMedida
        );
        this.selectUnidadeMedida.add(option);
      });
    } catch (error) {
      UIComponents.ModalErro.mostrar(
        "Falha ao carregar unidades de medida.",
        error.message
      );
    }
  }

  async salvarProduto() {
    if (!this.form.checkValidity()) {
      this.form.classList.add("was-validated");
      return;
    }
    if (this.verificarSeMedicamento()) return;

    const tipoId = this.selectTipoProduto.value;
    const unidadeId = this.selectUnidadeMedida.value;

    if (!tipoId || !unidadeId) {
      UIComponents.ModalErro.mostrar(
        "Dados Incompletos",
        "Por favor, selecione um Tipo de Produto e uma Unidade de Medida."
      );
      return;
    }

    const tipoCompleto = this.tiposCache.find((t) => t.idtipoproduto == tipoId);
    // CORREÇÃO: Buscando no cache com a propriedade correta 'idUnidadeMedida'
    const unidadeCompleta = this.unidadesCache.find(
      (u) => u.idUnidadeMedida == unidadeId
    );

    if (!tipoCompleto || !unidadeCompleta) {
      UIComponents.ModalErro.mostrar(
        "Erro de Dados",
        "Não foi possível encontrar os dados completos do tipo ou unidade. Tente recarregar a página."
      );
      return;
    }

    // **CORREÇÃO PRINCIPAL**: Montando a estrutura do JSON exatamente como o DTO do backend espera.
    const produtoData = {
      produto: {
        nome: document.getElementById("nome").value.trim(),
        fabricante: document.getElementById("fabricante").value.trim(),
        descricao: document.getElementById("descricao").value.trim(),
        // Incluindo os IDs DENTRO do objeto 'produto'
        idtipoproduto: parseInt(tipoId),
        idunidademedida: parseInt(unidadeId),
      },
      tipoProduto: {
        idtipoproduto: parseInt(tipoId),
        descricao: tipoCompleto.descricao,
      },
      unidadeMedida: {
        // Usando a propriedade correta 'idUnidadeMedida'
        idUnidadeMedida: parseInt(unidadeId),
        descricao: unidadeCompleta.descricao,
        sigla: unidadeCompleta.sigla,
      },
    };

    UIComponents.Loading.mostrar("Salvando produto...");
    try {
      // O serviço de produto já tem a URL correta: /produtos/cadastro
      const resultado = await this.produtoService.cadastrar(produtoData);
      localStorage.setItem(
        "mensagemSucesso",
        `Produto "${resultado.nome}" cadastrado com sucesso!`
      );
      window.location.href = "listar.html";
    } catch (error) {
      UIComponents.ModalErro.mostrar("Erro ao salvar o produto", error.message);
    } finally {
      UIComponents.Loading.esconder();
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  new NovoProdutoController();
});
