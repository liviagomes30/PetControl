class ReceituarioUIComponents {
    static Loading = {
        mostrar(mensagem = "Carregando...") {
            const overlay = document.getElementById('loadingOverlay');
            const messageEl = document.getElementById('loadingMessage');
            
            if (overlay) {
                if (messageEl) messageEl.textContent = mensagem;
                overlay.classList.add('show');
            }
        },

        esconder() {
            const overlay = document.getElementById('loadingOverlay');
            if (overlay) {
                overlay.classList.remove('show');
            }
        }
    };

    static ModalSucesso = {
        mostrar(mensagem) {
            const modal = document.getElementById('modalSucesso');
            const body = document.getElementById('modalSucessoBody');
            
            if (modal && body) {
                body.innerHTML = mensagem;
                const bootstrapModal = new bootstrap.Modal(modal);
                bootstrapModal.show();
            } else {
                alert('Sucesso: ' + mensagem);
            }
        }
    };

    static ModalErro = {
        mostrar(mensagem) {
            const modal = document.getElementById('modalErro');
            const body = document.getElementById('modalErroBody');
            
            if (modal && body) {
                body.innerHTML = mensagem;
                const bootstrapModal = new bootstrap.Modal(modal);
                bootstrapModal.show();
            } else {
                alert('Erro: ' + mensagem);
            }
        }
    };

    static ModalAviso = {
        mostrar(mensagem) {
            const modal = document.getElementById('modalAviso');
            const body = document.getElementById('modalAvisoBody');
            
            if (modal && body) {
                body.innerHTML = mensagem;
                const bootstrapModal = new bootstrap.Modal(modal);
                bootstrapModal.show();
            } else {
                alert('Aviso: ' + mensagem);
            }
        }
    };

    static ModalConfirmacao = {
        mostrar(titulo, mensagem, callback) {
            const modal = document.getElementById('modalConfirmacao');
            const titleEl = modal?.querySelector('.modal-title');
            const body = document.getElementById('modalConfirmacaoBody');
            const btnConfirmar = document.getElementById('btnConfirmarAcao');
            
            if (modal && body && btnConfirmar) {
                if (titleEl) titleEl.innerHTML = titulo;
                body.innerHTML = mensagem;
                
                const newBtn = btnConfirmar.cloneNode(true);
                btnConfirmar.parentNode.replaceChild(newBtn, btnConfirmar);
                
                newBtn.addEventListener('click', () => {
                    const bootstrapModal = bootstrap.Modal.getInstance(modal);
                    if (bootstrapModal) bootstrapModal.hide();
                    if (callback) callback();
                });
                
                const bootstrapModal = new bootstrap.Modal(modal);
                bootstrapModal.show();
            } else {
                if (confirm(mensagem)) {
                    if (callback) callback();
                }
            }
        }
    };

    static Toast = {
        mostrar(mensagem, tipo = 'info', duracao = 3000) {
            let container = document.getElementById('toast-container');
            if (!container) {
                container = document.createElement('div');
                container.id = 'toast-container';
                container.className = 'toast-container position-fixed top-0 end-0 p-3';
                container.style.zIndex = '1056';
                document.body.appendChild(container);
            }

            let bgClass, iconClass;
            switch (tipo) {
                case 'success':
                    bgClass = 'bg-success';
                    iconClass = 'bi-check-circle';
                    break;
                case 'error':
                    bgClass = 'bg-danger';
                    iconClass = 'bi-exclamation-triangle';
                    break;
                case 'warning':
                    bgClass = 'bg-warning';
                    iconClass = 'bi-exclamation-triangle';
                    break;
                default:
                    bgClass = 'bg-info';
                    iconClass = 'bi-info-circle';
            }

            const toastEl = document.createElement('div');
            toastEl.className = `toast align-items-center text-white ${bgClass} border-0`;
            toastEl.setAttribute('role', 'alert');
            toastEl.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi ${iconClass} me-2"></i>${mensagem}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            `;

            container.appendChild(toastEl);

            const toast = new bootstrap.Toast(toastEl, {
                autohide: true,
                delay: duracao
            });
            toast.show();

            toastEl.addEventListener('hidden.bs.toast', () => {
                toastEl.remove();
            });
        }
    };

    static Formulario = {
        limparErros(form) {
            if (typeof form === 'string') {
                form = document.getElementById(form);
            }
            
            if (form) {
                const invalidElements = form.querySelectorAll('.is-invalid');
                invalidElements.forEach(el => el.classList.remove('is-invalid'));
                
                const errorDivs = form.querySelectorAll('.invalid-feedback');
                errorDivs.forEach(div => div.textContent = '');
            }
        },

        exibirErro(campo, mensagem) {
            const elemento = document.getElementById(campo);
            const errorDiv = document.getElementById(campo + '_error');
            
            if (elemento) {
                elemento.classList.add('is-invalid');
            }
            
            if (errorDiv) {
                errorDiv.textContent = mensagem;
            }
        },

        obterDados(form) {
            if (typeof form === 'string') {
                form = document.getElementById(form);
            }
            
            if (!form) return {};
            
            const formData = new FormData(form);
            const dados = {};
            
            for (const [key, value] of formData.entries()) {
                dados[key] = value;
            }
            
            return dados;
        },

        preencherDados(form, dados) {
            if (typeof form === 'string') {
                form = document.getElementById(form);
            }
            
            if (!form || !dados) return;
            
            Object.keys(dados).forEach(key => {
                const elemento = form.querySelector(`[name="${key}"]`);
                if (elemento) {
                    if (elemento.type === 'checkbox' || elemento.type === 'radio') {
                        elemento.checked = dados[key];
                    } else {
                        elemento.value = dados[key];
                    }
                }
            });
        }
    };

    static Tabela = {
        limpar(tabela) {
            if (typeof tabela === 'string') {
                tabela = document.getElementById(tabela);
            }
            
            if (tabela) {
                tabela.innerHTML = '';
            }
        },

        adicionarLinha(tabela, dados) {
            if (typeof tabela === 'string') {
                tabela = document.getElementById(tabela);
            }
            
            if (!tabela || !dados) return;
            
            const linha = document.createElement('tr');
            dados.forEach(celula => {
                const td = document.createElement('td');
                td.innerHTML = celula;
                linha.appendChild(td);
            });
            
            tabela.appendChild(linha);
        },

        exibirMensagemVazia(tabela, mensagem, colspan = 1) {
            if (typeof tabela === 'string') {
                tabela = document.getElementById(tabela);
            }
            
            if (!tabela) return;
            
            tabela.innerHTML = `
                <tr>
                    <td colspan="${colspan}" class="text-center py-4 text-muted">
                        <i class="bi bi-inbox"></i>
                        <p class="mb-0 mt-2">${mensagem}</p>
                    </td>
                </tr>
            `;
        }
    };

    static Utils = {
        formatarData(data, formato = 'pt-BR') {
            if (!data) return 'N/A';
            try {
                return new Date(data + 'T00:00:00').toLocaleDateString(formato);
            } catch (error) {
                return data;
            }
        },

        formatarDataHora(dataHora, formato = 'pt-BR') {
            if (!dataHora) return 'N/A';
            try {
                return new Date(dataHora).toLocaleString(formato);
            } catch (error) {
                return dataHora;
            }
        },

        formatarMoeda(valor, moeda = 'BRL') {
            if (valor === null || valor === undefined) return 'R$ 0,00';
            return new Intl.NumberFormat('pt-BR', {
                style: 'currency',
                currency: moeda
            }).format(valor);
        },

        debounce(func, wait) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        },

        escapeHtml(text) {
            const map = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            };
            return text.replace(/[&<>"']/g, function(m) { return map[m]; });
        }
    };
}

export default ReceituarioUIComponents; 