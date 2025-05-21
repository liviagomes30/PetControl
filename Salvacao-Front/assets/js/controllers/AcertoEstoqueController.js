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

      // Configurar formulário para envio
      const form = document.getElementById("formAcertoEstoque");
      if (form) {
        form.addEventListener("submit", (e) => this.efetuarAcerto(e));
      }

      // Configurar botão para adicionar item
      const btnAdicionarItem = document.getElementById("btnAdicionarItem");
      if (btnAdicionarItem) {
        btnAdicionarItem.addEventListener("click", () => {
          this.prepararModal();
          UIComponents.ModalHelper.abrirModal("adicionarItemModal");
        });
      }

      // Configurar botão para confirmar adição de item no modal
      const btnConfirmarItem = document.getElementById("btnConfirmarItem");
      if (btnConfirmarItem) {
        btnConfirmarItem.addEventListener("click", () => this.adicionarItem());
      }

      // Configurar botões para fechar o modal
      const botoesFecharModal = document.querySelectorAll(
        '[data-bs-dismiss="modal"]'
      );
      botoesFecharModal.forEach((botao) => {
        botao.addEventListener("click", () => {
          UIComponents.ModalHelper.fecharModal("adicionarItemModal");
        });
      });

      // Configurar seleção de produto no modal para buscar quantidade atual
      const selectProduto = document.getElementById("selectProduto");
      if (selectProduto) {
        selectProduto.addEventListener("change", (e) =>
          this.buscarEstoqueAtual(e.target.value)
        );
      }

      // Inicializar a máscara para o campo de nova quantidade
      const novaQuantidadeInput = document.getElementById("novaQuantidade");
      if (novaQuantidadeInput) {
        novaQuantidadeInput.setAttribute("data-mask", "decimal");
        UIComponents.InputMasks.inicializar();
      }

      // Carregar produtos para o select
      await this.carregarProdutos();

      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao inicializar formulário:", error);
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar o formulário: " + (error.message || "")
      );
      UIComponents.Loading.esconder();
    }
  }

  async carregarProdutos() {
    try {
      UIComponents.Loading.mostrar("Carregando produtos...");
      const produtos = await this.service.listarProdutos();
      this.produtosCarregados = produtos;

      const selectProduto = document.getElementById("selectProduto");
      if (selectProduto) {
        // Limpar opções existentes, mantendo apenas a opção padrão
        while (selectProduto.options.length > 1) {
          selectProduto.remove(1);
        }

        // Adicionar produtos ao select
        produtos.forEach((produto) => {
          const option = document.createElement("option");
          option.value = produto.produto.idproduto;

          // Montar descrição do produto com informações relevantes
          const tipoProduto = produto.tipoProduto?.descricao || "";
          const unidade = produto.unidadeMedida?.sigla || "";

          option.textContent = `${produto.produto.nome} (${tipoProduto}) - ${unidade}`;
          selectProduto.appendChild(option);
        });
      }

      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao carregar produtos:", error);
      UIComponents.Loading.esconder();
      UIComponents.ModalErro.mostrar(
        "Não foi possível carregar a lista de produtos: " +
          (error.message || "")
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
        // Formatar quantidade com duas casas decimais
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
    }
  }

  prepararModal() {
    // Limpar campos do modal
    const selectProduto = document.getElementById("selectProduto");
    const quantidadeAtual = document.getElementById("quantidadeAtual");
    const novaQuantidade = document.getElementById("novaQuantidade");

    if (selectProduto) selectProduto.value = "";
    if (quantidadeAtual) quantidadeAtual.value = "";
    if (novaQuantidade) novaQuantidade.value = "";

    // Limpar mensagens de erro
    UIComponents.Validacao.limparErros("formItem");
  }

  adicionarItem() {
    // Validar campos
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
      // Converter para número (considerando formatação brasileira)
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

    // Verificar se o produto já foi adicionado
    const produtoExistente = this.itensAcerto.find(
      (item) => item.produto_id === parseInt(produtoId)
    );
    if (produtoExistente) {
      UIComponents.ModalErro.mostrar(
        "Este produto já foi adicionado ao acerto"
      );
      return;
    }

    // Obter informações do produto selecionado
    const produtoSelecionado = this.produtosCarregados.find(
      (p) => p.produto.idproduto === parseInt(produtoId)
    );

    if (!produtoSelecionado) {
      UIComponents.ModalErro.mostrar("Produto não encontrado");
      return;
    }

    // Obter quantidade atual
    const quantidadeAtual =
      document.getElementById("quantidadeAtual")?.value || "0,00";
    const qtdAtualNum = parseFloat(quantidadeAtual.replace(",", "."));

    // Converter nova quantidade considerando formato brasileiro
    const novaQtdNum = parseFloat(novaQuantidade.replace(",", "."));

    // Determinar tipo de ajuste
    let tipoAjuste = "ENTRADA";
    if (novaQtdNum < qtdAtualNum) {
      tipoAjuste = "SAIDA";
    } else if (novaQtdNum === qtdAtualNum) {
      tipoAjuste = "SEM ALTERAÇÃO";
    }

    // Criar o item de acerto
    const novoItem = {
      produto_id: parseInt(produtoId),
      nome_produto: produtoSelecionado.produto.nome,
      quantidade_antes: qtdAtualNum,
      quantidade_nova: novaQtdNum,
      tipoajuste: tipoAjuste,
    };

    // Adicionar à lista de itens
    this.itensAcerto.push(novoItem);

    // Atualizar a tabela
    this.atualizarTabelaItens();

    // Fechar o modal usando nosso utilitário
    UIComponents.ModalHelper.fecharModal("adicionarItemModal");

    // Mostrar mensagem de sucesso
    UIComponents.Toast.sucesso("Item adicionado com sucesso");
  }

  removerItem(index) {
    // Remover o item do array
    this.itensAcerto.splice(index, 1);

    // Atualizar a tabela
    this.atualizarTabelaItens();

    // Mostrar mensagem
    UIComponents.Toast.sucesso("Item removido com sucesso");
  }

  atualizarTabelaItens() {
    const tabela = document.getElementById("itensAcerto");
    if (!tabela) return;

    // Limpar tabela
    tabela.innerHTML = "";

    // Se não houver itens, mostrar mensagem
    if (this.itensAcerto.length === 0) {
      tabela.innerHTML = `
        <tr id="nenhumItem">
          <td colspan="5" class="text-center">Nenhum item adicionado</td>
        </tr>
      `;
      return;
    }

    // Adicionar cada item à tabela
    this.itensAcerto.forEach((item, index) => {
      const tr = document.createElement("tr");

      // Determinar a classe CSS baseada no tipo de ajuste
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
          <button type="button" class="btn btn-sm btn-outline-danger" onclick="acertoEstoqueController.removerItem(${index})">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      `;

      tabela.appendChild(tr);
    });
  }

  async efetuarAcerto(event) {
    event.preventDefault();

    try {
      UIComponents.Validacao.limparErros("formAcertoEstoque");

      // Obter dados do formulário
      const motivo = document.getElementById("motivo")?.value;
      let motivoFinal = motivo;

      // Se o motivo for "Outro", pegar o valor do campo personalizado
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

      // Criar o modelo de acerto
      const acertoEstoque = new AcertoEstoqueModel({
        usuario_pessoa_id: 1, // Exemplo - deve ser obtido do usuário logado
        motivo: motivoFinal,
        observacao: observacao,
        itens: this.itensAcerto,
      });

      // Validar o acerto
      const { valido, erros } = acertoEstoque.validar();

      if (!valido) {
        // Exibir mensagens de erro
        if (erros.motivo) {
          UIComponents.Validacao.mostrarErro("motivo", erros.motivo);
        }

        if (erros.itens) {
          UIComponents.ModalErro.mostrar(erros.itens);
        }

        return;
      }

      // Confirmar a operação
      UIComponents.ModalConfirmacao.mostrar(
        "Confirmar acerto de estoque",
        "Deseja confirmar o acerto de estoque? Esta operação não poderá ser desfeita.",
        async () => {
          try {
            UIComponents.Loading.mostrar("Processando acerto de estoque...");

            // Enviar para o servidor
            const resultado = await this.service.efetuarAcerto(
              acertoEstoque.toJSON()
            );

            console.log("Resultado do acerto:", resultado);

            UIComponents.Toast.sucesso(
              "Acerto de estoque realizado com sucesso!"
            );

            // Limpar o formulário e redirecionar após 2 segundos
            setTimeout(() => {
              window.location.href =
                "listarAcertosEstoque.html?message=Acerto de estoque realizado com sucesso!";
            }, 2000);
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
      this.renderizarTabela(acertos);

      // Verificar mensagem na URL
      const urlParams = new URLSearchParams(window.location.search);
      const message = urlParams.get("message");
      if (message) {
        UIComponents.Toast.sucesso(message);
        // Remover o parâmetro para não mostrar a mensagem novamente ao atualizar
        window.history.replaceState(
          {},
          document.title,
          window.location.pathname
        );
      }
    } catch (error) {
      console.error("Erro ao carregar acertos:", error);
      UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  renderizarTabela(acertos) {
    const tabela = document.getElementById("tabela-acertos");
    if (!tabela) return;

    tabela.innerHTML = "";

    if (acertos.length === 0) {
      tabela.innerHTML = `
        <tr>
          <td colspan="5" class="text-center">Nenhum acerto de estoque encontrado.</td>
        </tr>
      `;
      return;
    }

    acertos.forEach((acerto) => {
      const data = new Date(acerto.data);
      const dataFormatada = data.toLocaleDateString("pt-BR");

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${acerto.idacerto}</td>
        <td>${dataFormatada}</td>
        <td>${acerto.motivo || "-"}</td>
        <td>${acerto.observacao || "-"}</td>
        <td>
          <a href="detalhesAcertoEstoque.html?id=${
            acerto.idacerto
          }" class="btn btn-sm btn-primary">
            <i class="bi bi-eye"></i> Detalhes
          </a>
        </td>
      `;
      tabela.appendChild(tr);
    });
  }

  async aplicarFiltro(event) {
    event.preventDefault();

    const dataInicio = document.getElementById("dataInicio")?.value;
    const dataFim = document.getElementById("dataFim")?.value;

    if (!dataInicio || !dataFim) {
      UIComponents.Toast.alerta(
        "Informe as datas de início e fim para filtrar"
      );
      return;
    }

    try {
      UIComponents.Loading.mostrar("Filtrando acertos de estoque...");
      const acertos = await this.service.buscarPorPeriodo(dataInicio, dataFim);
      this.renderizarTabela(acertos);
    } catch (error) {
      console.error("Erro ao filtrar acertos:", error);
      UIComponents.ModalErro.mostrar("Erro ao filtrar acertos de estoque");
    } finally {
      UIComponents.Loading.esconder();
    }
  }

  async carregarDetalhesAcerto(id) {
    try {
      UIComponents.Loading.mostrar("Carregando detalhes do acerto...");
      const acertoCompleto = await this.service.buscarPorId(id);

      // Preencher os dados do acerto
      const idAcerto = document.getElementById("idAcerto");
      if (idAcerto)
        idAcerto.textContent = acertoCompleto.acertoEstoque.idacerto;

      // Formatar data
      const data = new Date(acertoCompleto.acertoEstoque.data);
      const dataAcerto = document.getElementById("dataAcerto");
      if (dataAcerto) dataAcerto.textContent = data.toLocaleDateString("pt-BR");

      const motivoAcerto = document.getElementById("motivoAcerto");
      if (motivoAcerto)
        motivoAcerto.textContent = acertoCompleto.acertoEstoque.motivo || "-";

      const observacaoAcerto = document.getElementById("observacaoAcerto");
      if (observacaoAcerto)
        observacaoAcerto.textContent =
          acertoCompleto.acertoEstoque.observacao || "-";

      const usuarioAcerto = document.getElementById("usuarioAcerto");
      if (usuarioAcerto && acertoCompleto.usuario) {
        usuarioAcerto.textContent = acertoCompleto.usuario.nome || "-";
      }

      // Renderizar a tabela de itens
      const tabelaItens = document.getElementById("itensAcertoDetalhe");
      if (tabelaItens && acertoCompleto.itens) {
        tabelaItens.innerHTML = "";

        acertoCompleto.itens.forEach((item) => {
          const tr = document.createElement("tr");

          // Determinar a classe CSS baseada no tipo de ajuste
          let tipoAjusteClass = "";
          if (item.item.tipoajuste === "ENTRADA") {
            tipoAjusteClass = "text-success";
          } else if (item.item.tipoajuste === "SAIDA") {
            tipoAjusteClass = "text-danger";
          }

          // Calcular a diferença
          const qtdAntes = parseFloat(item.item.quantidade_antes);
          const qtdDepois = parseFloat(item.item.quantidade_depois);
          const diferenca = qtdDepois - qtdAntes;
          let diferencaStr = diferenca.toFixed(2).replace(".", ",");
          if (diferenca > 0) {
            diferencaStr = "+" + diferencaStr;
          }

          tr.innerHTML = `
            <td>${item.produto?.nome || "-"}</td>
            <td>${item.nomeTipoProduto || "-"}</td>
            <td>${item.unidadeMedida || "-"}</td>
            <td>${item.item.quantidade_antes.toFixed(2).replace(".", ",")}</td>
            <td>${item.item.quantidade_depois.toFixed(2).replace(".", ",")}</td>
            <td>${diferencaStr}</td>
            <td class="${tipoAjusteClass}">${item.item.tipoajuste}</td>
          `;

          tabelaItens.appendChild(tr);
        });
      }

      UIComponents.Loading.esconder();
    } catch (error) {
      console.error("Erro ao carregar detalhes:", error);
      UIComponents.ModalErro.mostrar(
        "Erro ao carregar detalhes do acerto de estoque"
      );
      UIComponents.Loading.esconder();
    }
  }
}

const acertoEstoqueController = new AcertoEstoqueController();
window.acertoEstoqueController = acertoEstoqueController;

export { acertoEstoqueController };
export default AcertoEstoqueController;
