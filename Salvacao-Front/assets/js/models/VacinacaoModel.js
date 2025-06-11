class VacinacaoModel {
    constructor(
        idVacinacao = null,
        idVacina = null,
        idAnimal = null,
        dataVacinacao = "",
        localVacinacao = "",
        loteVacina = null,
        dataValidadeVacina = null,
        laboratorioVacina = null
    ) {
        this.idVacinacao = idVacinacao;
        this.idVacina = idVacina;
        this.idAnimal = idAnimal;
        this.dataVacinacao = dataVacinacao;
        this.localVacinacao = localVacinacao;
        this.loteVacina = loteVacina;
        this.dataValidadeVacina = dataValidadeVacina;
        this.laboratorioVacina = laboratorioVacina;
    }

    toJSON() {
        return {
            id_vacinacao: this.idVacinacao === "0" || this.idVacinacao === null ? null : parseInt(this.idVacinacao),
            id_vacina: this.idVacina ? parseInt(this.idVacina) : null,
            id_animal: this.idAnimal ? parseInt(this.idAnimal) : null,
            data_vacinacao: this.dataVacinacao,
            local_vacinacao: this.localVacinacao,
            lote_vacina: this.loteVacina || null,
            data_validade_vacina: this.dataValidadeVacina || null,
            laboratorio_vacina: this.laboratorioVacina || null
        };
    }

    static fromForm(formData) {
        return new VacinacaoModel(
            formData.id_vacinacao,
            formData.id_vacina,
            formData.id_animal,
            formData.data_vacinacao,
            formData.local_vacinacao,
            formData.lote_vacina,
            formData.data_validade_vacina,
            formData.laboratorio_vacina
        );
    }

    validate() {
        const errors = {};
        if (!this.idVacina) errors.idVacina = "Vacina é obrigatória.";
        if (!this.idAnimal) errors.idAnimal = "Animal é obrigatório.";
        if (!this.dataVacinacao) errors.dataVacinacao = "Data da vacinação é obrigatória.";
        if (!this.localVacinacao || this.localVacinacao.trim().length < 3) {
            errors.localVacinacao = "Local da vacinação é obrigatório e deve ter ao menos 3 caracteres.";
        }
        if (this.dataValidadeVacina && this.dataVacinacao && new Date(this.dataValidadeVacina) < new Date(this.dataVacinacao)) {
            errors.dataValidadeVacina = "Data de validade não pode ser anterior à data da vacinação.";
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    }
}

export default VacinacaoModel;