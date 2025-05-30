class AdocaoService {
    constructor(baseUrl = "http://localhost:8080") {
        this.baseUrl = baseUrl;
        this.endpoint = "/adocao";
    }

    async listarTodos() {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/listar`, {
                method: "GET"
            });
            
            if (!response.ok) {
                if (response.status === 404) {
                    return []; // Retorna array vazio para renderizarTabela funcionar
                }
                throw new Error(`Erro ${response.status}: ${await response.text()}`);
            }
            
            const data = await response.json();
            console.log("Dados recebidos da API listarTodos:", data);
            return data;
        } catch (error) {
            console.error("Erro ao listar adoções:", error);
            throw error;
        }
    }

    // Método simples para listar usando fetch direto
    listar() {
        const tabela = document.getElementById("tabela-adocoes");
        
        fetch(`${this.baseUrl}${this.endpoint}/listar`)
            .then(res => res.json())
            .then(lista => {
                if(!Array.isArray(lista))
                    throw new Error('A resposta da API não é uma lista');

                if(lista.length === 0) {
                    tabela.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center">Nenhuma adoção encontrada.</td>
                    </tr>`;
                    return;
                }

                tabela.innerHTML = ""; // Limpa a tabela
                
                lista.forEach(adocao => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${adocao.idAdocao || '-'}</td>
                        <td>Animal ID: ${adocao.idAnimal || '-'}</td>
                        <td>Adotante ID: ${adocao.idAdotante || '-'}</td>
                        <td>${this.formatarData(adocao.dataAdocao)}</td>
                        <td>${this.getBadgeStatus(adocao.statusAcompanhamento)}</td>
                        <td>${adocao.dataAcompanhamento ? this.formatarData(adocao.dataAcompanhamento) : '-'}</td>
                        <td>${adocao.obs ? (adocao.obs.length > 50 ? adocao.obs.substring(0, 50) + '...' : adocao.obs) : '-'}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary me-1" onclick="editarAdocao(${adocao.idAdocao})">Editar</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="adocaoController.confirmarExclusao(${adocao.idAdocao})">Excluir</button>
                        </td>
                    `;
                    tabela.appendChild(tr);
                });
            })
            .catch(error => {
                console.error('Erro ao carregar adoções:', error);
                tabela.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">Erro ao carregar dados.</td>
                </tr>`;
            });
    }

    // Métodos auxiliares para formatação
    formatarData(data) {
        if (!data) return '';
        try {
            const date = new Date(data);
            return date.toLocaleDateString('pt-BR');
        } catch (error) {
            return data;
        }
    }

    getBadgeStatus(status) {
        const statusMap = {
            'Pendente': 'bg-warning',
            'Em acompanhamento': 'bg-info',
            'Aprovado': 'bg-success',
            'Rejeitado': 'bg-danger',
            'Cancelado': 'bg-secondary'
        };

        const badgeClass = statusMap[status] || 'bg-secondary';
        return `<span class="badge ${badgeClass}">${status || 'N/A'}</span>`;
    }

    async buscarPorId(id) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
                method: "GET"
            });
            
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: ${await response.text()}`);
            }
            
            const data = await response.json();
            console.log("Dados recebidos da API buscarPorId:", data);
            return data;
        } catch (error) {
            console.error("Erro ao buscar adoção:", error);
            throw error;
        }
    }

    async cadastrar(adocao) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/cadastro`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(adocao)
            });
            
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: ${await response.text()}`);
            }
            
            const data = await response.json();
            console.log("Adoção cadastrada:", data);
            return data;
        } catch (error) {
            console.error("Erro ao cadastrar adoção:", error);
            throw error;
        }
    }

    async atualizar(id, adocao) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/alterar`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ ...adocao, idadocao: id })
            });
            
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: ${await response.text()}`);
            }
            
            const data = await response.text();
            console.log("Adoção atualizada:", data);
            return data;
        } catch (error) {
            console.error("Erro ao atualizar adoção:", error);
            throw error;
        }
    }

    async excluir(id) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoint}/${id}`, {
                method: "DELETE"
            });
            
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: ${await response.text()}`);
            }
            
            const data = await response.text();
            console.log("Adoção excluída:", data);
            return data;
        } catch (error) {
            console.error("Erro ao excluir adoção:", error);
            throw error;
        }
    }


}

export default AdocaoService;