import EntradaProdutoService from "../services/EntradaProdutoService.js";
import EntradaProdutoModel from "../models/EntradaProdutoModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class EntradaProdutoController{
    constructor(){
        this.service = new EntradaProdutoService();
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

    async filtrarProduto(termo) {
        try {
            UIComponents.Loading.mostrar("Carregando produto...");
            
            var produtos;

            if(termo == '')
                produtos = await this.service.listarTodos();
            else
                produtos = await this.service.listarPorNome(termo);  

            this.renderizarTabela(produtos);

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

    renderizarTabela(entprodutos) {
        const tabela = document.getElementById("tabela-produto");
        if (!tabela) return;

        tabela.innerHTML = "";

        if (entprodutos.length === 0) {
        tabela.innerHTML = `
        <tr>
            <td colspan="9" class="text-center">Nenhum produto encontrado.</td>
        </tr>
        `;
        return;
        }

        entprodutos.forEach((prod) => {

        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${prod.produto.nome || "-"}</td>
        <td>${prod.produto.fabricante || "-"}</td>
        <td>${prod.produto.preco || "-"}</td>
        <td>${prod.estoque.quantidade || "-"}</td>
        <td>
            <a href="registroEntrada.html?id=${
            prod.produto.idproduto
            }" class="btn btn-sm btn-primary">Registrar</a>
        </td>
        `;
        tabela.appendChild(tr);
        });
    }

    formatarDataLocal(dataISO) {
        if (!dataISO) return "-";
        
        const partes = dataISO.split("-");
        if (partes.length !== 3) return "-";

        const [ano, mes, dia] = partes;
        return `${dia}/${mes}/${ano}`; // formato pt-BR
    }

    renderizarTabelaRegistros(registros){
        const tabela = document.getElementById("tabela-registro");
        if (!tabela) return;

        tabela.innerHTML = "";

        if (registros.length === 0) {
            tabela.innerHTML = `
            <tr>
                <td colspan="9" class="text-center">Nenhum produto encontrado.</td>
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
            <td>${reg.usuario || "-"}</td>
            <td>${reg.produto || "-"}</td>
            <td>${data || "-"}</td>
            <td>${reg.quantidade || "-"}</td>
            <td>${reg.fornecedor || "-"}</td>
            `;
            tabela.appendChild(tr);
        });
    }

    obterDadosFormulario(id) {
        const quantidadeElement = document.getElementById("quantidade");
        const fornecedorElement = document.getElementById("fornecedor");
        const observacaoElement = document.getElementById("observacao");

        // Verificação de campos obrigatórios
        if (!quantidadeElement.value.trim()) {
            throw new Error("Campos obrigatórios não preenchidos.");
        }

        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);

       var registro = new EntradaProdutoModel({
            idprod: id,
            idusu: 0,
            quantidade: quantidadeElement.value,
            fornecedor: fornecedorElement.value.trim(),
            observacao: observacaoElement.value,
            date: hoje
        });

        return registro;
    }

    async cadastrar(id) {
        try {
                console.log("IDProduto: "+id);
                const registro = this.obterDadosFormulario(id);

                const resultado = await this.service.cadastrar(registro);
                console.log("Resposta do backend:", resultado);

                UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.CADASTRO);

                // Redirecionar após 2 segundos
                setTimeout(() => {
                    window.location.href =
                    "listarprodutos.html?" +
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

}

const entradaProdutoController = new EntradaProdutoController();
window.entradaProdutoController = entradaProdutoController;

export { entradaProdutoController };
export default EntradaProdutoController;