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
            idVacinacao: this.idVacinacao === "0" || this.idVacinacao === null ? null : parseInt(this.idVacinacao),
            idVacina: this.idVacina ? parseInt(this.idVacina) : null,
            idAnimal: this.idAnimal ? parseInt(this.idAnimal) : null,
            dataVacinacao: this.dataVacinacao,
            localVacinacao: this.localVacinacao,
            loteVacina: this.loteVacina || null,
            dataValidadeVacina: this.dataValidadeVacina || null,
            laboratorioVacina: this.laboratorioVacina || null
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