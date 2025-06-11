class AdocaoModel {
    constructor(
        idAdocao = null,
        idAdotante = '',
        idAnimal = '',
        dataAdocao = '',
        pessoaIdPessoa = null,
        obs = '',
        statusAcompanhamento = '',
        dataAcompanhamento = null
    ) {
        this.idAdocao = idAdocao;
        this.idAdotante = idAdotante;
        this.idAnimal = idAnimal;
        this.dataAdocao = dataAdocao;
        this.pessoaIdPessoa = pessoaIdPessoa;
        this.obs = obs;
        this.statusAcompanhamento = statusAcompanhamento;
        this.dataAcompanhamento = dataAcompanhamento;
    }

    // Método para criar uma instância de AdocaoModel a partir de um objeto JSON
    static fromJson(json) {
        return new AdocaoModel(
            json.idAdocao,
            json.idAdotante,
            json.idAnimal,
            json.dataAdocao,
            json.pessoaIdPessoa,
            json.obs,
            json.statusAcompanhamento,
            json.dataAcompanhamento
        );
    }

    // Método para converter vários objetos JSON em uma lista de AdocaoModel
    static fromJsonList(jsonList) {
        if (!Array.isArray(jsonList)) return [];
        return jsonList.map(json => AdocaoModel.fromJson(json));
    }

    // Método para validar os campos obrigatórios
    validar() {
        const erros = [];

        if (!this.idAdotante || this.idAdotante === '') {
            erros.push('ID do Adotante é obrigatório');
        }

        if (!this.idAnimal || this.idAnimal === '') {
            erros.push('ID do Animal é obrigatório');
        }

        if (!this.dataAdocao || this.dataAdocao.trim() === '') {
            erros.push('Data de adoção é obrigatória');
        } else if (!this.validarData(this.dataAdocao)) {
            erros.push('Data de adoção inválida');
        }

        if (!this.statusAcompanhamento || this.statusAcompanhamento.trim() === '') {
            erros.push('Status do acompanhamento é obrigatório');
        }

        // Validar data de acompanhamento se fornecida
        if (this.dataAcompanhamento && !this.validarData(this.dataAcompanhamento)) {
            erros.push('Data de acompanhamento inválida');
        }

        // Validar se data de acompanhamento não é anterior à data de adoção
        if (this.dataAcompanhamento && this.dataAdocao) {
            const dataAdocao = new Date(this.dataAdocao);
            const dataAcompanhamento = new Date(this.dataAcompanhamento);
            
            if (dataAcompanhamento < dataAdocao) {
                erros.push('Data de acompanhamento não pode ser anterior à data de adoção');
            }
        }

        // Validar tamanho das observações
        if (this.obs && this.obs.length > 500) {
            erros.push('Observações não podem exceder 500 caracteres');
        }

        return {
            valido: erros.length === 0,
            erros: erros
        };
    }

    // Método para validar formato de data
    validarData(data) {
        if (!data) return false;
        
        const regex = /^\d{4}-\d{2}-\d{2}$/;
        if (!regex.test(data)) return false;
        
        const date = new Date(data);
        return date instanceof Date && !isNaN(date.getTime());
    }

    // Método para formatar data para exibição (dd/mm/yyyy)
    formatarData(data) {
        if (!data) return '';
        
        try {
            const date = new Date(data);
            return date.toLocaleDateString('pt-BR');
        } catch (error) {
            return data;
        }
    }

    // Método para formatar datas do objeto para exibição
    formatarDatasParaExibicao() {
        return {
            ...this,
            dataAdocao: this.formatarData(this.dataAdocao),
            dataAcompanhamento: this.formatarData(this.dataAcompanhamento)
        };
    }

    // Método para obter badge HTML do status
    getBadgeStatus() {
        const statusMap = {
            'Pendente': 'bg-warning',
            'Em acompanhamento': 'bg-info',
            'Aprovado': 'bg-success',
            'Rejeitado': 'bg-danger',
            'Cancelado': 'bg-secondary'
        };

        const badgeClass = statusMap[this.statusAcompanhamento] || 'bg-secondary';
        return `<span class="badge ${badgeClass}">${this.statusAcompanhamento}</span>`;
    }

    // Método para criar uma cópia do objeto
    clone() {
        return new AdocaoModel(
            this.idAdocao,
            this.idAdotante,
            this.idAnimal,
            this.dataAdocao,
            this.pessoaIdPessoa,
            this.obs,
            this.statusAcompanhamento,
            this.dataAcompanhamento
        );
    }

    // Método para limpar os dados do objeto
    limpar() {
        this.idAdocao = null;
        this.idAdotante = '';
        this.idAnimal = '';
        this.dataAdocao = '';
        this.pessoaIdPessoa = null;
        this.obs = '';
        this.statusAcompanhamento = '';
        this.dataAcompanhamento = null;
    }

    // Método para converter para JSON (para envio ao backend)
    toJSON() {
        return {
            idAdocao: this.idAdocao,
            idAdotante: parseInt(this.idAdotante),
            idAnimal: parseInt(this.idAnimal),
            dataAdocao: this.dataAdocao,
            pessoaIdPessoa: this.pessoaIdPessoa ? parseInt(this.pessoaIdPessoa) : null,
            obs: this.obs || null,
            statusAcompanhamento: this.statusAcompanhamento,
            dataAcompanhamento: this.dataAcompanhamento || null
        };
    }
}

export default AdocaoModel;