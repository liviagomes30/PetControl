import AcertoEstoqueService from "../services/AcertoEstoqueService.js";
import AcertoEstoqueModel from "../models/AcertoEstoqueModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class AcertoEstoqueController {
  constructor() {
    this.service = new AcertoEstoqueService();
    this.itensAcerto = [];
    this.produtosCarregados = [];
  }

  async inicializarFormulario() {
    try {
      UIComponents.Validacao.limparErros("formAcertoEstoque");

      const form = document.getElementById("formAcertoEstoque");
      if (form) {
        form.addEventListener("submit", (e) => this.efetuarAcerto(e));
      }

      const btnAdicionarItem = document.getElementById("btnAdicionarItem");
      if (btnAdicionarItem) {
        btnAdicionarItem.addEventListener("click", () => {
          this.prepararModal();
          UIComponents.Loading.esconder();
          UIComponents.ModalHelper.abrirModal("adicionarItemModal");
        });
      }

      const btnConfirmarItem = document.getElementById("btnConfirmarItem");
      if (btnConfirmarItem) {
        btnConfirmarItem.addEventListener("click", () => this.adicionarItem());
      }

      const selectProduto = document.getElementById("selectProduto");
      if (selectProduto) {
        selectProduto.addEventListener("change", (e) =>
          this.buscarEstoqueAtual(e.target.value)
        );
      }

      const novaQuantidadeInput = document.getElementById("novaQuantidade");
      if (novaQuantidadeInput) {
        novaQuantidadeInput.setAttribute("data-mask", "decimal");
        UIComponents.InputMasks.inicializar(); // Still using UIComponents.InputMasks
      }

      UIComponents.Loading.mostrar("Carregando produtos...");
      await this.carregarProdutos();
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error);
      UIComponents.ModalErro.mostrar(
        // Still using UIComponents.ModalErro
        "Não foi possível carregar o formulário. " +
          (error.message || "Erro desconhecido.")
      );
      UIComponents.Loading.esconder();
    }
  }

  async carregarProdutos() {
    try {
      const produtos = await this.service.listarProdutos();
      this.produtosCarregados = produtos;

      const selectProduto = document.getElementById("selectProduto");
      if (selectProduto) {
        while (selectProduto.options.length > 1) {
          selectProduto.remove(1);
        }
        produtos.forEach((produto) => {
          const option = document.createElement("option");
          option.value = produto.produto.idproduto;
          const tipoProduto = produto.tipoProduto?.descricao || "";
          const unidade = produto.unidadeMedida?.sigla || "";
          option.textContent = `${produto.produto.nome} (${tipoProduto}) - ${unidade}`;
          selectProduto.appendChild(option);
        });
      }
    } catch (error) {
      console.error("Erro ao carregar produtos:", error);
      throw new Error(
        "Não foi possível carregar a lista de produtos: " +
          (error.message || "Erro desconhecido.")
      );
    }
  }

  async buscarEstoqueAtual(produtoId) {
    if (!produtoId) {
      document.getElementById("quantidadeAtual").value = "";
      return;
    }
    try {
      UIComponents.Loading.mostrar("Consultando estoque...");
      const estoque = await this.service.obterEstoqueAtual(produtoId);
      const quantidadeAtualInput = document.getElementById("quantidadeAtual");
      if (quantidadeAtualInput) {
        const quantidade = parseFloat(estoque.quantidade || 0).toFixed(2);
        quantidadeAtualInput.value = quantidade.replace(".", ",");
      }
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao buscar estoque atual:", error);
      UIComponents.Loading.esconder();
      const quantidadeAtualInput = document.getElementById("quantidadeAtual");
      if (quantidadeAtualInput) {
        quantidadeAtualInput.value = "0,00";
      }
      UIComponents.ModalErro.mostrar(
        "Não foi possível consultar o estoque atual do produto."
      );
    }
  }

  prepararModal() {
    const selectProduto = document.getElementById("selectProduto");
    const quantidadeAtual = document.getElementById("quantidadeAtual");
    const novaQuantidade = document.getElementById("novaQuantidade");
    if (selectProduto) selectProduto.value = "";
    if (quantidadeAtual) quantidadeAtual.value = "";
    if (novaQuantidade) novaQuantidade.value = "";
    UIComponents.Validacao.limparErros("formItem");
  }

  adicionarItem() {
    const produtoId = document.getElementById("selectProduto")?.value;
    const novaQuantidade = document.getElementById("novaQuantidade")?.value;
    let valido = true;
    if (!produtoId) {
      UIComponents.Validacao.mostrarErro(
        "selectProduto",
        "Selecione um produto"
      );
      valido = false;
    }
    if (!novaQuantidade) {
      UIComponents.Validacao.mostrarErro(
        "novaQuantidade",
        "Informe a nova quantidade"
      );
      valido = false;
    } else {
      const novaQtdNum = parseFloat(novaQuantidade.replace(",", "."));
      if (isNaN(novaQtdNum)) {
        UIComponents.Validacao.mostrarErro(
          "novaQuantidade",
          "Informe um valor numérico válido"
        );
        valido = false;
      } else if (novaQtdNum < 0) {
        UIComponents.Validacao.mostrarErro(
          "novaQuantidade",
          "A quantidade não pode ser negativa"
        );
        valido = false;
      }
    }
    if (!valido) return;
    const produtoExistente = this.itensAcerto.find(
      (item) => item.produto_id === parseInt(produtoId)
    );
    if (produtoExistente) {
      UIComponents.ModalErro.mostrar(
        "Este produto já foi adicionado ao acerto"
      );
      return;
    }
    const produtoSelecionado = this.produtosCarregados.find(
      (p) => p.produto.idproduto === parseInt(produtoId)
    );
    if (!produtoSelecionado) {
      UIComponents.ModalErro.mostrar("Produto não encontrado");
      return;
    }
    const quantidadeAtual =
      document.getElementById("quantidadeAtual")?.value || "0,00";
    const qtdAtualNum = parseFloat(quantidadeAtual.replace(",", "."));
    const novaQtdNum = parseFloat(novaQuantidade.replace(",", "."));
    let tipoAjuste = "ENTRADA";
    if (novaQtdNum < qtdAtualNum) {
      tipoAjuste = "SAIDA";
    } else if (novaQtdNum === qtdAtualNum) {
      tipoAjuste = "SEM ALTERAÇÃO";
    }
    const novoItem = {
      produto_id: parseInt(produtoId),
      nome_produto: produtoSelecionado.produto.nome,
      quantidade_antes: qtdAtualNum,
      quantidade_nova: novaQtdNum,
      tipoajuste: tipoAjuste,
    };
    this.itensAcerto.push(novoItem);
    this.atualizarTabelaItens();

    const modalElement = document.getElementById("adicionarItemModal");
    let modalInstance = bootstrap.Modal.getInstance(modalElement);
    if (!modalInstance) {
      modalInstance = new bootstrap.Modal(modalElement);
    }
    modalInstance.hide();
    UIComponents.ModalHelper.fecharModal("adicionarItemModal");
    UIComponents.Toast.sucesso("Item adicionado com sucesso");
  }

  removerItem(index) {
    this.itensAcerto.splice(index, 1);
    this.atualizarTabelaItens();
    UIComponents.Toast.sucesso("Item removido com sucesso");
  }

  atualizarTabelaItens() {
    const tabela = document.getElementById("itensAcerto");
    if (!tabela) return;
    tabela.innerHTML = "";
    if (this.itensAcerto.length === 0) {
      tabela.innerHTML = ` 
        <tr id="nenhumItem"> 
          <td colspan="5" class="text-center">Nenhum item adicionado</td> 
        </tr>
      `;
      return;
    }
    this.itensAcerto.forEach((item, index) => {
      const tr = document.createElement("tr");
      let tipoAjusteClass = "";
      if (item.tipoajuste === "ENTRADA") {
        tipoAjusteClass = "text-success";
      } else if (item.tipoajuste === "SAIDA") {
        tipoAjusteClass = "text-danger";
      }
      tr.innerHTML = ` 
        <td>${item.nome_produto}</td> 
        <td>${item.quantidade_antes.toFixed(2).replace(".", ",")}</td> 
        <td>${item.quantidade_nova.toFixed(2).replace(".", ",")}</td> 
        <td class="${tipoAjusteClass}">${item.tipoajuste}</td> 
        <td> 
          <button type="button" class="btn btn-sm btn-outline-danger btn-remover-item" data-index="${index}"> 
            <i class="bi bi-trash "></i> 
          </button>
        </td>
      `;
      tabela.appendChild(tr);
    });

    const removerButtons = tabela.querySelectorAll(".btn-remover-item");
    removerButtons.forEach((button) => {
      button.addEventListener("click", (e) => {
        const indexToRemove = parseInt(e.currentTarget.dataset.index);
        this.removerItem(indexToRemove);
      });
    });
  }

  async efetuarAcerto(event) {
    event.preventDefault();
    try {
      UIComponents.Validacao.limparErros("formAcertoEstoque");
      const motivo = document.getElementById("motivo")?.value;
      let motivoFinal = motivo;
      if (motivo === "Outro") {
        const motivoOutro = document.getElementById("motivoOutro")?.value;
        if (!motivoOutro || !motivoOutro.trim()) {
          UIComponents.Validacao.mostrarErro(
            "motivoOutro",
            "Especifique o motivo"
          );
          return;
        }
        motivoFinal = motivoOutro;
      }
      const observacao = document.getElementById("observacao")?.value || "";
      const acertoEstoque = new AcertoEstoqueModel({
        usuario_pessoa_id: 1,
        motivo: motivoFinal,
        observacao: observacao,
        itens: this.itensAcerto,
      });
      const { valido, erros } = acertoEstoque.validar();
      if (!valido) {
        if (erros.motivo) {
          UIComponents.Validacao.mostrarErro("motivo", erros.motivo);
        }
        if (erros.itens) {
          UIComponents.ModalErro.mostrar(erros.itens);
        }
        return;
      }
      UIComponents.ModalConfirmacao.mostrar(
        "Confirmar acerto de estoque",
        "Deseja confirmar o acerto de estoque? Esta operação não poderá ser desfeita.",
        async () => {
          try {
            UIComponents.Loading.mostrar("Processando acerto de estoque...");
            const resultado = await this.service.efetuarAcerto(
              acertoEstoque.toJSON()
            );
            console.log("Resultado do acerto:", resultado);
            UIComponents.Toast.sucesso(
              "Acerto de estoque realizado com sucesso!"
            );
            window.location.href =
              "listarAcertosEstoque.html?message=Acerto de estoque realizado com sucesso!";
          } catch (error) {
            console.error("Erro ao efetuar acerto:", error);
            UIComponents.ModalErro.mostrar(
              "Não foi possível efetuar o acerto de estoque: " +
                (error.message || "")
            );
            UIComponents.Loading.esconder();
          }
        }
      );
    } catch (error) {
      console.error("Erro ao processar acerto:", error);
      UIComponents.ModalErro.mostrar(
        "Erro ao processar o acerto de estoque: " + (error.message || "")
      );
    }
  }

  async inicializarListagem() {
    try {
      UIComponents.Loading.mostrar("Carregando acertos de estoque...");
      const acertos = await this.service.listarTodos();
      this.atualizarTabelaAcertos(acertos);
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao carregar lista de acertos:", error);
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar a lista de acertos de estoque."
      );
      UIComponents.Loading.esconder();
    }
  }

  atualizarTabelaAcertos(acertosRetornados) {
    const tabela = document.getElementById("tabela-acertos");
    if (!tabela) return;

    tabela.innerHTML = "";

    if (!Array.isArray(acertosRetornados) || acertosRetornados.length === 0) {
      tabela.innerHTML = `
            <tr>
                <td colspan="5" class="text-center">Nenhum acerto de estoque encontrado.</td>
            </tr>
        `;
      return;
    }

    acertosRetornados.forEach((acertoModel) => {
      if (acertoModel.idacerto === undefined || acertoModel.idacerto === null) {
        console.warn(
          "Acerto sem ID válido (idacerto) encontrado, pulando:",
          acertoModel
        );
        return;
      }

      const tr = document.createElement("tr");
      const dataFormatada = new Date(acertoModel.data).toLocaleDateString(
        "pt-BR"
      );
      tr.innerHTML = `
            <td>${acertoModel.idacerto}</td>
            <td>${dataFormatada}</td>
            <td>${acertoModel.motivo}</td>
            <td>${acertoModel.observacao || "-"}</td>
            <td>
                <a href="detalhesAcertoEstoque.html?id=${
                  acertoModel.idacerto
                }" class="btn btn-sm btn-primary">
                    <i class="bi bi-eye me-1"></i> Detalhes
                </a>
            </td>
        `;
      tabela.appendChild(tr);
    });
  }

  async aplicarFiltro(event) {
    event.preventDefault();
    try {
      UIComponents.Loading.mostrar("Aplicando filtro...");
      const dataInicio = document.getElementById("dataInicio").value;
      const dataFim = document.getElementById("dataFim").value;

      const acertosFiltrados = await this.service.buscarPorPeriodo(
        dataInicio,
        dataFim
      );
      this.atualizarTabelaAcertos(acertosFiltrados);
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao aplicar filtro:", error);
      UIComponents.ModalErro.mostrar("Não foi possível aplicar o filtro.");
      UIComponents.Loading.esconder();
    }
  }

  async carregarDetalhesAcerto(id) {
    try {
      UIComponents.Loading.mostrar("Carregando detalhes do acerto...");
      const acertoRetornado = await this.service.buscarPorId(id);

      if (acertoRetornado && acertoRetornado.acertoEstoque) {
        const acerto = acertoRetornado.acertoEstoque;
        const usuario = acertoRetornado.usuario;

        document.getElementById("idAcerto").textContent = acerto.idacerto;
        document.getElementById("dataAcerto").textContent = new Date(
          acerto.data
        ).toLocaleDateString("pt-BR");
        document.getElementById("usuarioAcerto").textContent =
          usuario?.pessoa?.nome || "N/A";
        document.getElementById("motivoAcerto").textContent = acerto.motivo;
        document.getElementById("observacaoAcerto").textContent =
          acerto.observacao || "N/A";
        this.atualizarTabelaItensDetalhe(acertoRetornado.itens);
      } else {
        UIComponents.ModalErro.mostrar("Acerto de estoque não encontrado.");
        setTimeout(() => {
          window.location.href = "listarAcertosEstoque.html";
        }, 2000);
      }
      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao carregar detalhes do acerto:", error);
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar os detalhes do acerto de estoque."
      );
      UIComponents.Loading.esconder();
    }
  }

  atualizarTabelaItensDetalhe(itensWrapper) {
    const tabelaItens = document.getElementById("itensAcertoDetalhe");
    if (!tabelaItens) return;

    tabelaItens.innerHTML = "";

    if (!Array.isArray(itensWrapper) || itensWrapper.length === 0) {
      tabelaItens.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">Nenhum item neste acerto.</td>
            </tr>
        `;
      return;
    }

    itensWrapper.forEach((itemWrapper) => {
      const item = itemWrapper.item;
      const produto = itemWrapper.produto;

      const tr = document.createElement("tr");

      const quantidadeAntes = item.quantidade_antes ?? 0;
      const quantidadeNova = item.quantidade_depois ?? 0;

      const diferenca = (quantidadeNova - quantidadeAntes).toFixed(2);
      let diferencaClass = "";
      if (diferenca > 0) {
        diferencaClass = "text-success";
      } else if (diferenca < 0) {
        diferencaClass = "text-danger";
      }

      let tipoAjusteClass = "";
      if (item.tipoajuste === "ENTRADA") {
        tipoAjusteClass = "text-success";
      } else if (item.tipoajuste === "SAIDA") {
        tipoAjusteClass = "text-danger";
      }

      tr.innerHTML = `
            <td>${produto?.nome || "N/A"}</td>
            <td>${itemWrapper.nomeTipoProduto || "N/A"}</td>
            <td>${itemWrapper.unidadeMedida || "N/A"}</td>
            <td>${quantidadeAntes.toFixed(2).replace(".", ",")}</td>
            <td>${quantidadeNova.toFixed(2).replace(".", ",")}</td>
            <td class="${diferencaClass}">${diferenca.replace(".", ",")}</td>
            <td class="${tipoAjusteClass}">${item.tipoajuste || "N/A"}</td>
        `;
      tabelaItens.appendChild(tr);
    });
  }
}

export default new AcertoEstoqueController();
