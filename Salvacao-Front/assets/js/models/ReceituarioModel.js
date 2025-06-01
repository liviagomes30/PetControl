class ReceituarioModel {
    constructor(data = {}) {
        this.idreceita = data.idreceita || null;
        this.data = data.data || '';
        this.medico = data.medico || '';
        this.clinica = data.clinica || '';
        this.animal_idanimal = data.animal_idanimal || null;
        this.animalNome = data.animalNome || '';
        this.posologias = data.posologias || [];
    }

    validar() {
        const erros = {};

        if (!this.medico || this.medico.trim() === '') {
            erros.medico = 'Médico veterinário é obrigatório';
        } else if (this.medico.trim().length < 3) {
            erros.medico = 'Nome do médico deve ter pelo menos 3 caracteres';
        } else if (this.medico.trim().length > 255) {
            erros.medico = 'Nome do médico deve ter no máximo 255 caracteres';
        }

        if (!this.data || this.data.trim() === '') {
            erros.data = 'Data da receita é obrigatória';
        } else {
            const dataReceita = new Date(this.data);
            const hoje = new Date();
            hoje.setHours(0, 0, 0, 0);
            
            if (isNaN(dataReceita.getTime())) {
                erros.data = 'Data inválida';
            } else if (dataReceita > hoje) {
                erros.data = 'Data da receita não pode ser futura';
            }
        }

        if (!this.animal_idanimal) {
            erros.animal_idanimal = 'Animal é obrigatório';
        }

        if (this.clinica && this.clinica.trim() !== '') {
            if (this.clinica.trim().length < 3) {
                erros.clinica = 'Nome da clínica deve ter pelo menos 3 caracteres';
            } else if (this.clinica.trim().length > 255) {
                erros.clinica = 'Nome da clínica deve ter no máximo 255 caracteres';
            }
        }

        if (!this.posologias || this.posologias.length === 0) {
            erros.posologias = 'Pelo menos um medicamento deve ser prescrito';
        } else {
            const posologiasErros = [];
            
            this.posologias.forEach((posologia, index) => {
                const posErros = this.validarPosologia(posologia);
                if (Object.keys(posErros).length > 0) {
                    posologiasErros[index] = posErros;
                }
            });

            if (posologiasErros.length > 0) {
                erros.posologiasDetalhes = posologiasErros;
            }

            const medicamentosIds = this.posologias.map(p => p.medicamento_idproduto);
            const medicamentosDuplicados = medicamentosIds.filter((id, index) => 
                medicamentosIds.indexOf(id) !== index
            );
            
            if (medicamentosDuplicados.length > 0) {
                erros.medicamentosDuplicados = 'Não é possível prescrever o mesmo medicamento mais de uma vez';
            }
        }

        return {
            valido: Object.keys(erros).length === 0,
            erros
        };
    }

    validarPosologia(posologia) {
        const erros = {};

        if (!posologia.medicamento_idproduto) {
            erros.medicamento_idproduto = 'Medicamento é obrigatório';
        }

        if (!posologia.dose || posologia.dose.trim() === '') {
            erros.dose = 'Dose é obrigatória';
        } else if (posologia.dose.trim().length < 1) {
            erros.dose = 'Dose deve ser informada';
        } else if (posologia.dose.trim().length > 50) {
            erros.dose = 'Dose deve ter no máximo 50 caracteres';
        }

        if (!posologia.quantidadedias) {
            erros.quantidadedias = 'Quantidade de dias é obrigatória';
        } else {
            const qtdDias = parseInt(posologia.quantidadedias);
            if (isNaN(qtdDias) || qtdDias <= 0) {
                erros.quantidadedias = 'Quantidade de dias deve ser um número positivo';
            } else if (qtdDias > 365) {
                erros.quantidadedias = 'Quantidade de dias não pode ser superior a 365';
            }
        }

        if (!posologia.intervalohoras) {
            erros.intervalohoras = 'Intervalo em horas é obrigatório';
        } else {
            const intervalo = parseInt(posologia.intervalohoras);
            if (isNaN(intervalo) || intervalo <= 0) {
                erros.intervalohoras = 'Intervalo deve ser um número positivo';
            } else if (intervalo > 168) {
                erros.intervalohoras = 'Intervalo não pode ser superior a 168 horas (7 dias)';
            }
        }

        return erros;
    }

    toJSON() {
        return {
            data: this.data,
            medico: this.medico.trim(),
            clinica: this.clinica.trim(),
            animal_idanimal: parseInt(this.animal_idanimal),
            posologias: this.posologias.map(p => ({
                dose: p.dose.trim(),
                quantidadedias: parseInt(p.quantidadedias),
                intervalohoras: parseInt(p.intervalohoras),
                medicamento_idproduto: parseInt(p.medicamento_idproduto)
            }))
        };
    }

    static fromAPI(data) {
        return new ReceituarioModel({
            idreceita: data.idreceita,
            data: data.data,
            medico: data.medico,
            clinica: data.clinica,
            animal_idanimal: data.animal_idanimal,
            animalNome: data.animalNome,
            posologias: data.posologias || []
        });
    }

    adicionarPosologia(posologia = {}) {
        const novaPosologia = {
            dose: posologia.dose || '',
            quantidadedias: posologia.quantidadedias || '',
            intervalohoras: posologia.intervalohoras || '',
            medicamento_idproduto: posologia.medicamento_idproduto || null,
            medicamentoNome: posologia.medicamentoNome || '',
            medicamentoComposicao: posologia.medicamentoComposicao || ''
        };
        
        this.posologias.push(novaPosologia);
        return this.posologias.length - 1; // retorna o índice da nova posologia
    }

    removerPosologia(index) {
        if (index >= 0 && index < this.posologias.length) {
            this.posologias.splice(index, 1);
        }
    }

    atualizarPosologia(index, posologia) {
        if (index >= 0 && index < this.posologias.length) {
            this.posologias[index] = { ...this.posologias[index], ...posologia };
        }
    }

    limpar() {
        this.idreceita = null;
        this.data = '';
        this.medico = '';
        this.clinica = '';
        this.animal_idanimal = null;
        this.animalNome = '';
        this.posologias = [];
    }

    calcularTotalDoses(posologia) {
        if (!posologia.quantidadedias || !posologia.intervalohoras) {
            return 0;
        }
        
        const totalHoras = posologia.quantidadedias * 24;
        return Math.ceil(totalHoras / posologia.intervalohoras);
    }

    obterResumoTratamento() {
        return this.posologias.map(p => ({
            medicamento: p.medicamentoNome || 'Medicamento não identificado',
            dose: p.dose,
            frequencia: this.formatarFrequencia(p.intervalohoras),
            duracao: `${p.quantidadedias} dia(s)`,
            totalDoses: this.calcularTotalDoses(p)
        }));
    }

    formatarFrequencia(intervalohoras) {
        if (intervalohoras === 24) return 'Uma vez ao dia';
        if (intervalohoras === 12) return 'Duas vezes ao dia';
        if (intervalohoras === 8) return 'Três vezes ao dia';
        if (intervalohoras === 6) return 'Quatro vezes ao dia';
        if (intervalohoras < 24) return `A cada ${intervalohoras} horas`;
        return `A cada ${Math.round(intervalohoras / 24)} dia(s)`;
    }
}

export default ReceituarioModel; 