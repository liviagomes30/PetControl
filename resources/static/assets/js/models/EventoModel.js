class EventoModel {
  
  constructor(idEvento = null, descricao = '', data = '', local = '', animalId = null, foto = '') {
    this.idEvento = idEvento;
    this.descricao = descricao;
    this.data = data;
    this.local = local;
    this.animalId = animalId;
    this.foto = foto;
  }

  // Método para criar uma instância de EventoModel a partir de um objeto JSON
  static fromJson(json) {
    return new EventoModel(
      json.idEvento,
      json.descricao,
      json.data,
      json.local,
      json.animalId,
      json.foto
    );
  }

  // Método para converter vários objetos JSON em uma lista de EventoModel
  static fromJsonList(jsonList) {
    if (!Array.isArray(jsonList)) return [];
    return jsonList.map(json => EventoModel.fromJson(json));
  }

  // Método para validar os campos obrigatórios
  validar() {
    const erros = [];

    if (!this.descricao || this.descricao.trim() === '') {
      erros.push('Descrição é obrigatória');
    } else if (this.descricao.length > 500) {
      erros.push('Descrição deve ter no máximo 500 caracteres');
    }

    if (!this.data || this.data.trim() === '') {
      erros.push('Data é obrigatória');
    } else if (!this.validarData(this.data)) {
      erros.push('Data deve estar no futuro');
    }

    if (!this.local || this.local.trim() === '') {
      erros.push('Local é obrigatório');
    } else if (this.local.length > 255) {
      erros.push('Local deve ter no máximo 255 caracteres');
    }

    if (this.foto && this.foto.trim() !== '' && !this.validarURL(this.foto)) {
      erros.push('URL da foto em formato inválido');
    }

    return {
      valido: erros.length === 0,
      erros: erros
    };
  }

  // Método para validar se a data é futura
  validarData(data) {
    const dataEvento = new Date(data);
    const agora = new Date();
    return dataEvento > agora;
  }

  // Método para validar formato de URL
  validarURL(url) {
    try {
      new URL(url);
      return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(url);
    } catch {
      return false;
    }
  }

  // Método para formatar data para exibição
  formatarData() {
    if (!this.data) return '';
    
    const data = new Date(this.data);
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(data);
  }

  // Método para formatar data para input datetime-local
  formatarDataParaInput() {
    if (!this.data) return '';
    
    const data = new Date(this.data);
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    const hora = String(data.getHours()).padStart(2, '0');
    const minuto = String(data.getMinutes()).padStart(2, '0');
    
    return `${ano}-${mes}-${dia}T${hora}:${minuto}`;
  }

  // Método para converter datetime-local para formato ISO
  static converterDataParaISO(dataInput) {
    if (!dataInput) return null;
    return new Date(dataInput).toISOString();
  }

  // Método para criar uma cópia do objeto
  clone() {
    return new EventoModel(
      this.idEvento,
      this.descricao,
      this.data,
      this.local,
      this.animalId,
      this.foto
    );
  }

  // Método para limpar os dados do objeto
  limpar() {
    this.idEvento = null;
    this.descricao = '';
    this.data = '';
    this.local = '';
    this.animalId = null;
    this.foto = '';
  }

  // Método para converter para objeto JSON para envio ao backend
  toJSON() {
    return {
      idEvento: this.idEvento,
      descricao: this.descricao,
      data: this.data,
      local: this.local,
      animalId: this.animalId || null,
      foto: this.foto || null
    };
  }
}

export default EventoModel;