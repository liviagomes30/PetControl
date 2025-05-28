import SaidaProdutoService from "../services/SaidaProdutoService.js";
import EntradaProdutoModel from "../models/EntradaProdutoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class SaidaProdutoController{
    constructor(){
        this.service = new SaidaProdutoService();
        this.itensSaida = [];
        this.produtosCarregados = [];
    }

    async inicializarListagem() {
        try {
            UIComponents.Loading.mostrar("Carregando produto...");
            const entprodutos = await this.service.listarTodos();
            this.renderizarTabela(entprodutos);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(message);
            }
            } catch (error) {
            console.error("Erro ao carregar produto:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
            } finally {
            UIComponents.Loading.esconder();
        }
    }

    async inicializarListagemRegistros(){
        try {
            console.log("AQUI");
            UIComponents.Loading.mostrar("Carregando registros...");
            const registros = await this.service.listarTodosRegistros();
            this.renderizarTabelaRegistros(registros);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(message);
            }
            } catch (error) {
            console.error("Erro ao carregar registro:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
            } finally {
            UIComponents.Loading.esconder();
        }
    }

    async carregarDetalhesSaida(id){
        try{
            UIComponents.Loading.mostrar("Carregando detalhes da saída...");
            const itens = await this.service.listarItens(id);
            const tabela = document.getElementById("itensSaida");

            if (!tabela) return;

           tabela.innerHTML = "";

            if(itens.length === 0){
                tabela.innerHTML = `
                <tr>
                    <td colspan="9" class="text-center">Nenhum produto encontrado.</td>
                </tr>
                `;
                return;
            }

            
            document.getElementById("id").textContent = itens.id;
            let data = this.formatarDataLocal(itens.data);
            document.getElementById("data").textContent = data;
            document.getElementById("usuario").textContent = itens.usuario;
            document.getElementById("observacao").textContent = itens.observacao;

            itens.itens.forEach((it) => {

                const tr = document.createElement("tr");
                tr.innerHTML = `
                <td>${it.produto || "-"}</td>
                <td>${it.tipo || "-"}</td>
                <td>${it.unidade || "-"}</td>
                <td>${it.quantidade || "-"}</td>
                `;
                tabela.appendChild(tr);
                });

    
            }catch (error) {
                console.error("Erro ao carregar detalhes da saída:", error);
                UIComponents.ModalErro.mostrar(
                "Não foi possível carregar os detalhes da saída."
                );
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

    async aplicarFiltro(event) {
      event.preventDefault();
      try {
        UIComponents.Loading.mostrar("Aplicando filtro...");
        const dataInicio = document.getElementById("dataInicio").value;
        const dataFim = document.getElementById("dataFim").value;

        const inicio = new Date(dataInicio);
        const fim = new Date(dataFim);

        if (inicio > fim) {
          UIComponents.ModalErro.mostrar("A data de início não pode ser maior que a data de fim.");
          return;
        }

        var Filtrados = await this.service.buscarPorPeriodo(dataInicio,dataFim);

        this.renderizarTabelaRegistros(Filtrados);
        UIComponents.Loading.esconder();
      } catch (error) {
        console.error("Erro ao aplicar filtro:", error);
        UIComponents.ModalErro.mostrar("Não foi possível aplicar o filtro.");
        UIComponents.Loading.esconder();
      }
  }

    renderizarTabelaRegistros(registros){
        const tabela = document.getElementById("tabela-registro");
        if (!tabela) return;

        tabela.innerHTML = "";

        if (registros.length === 0) {
            tabela.innerHTML = `
            <tr>
                <td colspan="9" class="text-center">Nenhum Registro encontrado.</td>
            </tr>
            `;
            return;
        }

        registros.forEach((reg) => {
            let data = "-";
            if (reg.data) {
                data = this.formatarDataLocal(reg.data);
            }

            const tr = document.createElement("tr");
            tr.innerHTML = `
            <td>${reg.id || "-"}</td>
            <td>${reg.usuario || "-"}</td>
            <td>${data || "-"}</td>
            <td>${reg.oboservacao || "-"}</td>
            <td>
                <a href="detalhesSaida.html?id=${
                    reg.id
                    }" class="btn btn-sm btn-primary">
                        <i class="bi bi-eye me-1"></i> Detalhes
                </a>
            </td>`;
            tabela.appendChild(tr);
        });
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
            option.value = produto.produto.produto.idproduto;
            const tipoProduto = produto.produto.tipoProduto?.descricao || "";
            const unidade = produto.produto.unidadeMedida?.sigla || "";
            option.textContent = `${produto.produto.produto.nome} (${tipoProduto}) - ${unidade}`;
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
      const estoque = await this.service.obterEstoque(produtoId);
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
        quantidadeAtualInput.value = "0";
      }
      UIComponents.ModalErro.mostrar(
        "Não foi possível consultar o estoque atual do produto."
      );
    }
  }


    async inicializarFormulario() {
    try {
        UIComponents.Validacao.limparErros("formSaida");

        const form = document.getElementById("formSaida");
        if (form) {
            form.addEventListener("submit", (e) => this.efetuarSaida(e));
        }

        const btnAdicionarItem = document.getElementById("btnAdicionarItem");
        if (btnAdicionarItem) {
            btnAdicionarItem.addEventListener("click", () => {
            document.getElementById("quantidadeAtual").textContent = 0;
            this.prepararModal();
            UIComponents.Loading.esconder();
            UIComponents.ModalHelper.abrirModal("modalItem");
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

        const novaQuantidadeInput = document.getElementById("quantidade");
        if (novaQuantidadeInput) {
            novaQuantidadeInput.setAttribute("data-mask", "decimal");
            UIComponents.InputMasks.inicializar();
        }

        UIComponents.Loading.mostrar("Carregando produtos...");
        await this.carregarProdutos();
        UIComponents.Loading.esconder();
    } catch (error) {
        console.error("Erro ao inicializar formulário:", error);
        UIComponents.ModalErro.mostrar(
            "Não foi possível carregar o formulário. " +
            (error.message || "Erro desconhecido.")
        );
        UIComponents.Loading.esconder();
    }
  }

  validarDataNaoFutura(elemento) {
      const data = new Date(elemento);
      const hoje = new Date();
      
      data.setHours(0, 0, 0, 0);
      hoje.setHours(0, 0, 0, 0);

      if (data > hoje) {

        UIComponents.Validacao.mostrarErro("dataUso","A data não pode ser futura");
        return true;
      }
      else
        return false;
    }

  async efetuarSaida(event) {
      event.preventDefault();
      try {
          UIComponents.Validacao.limparErros("formSaida");
          const observacao = document.getElementById("observacao")?.value || "";
          const data = document.getElementById("dataUso")?.value || "";

          console.log("data: ",data);
          if(!data)
          {
                  console.log("data: ",data);
                  UIComponents.Validacao.mostrarErro(
                  "dataUso",
                      "É necessario informar a data de uso"
                  );
                  return;
          }

          if(this.validarDataNaoFutura(data)){
            return;
          }
          
          const tbody = document.getElementById("itensSaida");
          const linhas = tbody.querySelectorAll("tr");

          if (linhas.length === 1 && linhas[0].id === "nenhumItem") {
                  UIComponents.Validacao.mostrarErro(
                  "itensSaida",
                  "É adicionar ao menos um item"
                  );
                return;
          }
          
          const entradaProdutos = new EntradaProdutoModel({
              usuario_pessoa_id: 1,
              observacao: observacao,
              data: data,
              itens: this.itensSaida
          });

          
          
          UIComponents.ModalConfirmacao.mostrar(
              "Confirmar saída de produtos",
              "Deseja confirmar a saída de produtos?",
              async () => {
              try {
                  UIComponents.Loading.mostrar("Processando saída de produtos...");
                  const resultado = await this.service.cadastrar(
                  entradaProdutos
                  );
                  console.log("Resultado da saída de produtos:", resultado);
                  UIComponents.Toast.sucesso(
                  "Saída de produtos realizada com sucesso!"
                  );
                  window.location.href =
                  "listarregistros.html?message=Saída de produtos realizado com sucesso!";
              } catch (error) {
                  console.error("Erro ao efetuar saída:", error);
                  UIComponents.ModalErro.mostrar(
                  "Não foi possível efetuar a saída de produtos: " +
                      (error.message || "")
                  );
                  UIComponents.Loading.esconder();
              }
              }
          );
      } catch (error) {
        console.error("Erro ao processar saída:", error);
        UIComponents.ModalErro.mostrar(
          "Erro ao processar a saída de produtos: " + (error.message || "")
        );
      }
    }

    atualizarTabelaItens() {
        const tabela = document.getElementById("itensSaida");
        if (!tabela) return;
        tabela.innerHTML = "";
        if (this.itensSaida.length === 0) {
          tabela.innerHTML = ` 
            <tr id="nenhumItem"> 
              <td colspan="5" class="text-center">Nenhum item adicionado</td> 
            </tr>
          `;
          return;
        }
        this.itensSaida.forEach((item, index) => {
          const tr = document.createElement("tr");
         
          tr.innerHTML = ` 
            <td>${item.nome}</td> 
            <td>${item.quantidade.toFixed(2).replace(".", ",")}</td> 
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

    adicionarItem() {
        UIComponents.Validacao.limparErros("formSaida");
        const produtoId = document.getElementById("selectProduto")?.value;
        const novaQuantidade = document.getElementById("quantidade")?.value;
        var estoque = document.getElementById("quantidadeAtual")?.value;
        estoque = parseFloat(estoque.replace(',', '.'));
        let adicionado = false;
        let valido = true;
        if (!produtoId) {
          UIComponents.Validacao.mostrarErro(
            "selectProduto"
          );
          valido = false;
        }
        if (!novaQuantidade) {
          UIComponents.Validacao.mostrarErro(
            "quantidade"
          );
          valido = false;

        } 
        else 
        {
          let valor = Number(novaQuantidade);
          if(!Number.isInteger(valor)){
            UIComponents.Validacao.mostrarErro(
              "quantidade",
              "Quantidade precisa ser um valor inteiro"
            );
            valido = false;
          }
          var novaQtdNum = parseFloat(novaQuantidade.replace(",", "."));
          if (isNaN(novaQtdNum)) {
            UIComponents.Validacao.mostrarErro(
              "quantidade",
              "Informe um valor numérico válido"
            );
            valido = false;
          } 
          else {
                if (novaQtdNum <= 0) {
                    UIComponents.Validacao.mostrarErro(
                    "quantidade",
                    "A quantidade não pode ser zero ou negativa"
                    );
                    valido = false;
                }
                else {
                    if(novaQtdNum > estoque){
                        UIComponents.Validacao.mostrarErro(
                        "quantidade",
                        "A quantidade não pode ser maior que o total em estoque"
                        );
                        valido = false;
                    }
                }
            }
        }
        console.log(novaQtdNum);
        if (!valido) return;
        const produtoExistente = this.itensSaida.find(
          (item) => item.produtoId === parseInt(produtoId)
        );
        if (produtoExistente) {
            if((novaQtdNum+produtoExistente.quantidade) > estoque){
                UIComponents.Validacao.mostrarErro(
                "quantidade",
                "A quantidade não pode ser maior que o total em estoque"
                );
                valido = false;
                return;
            }
            else{
                produtoExistente.quantidade += novaQtdNum;
                console.log("quantidade: ",produtoExistente.quantidade);
                
                this.atualizarTabelaItens();
                adicionado = true;
            }
        }
        const produtoSelecionado = this.produtosCarregados.find(
            (p) => p.produto.produto.idproduto === parseInt(produtoId)
            );

        if (!produtoSelecionado) {
          UIComponents.ModalErro.mostrar("Produto não encontrado");
          return;
        }

        if(!adicionado){
            const novoItem = {
                produtoId: parseInt(produtoId),
                nome: produtoSelecionado.produto.produto.nome,
                quantidade: novaQtdNum
            };
            this.itensSaida.push(novoItem);
            this.atualizarTabelaItens();
        }
    
        const modalElement = document.getElementById("modalItem");
        let modalInstance = bootstrap.Modal.getInstance(modalElement);
        if (!modalInstance) {
          modalInstance = new bootstrap.Modal(modalElement);
        }
        modalInstance.hide();
        UIComponents.ModalHelper.fecharModal("modalItem");
        UIComponents.Toast.sucesso("Item adicionado com sucesso");
    }

    removerItem(index) {
        this.itensSaida.splice(index, 1);
        this.atualizarTabelaItens();
        UIComponents.Toast.sucesso("Item removido com sucesso");
      }

    prepararModal() {
        const selectProduto = document.getElementById("selectProduto");
        const quantidadeAtual = document.getElementById("quantidadeAtual");
        const novaQuantidade = document.getElementById("quantidade");
        if (selectProduto) selectProduto.value = "";
        if (quantidadeAtual) quantidadeAtual.value = "";
        if (novaQuantidade) novaQuantidade.value = "";
        UIComponents.Validacao.limparErros("formItem");
  }

  async excluir() {
      try {
        const id = document.getElementById("id").textContent;
        console.log("id: ", id);

        UIComponents.ModalConfirmacao.mostrar(
          "Confirmar exclusão",
          MensagensPadroes.CONFIRMACAO.EXCLUSAO,
          () => {
            this.service.excluir(id)
              .then((resposta) => {
                if (resposta.ok) {
                  // Salva mensagem de sucesso no localStorage
                  localStorage.setItem("mensagemSucesso", MensagensPadroes.SUCESSO.EXCLUSAO);

                  // Redireciona pra tela de listagem
                  window.location.href = "listarRegistros.html";
                } else {
                  UIComponents.Toast.erro(
                    MensagensPadroes.ERRO.EXCLUSAO + " (Erro na resposta)"
                  );
                }
              })
              .catch((error) => {
                console.error("Erro ao excluir:", error);
                UIComponents.Toast.erro(
                  MensagensPadroes.ERRO.EXCLUSAO +
                    (error.message ? `: ${error.message}` : "")
                );
              });
          }
        );
      } catch (error) {
        console.error("Erro ao excluir registro:", error);
        UIComponents.ModalErro.mostrar(
          "Erro ao excluir registro: " + (error.message || "")
        );
      }
  }


}

const saidaProdutoController = new SaidaProdutoController();
window.saidaProdutoController = saidaProdutoController;

export { saidaProdutoController };
export default SaidaProdutoController;