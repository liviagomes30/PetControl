// Salvacao-Front/assets/js/models/VacinaModel.js
class VacinaModel {
    constructor(id_vacina = null, descricao_vacina = "") {
        this.id_vacina = id_vacina;
        this.descricao_vacina = descricao_vacina;
    }

    static fromJson(json) {
        return new VacinaModel(json.id_vacina, json.descricao_vacina);
    }

    toJSON() {
        // Este método prepara o objeto para ser enviado ao backend.
        // Se o backend espera 'id' e 'descricao', faremos o mapeamento aqui.
        // Por enquanto, vamos manter os nomes consistentes com o banco.
        return {
            id_vacina: this.id_vacina && this.id_vacina !== "0" ? parseInt(this.id_vacina) : null,
            descricao_vacina: this.descricao_vacina,
        };
    }

    validate() {
        const errors = {};
        let isValid = true;

        if (!this.descricao_vacina || this.descricao_vacina.trim() === "") {
            errors.descricao_vacina = "A descrição da vacina é obrigatória.";
            isValid = false;
        } else if (this.descricao_vacina.trim().length < 2) {
            errors.descricao_vacina = "A descrição deve ter pelo menos 2 caracteres.";
            isValid = false;
        } else if (this.descricao_vacina.trim().length > 100) {
            errors.descricao_vacina = "A descrição não pode ter mais de 100 caracteres.";
            isValid = false;
        }

        return {
            isValid,
            errors,
        };
    }
}

export default VacinaModel;