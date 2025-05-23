import PessoaService from "../services/PessoaService.js";
import PessoaModel from "../models/PessoaModel.js";
import MensagensPadroes from "../utils/mensagensPadroes.js";
import UIComponents from "../components/uiComponents.js";

class PessoaController {

    constructor() {
        this.service = new PessoaService();
        this.pessoasOriginais = []; // Para filtros e busca
    }
    
    async inicializarListagem() {
        try {
            UIComponents.Loading.mostrar("Carregando adotantes...");
            const pessoas = await this.service.listarTodos();
            this.pessoasOriginais = pessoas; // Armazena dados originais
            this.renderizarTabela(pessoas);
            this.atualizarContador(pessoas.length);

            // Verificar mensagem na URL
            const urlParams = new URLSearchParams(window.location.search);
            const message = urlParams.get("message");
            if (message) {
                UIComponents.Toast.sucesso(decodeURIComponent(message));
                // Limpar a URL
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        } catch (error) {
            console.error("Erro ao carregar adotantes:", error);
            UIComponents.ModalErro.mostrar(MensagensPadroes.ERRO.CARREGAMENTO);
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    async inicializarFormulario() {
        try {
            // Verificar se é edição
            const urlParams = new URLSearchParams(window.location.search);
            const cpf = urlParams.get("cpf");
            
            if (cpf) {
                await this.carregarDadosEdicao(cpf);
            }

            // Configurar eventos do formulário
            this.configurarEventosFormulario();
        } catch (error) {
            console.error("Erro ao inicializar formulário:", error);
            UIComponents.Toast.erro("Erro ao carregar dados do formulário");
        }
    }

    async carregarDadosEdicao(cpf) {
        try {
            UIComponents.Loading.mostrar("Carregando dados...");
            const pessoa = await this.service.buscarPorCpf(cpf);
            
            if (pessoa) {
                this.preencherFormulario(pessoa);
                // Alterar título da página
                document.querySelector('h1').textContent = 'Editar Adotante';
            }
        } catch (error) {
            console.error("Erro ao carregar dados:", error);
            UIComponents.Toast.erro("Erro ao carregar dados do adotante");
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    preencherFormulario(pessoa) {
        document.getElementById('cpf').value = pessoa.cpf || '';
        document.getElementById('nome').value = pessoa.nome || '';
        document.getElementById('telefone').value = pessoa.telefone || '';
        document.getElementById('email').value = pessoa.email || '';
        document.getElementById('endereco').value = pessoa.endereco || '';
        
        // CPF não deve ser editável em modo de edição
        document.getElementById('cpf').readOnly = true;
    }

    configurarEventosFormulario() {
        const form = document.getElementById('formAdotante');
        if (form) {
            form.addEventListener('submit', (e) => this.salvarPessoa(e));
        }

        // Máscara para CPF
        const cpfInput = document.getElementById('cpf');
        if (cpfInput) {
            cpfInput.addEventListener('input', this.aplicarMascaraCPF);
        }

        // Máscara para telefone
        const telefoneInput = document.getElementById('telefone');
        if (telefoneInput) {
            telefoneInput.addEventListener('input', this.aplicarMascaraTelefone);
        }
    }

    async salvarPessoa(event) {
        event.preventDefault();
        
        try {
            const formData = this.obterDadosFormulario();
            const pessoa = new PessoaModel(
                null, // ID será definido pelo backend
                formData.nome,
                formData.cpf,
                formData.email,
                formData.telefone,
                formData.endereco
            );

            // Validação
            const validacao = pessoa.validar();
            if (!validacao.valido) {
                this.exibirErrosValidacao(validacao.erros);
                return;
            }

            UIComponents.Loading.mostrar("Salvando...");

            // Verificar se é edição ou novo cadastro
            const urlParams = new URLSearchParams(window.location.search);
            const cpfEdicao = urlParams.get("cpf");

            let resultado;
            if (cpfEdicao) {
                resultado = await this.service.atualizar(pessoa);
                UIComponents.Toast.sucesso("Adotante atualizado com sucesso!");
            } else {
                resultado = await this.service.cadastrar(pessoa);
                UIComponents.Toast.sucesso("Adotante cadastrado com sucesso!");
            }

            // Redirecionar para listagem
            setTimeout(() => {
                window.location.href = 'listarAdotante.html?message=' + 
                    encodeURIComponent(cpfEdicao ? 'Adotante atualizado com sucesso!' : 'Adotante cadastrado com sucesso!');
            }, 1500);

        } catch (error) {
            console.error("Erro ao salvar:", error);
            UIComponents.Toast.erro(error.message || "Erro ao salvar adotante");
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    obterDadosFormulario() {
        return {
            cpf: document.getElementById('cpf').value.trim(),
            nome: document.getElementById('nome').value.trim(),
            telefone: document.getElementById('telefone').value.trim(),
            email: document.getElementById('email').value.trim(),
            endereco: document.getElementById('endereco').value.trim()
        };
    }

    exibirErrosValidacao(erros) {
        // Limpar erros anteriores
        document.querySelectorAll('.input-error').forEach(el => el.textContent = '');

        erros.forEach(erro => {
            if (erro.includes('Nome')) {
                document.getElementById('nomeError').textContent = erro;
            } else if (erro.includes('CPF')) {
                document.getElementById('cpfError').textContent = erro;
            } else if (erro.includes('E-mail')) {
                document.getElementById('emailError').textContent = erro;
            }
        });

        UIComponents.Toast.erro("Por favor, corrija os erros no formulário");
    }

    async renderizarTabela(pessoas) {
        const tabela = document.getElementById("tabelaAdotantes");
        
        if (!tabela) {
            console.error("Elemento tabela não encontrado");
            return;
        }

        tabela.innerHTML = ""; // Limpa a tabela antes de renderizar

        if (!pessoas || pessoas.length === 0) {
            const linhaSemDados = document.createElement("tr");
            linhaSemDados.className = "no-data";
            linhaSemDados.innerHTML = `<td colspan="6" class="text-center py-3">Nenhum adotante encontrado</td>`;
            tabela.appendChild(linhaSemDados);
            return;
        }

        pessoas.forEach(pessoa => {
            const linha = document.createElement("tr");
            linha.innerHTML = `
                <td>${pessoa.cpf || 'N/A'}</td>
                <td>${pessoa.nome || 'N/A'}</td>
                <td>${pessoa.telefone || 'N/A'}</td>
                <td>${pessoa.email || 'N/A'}</td>
                <td>${pessoa.endereco || 'N/A'}</td>
                <td>
                    <div class="actions">
                        <button class="btn-icon btn-view" title="Visualizar" onclick="pessoaController.visualizar('${pessoa.cpf}')">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn-icon btn-edit" title="Editar" onclick="pessoaController.chamaAtualizazr('${pessoa}')">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn-icon btn-delete" title="Excluir" onclick="pessoaController.confirmarExclusao('${pessoa.cpf}', '${pessoa.nome}')">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            `;
            tabela.appendChild(linha);
        });
    }

    async visualizar(cpf) {
        try {
            UIComponents.Loading.mostrar("Carregando dados...");
            const pessoa = await this.service.buscarPorCPF(cpf);;
            
            if (pessoa) {
                this.mostrarModalDetalhes(pessoa);
            } else {
                UIComponents.Toast.erro("Adotante não encontrado");
            }
        } catch (error) {
            console.error("Erro ao visualizar:", error);
            UIComponents.Toast.erro("Erro ao carregar dados do adotante");
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    mostrarModalDetalhes(pessoa) {
        const modal = document.getElementById('modalDetalhes');
        if (modal) {
            // Preencher dados do modal
            document.getElementById('detalheCpf').textContent = pessoa.cpf || 'N/A';
            document.getElementById('detalheNome').textContent = pessoa.nome || 'N/A';
            document.getElementById('detalheTelefone').textContent = pessoa.telefone || 'N/A';
            document.getElementById('detalheEmail').textContent = pessoa.email || 'N/A';
            document.getElementById('detalheEndereco').textContent = pessoa.endereco || 'N/A';
            
            // Configurar botão de editar
            const btnEditar = document.getElementById('btnEditarAdotante');
            if (btnEditar) {
                btnEditar.onclick = () => this.editar(pessoa.cpf);
            }

            // Mostrar modal (assumindo Bootstrap)
            const modalInstance = new bootstrap.Modal(modal);
            modalInstance.show();
        }
    }

    editar(cpf) {
        window.location.href = `cadastrarAdotante.html?cpf=${encodeURIComponent(cpf)}`;
    }

    chamaAtualizar(){
    }

    async atualizar() {
        try {
            const pessoa = this.obterDadosAtualizacao();
            
            console.log("Pessoa para atualização:", pessoa);
            
            UIComponents.Loading.mostrar("Atualizando adotante...");
            console.log(
                "Enviando para o backend (atualização):",
                JSON.stringify(pessoa)
            );

            const resultado = await this.service.atualizar(pessoa);
            console.log("Resposta do backend (atualização):", resultado);

            UIComponents.Toast.sucesso(MensagensPadroes.SUCESSO.ATUALIZACAO);

            // Redirecionar após 2 segundos
            setTimeout(() => {
                window.location.href =
                    "listarAdotante.html?message=" +
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

    mostrarModalEdicao(pessoa) {
        const modalHTML = `
            <div class="modal fade" id="modalEdicao" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Editar Adotante</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="formEdicaoModal">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="cpfEdicao" class="form-label">CPF</label>
                                        <input type="text" class="form-control" id="cpfEdicao" readonly value="${pessoa.cpf}">
                                    </div>
                                    <div class="col-md-6">
                                        <label for="nomeEdicao" class="form-label">Nome Completo</label>
                                        <input type="text" class="form-control" id="nomeEdicao" required value="${pessoa.nome}">
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="telefoneEdicao" class="form-label">Telefone</label>
                                        <input type="tel" class="form-control" id="telefoneEdicao" required value="${pessoa.telefone}">
                                    </div>
                                    <div class="col-md-6">
                                        <label for="emailEdicao" class="form-label">E-mail</label>
                                        <input type="email" class="form-control" id="emailEdicao" required value="${pessoa.email}">
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label for="enderecoEdicao" class="form-label">Endereço Completo</label>
                                    <input type="text" class="form-control" id="enderecoEdicao" required value="${pessoa.endereco}">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="button" class="btn btn-primary" onclick="pessoaController.salvarEdicaoModal()">Salvar Alterações</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Remover modal existente se houver
        const modalExistente = document.getElementById('modalEdicao');
        if (modalExistente) {
            modalExistente.remove();
        }

        // Adicionar novo modal ao body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Mostrar modal
        const modal = document.getElementById('modalEdicao');
        const modalInstance = new bootstrap.Modal(modal);
        modalInstance.show();

        // Aplicar máscaras
        const telefoneInput = document.getElementById('telefoneEdicao');
        if (telefoneInput) {
            telefoneInput.addEventListener('input', this.aplicarMascaraTelefone);
        }
    }

    confirmarExclusao(cpf, nome) {
        if (!cpf) {
            UIComponents.Toast.erro("CPF não informado");
            return;
        }

        const modal = document.getElementById('confirmarExclusao');
        if (modal) {
            // Preencher nome no modal
            const nomeElement = document.getElementById('nomeAdotanteExclusao');
            if (nomeElement) {
                nomeElement.textContent = nome;
            }

            // Configurar botão de confirmação
            const btnConfirmar = document.getElementById('btnConfirmarExclusao');
            if (btnConfirmar) {
                btnConfirmar.onclick = () => this.excluir(cpf);
            }

            // Mostrar modal
            const modalInstance = new bootstrap.Modal(modal);
            modalInstance.show();
        } else {
            // Fallback para confirm nativo
            if (confirm(`Tem certeza que deseja excluir o adotante ${nome}?`)) {
                this.excluir(cpf);
            }
        }
    }

    async excluir(cpf) {
        try {
            UIComponents.Loading.mostrar("Excluindo...");
            await this.service.excluir(cpf);
            
            UIComponents.Toast.sucesso("Adotante excluído com sucesso!");
            
            // Fechar modal se estiver aberto
            const modal = document.getElementById('confirmarExclusao');
            if (modal) {
                const modalInstance = bootstrap.Modal.getInstance(modal);
                if (modalInstance) {
                    modalInstance.hide();
                }
            }

            // Recarregar listagem
            await this.inicializarListagem();
            
        } catch (error) {
            console.error("Erro ao excluir:", error);
            UIComponents.Toast.erro(error.message || "Erro ao excluir adotante");
        } finally {
            UIComponents.Loading.esconder();
        }
    }

    filtrarPessoas(termo) {
        if (!this.pessoasOriginais) return;

        const termoLower = termo.toLowerCase().trim();
        
        if (!termoLower) {
            this.renderizarTabela(this.pessoasOriginais);
            this.atualizarContador(this.pessoasOriginais.length);
            return;
        }

        const pessoasFiltradas = this.pessoasOriginais.filter(pessoa => {
            return (
                pessoa.nome?.toLowerCase().includes(termoLower) ||
                pessoa.cpf?.toLowerCase().includes(termoLower) ||
                pessoa.email?.toLowerCase().includes(termoLower) ||
                pessoa.telefone?.toLowerCase().includes(termoLower) ||
                pessoa.endereco?.toLowerCase().includes(termoLower)
            );
        });

        this.renderizarTabela(pessoasFiltradas);
        this.atualizarContador(pessoasFiltradas.length);
    }

    ordenarPessoas(criterio) {
        if (!this.pessoasOriginais) return;

        let pessoasOrdenadas = [...this.pessoasOriginais];

        switch (criterio) {
            case 'alfabetica':
                pessoasOrdenadas.sort((a, b) => (a.nome || '').localeCompare(b.nome || ''));
                break;
            case 'recentes':
                // Assumindo que IDs maiores são mais recentes
                pessoasOrdenadas.sort((a, b) => (b.idpessoa || 0) - (a.idpessoa || 0));
                break;
            default:
                // 'todos' - ordem original
                break;
        }

        this.renderizarTabela(pessoasOrdenadas);
        this.atualizarContador(pessoasOrdenadas.length);
    }

    atualizarContador(total) {
        const contador = document.getElementById('totalRegistros');
        if (contador) {
            contador.textContent = total;
        }
    }

    // Funções utilitárias para máscaras
    aplicarMascaraCPF(event) {
        let valor = event.target.value.replace(/\D/g, '');
        valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
        valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
        valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        event.target.value = valor;
    }

    aplicarMascaraTelefone(event) {
        let valor = event.target.value.replace(/\D/g, '');
        if (valor.length <= 10) {
            valor = valor.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
        } else {
            valor = valor.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3');
        }
        event.target.value = valor;
    }
}

// Instância global
const pessoaController = new PessoaController();

// Exportações
window.pessoaController = pessoaController;
export { pessoaController };
export default PessoaController;