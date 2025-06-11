class PessoaModel {
  
  constructor(idPessoa = null, nome = '', cpf = '', email = '', telefone = '', endereco = '') {
    this.idPessoa = idPessoa;
    this.nome = nome;
    this.cpf = cpf;
    this.email = email;
    this.telefone = telefone;
    this.endereco = endereco;
  }

  // Método para criar uma instância de PessoaModel a partir de um objeto JSON
  static fromJson(json) {
    return new PessoaModel(
      json.idPessoa,
      json.nome,
      json.cpf,
      json.email,
      json.telefone,
      json.endereco
    );
  }

  // Método para converter vários objetos JSON em uma lista de PessoaModel
  static fromJsonList(jsonList) {
    if (!Array.isArray(jsonList)) return [];
    return jsonList.map(json => PessoaModel.fromJson(json));
  }

  // Método para validar os campos obrigatórios
  validar() {
    const erros = [];

    if (!this.nome || this.nome.trim() === '') {
      erros.push('Nome é obrigatório');
    }

    if (!this.cpf || this.cpf.trim() === '') {
      erros.push('CPF é obrigatório');
    } else if (!this.validarCPF(this.cpf)) {
      erros.push('CPF inválido');
    }

    if (this.email && this.email.trim() !== '' && !this.validarEmail(this.email)) {
      erros.push('E-mail em formato inválido');
    }

    return {
      valido: erros.length === 0,
      erros: erros
    };
  }

  // Método para validar formato de e-mail
  validarEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }

  // Método para validar CPF
  validarCPF(cpf) {
    // Remove caracteres não numéricos
    cpf = cpf.replace(/[^\d]/g, '');

    // Verifica se o CPF tem 11 dígitos
    if (cpf.length !== 11) {
      return false;
    }

    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1+$/.test(cpf)) {
      return false;
    }

    // Validação do primeiro dígito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) {
      soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = 11 - (soma % 11);
    let digitoVerificador1 = resto === 10 || resto === 11 ? 0 : resto;
    
    if (digitoVerificador1 !== parseInt(cpf.charAt(9))) {
      return false;
    }

    // Validação do segundo dígito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) {
      soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = 11 - (soma % 11);
    let digitoVerificador2 = resto === 10 || resto === 11 ? 0 : resto;
    
    return digitoVerificador2 === parseInt(cpf.charAt(10));
  }

  // Método para formatar CPF (000.000.000-00)
  formatarCPF() {
    if (!this.cpf) return '';
    
    const cpfNumerico = this.cpf.replace(/\D/g, '');
    
    if (cpfNumerico.length !== 11) return this.cpf;
    
    return cpfNumerico.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  // Método para formatar telefone ((00) 00000-0000)
  formatarTelefone() {
    if (!this.telefone) return '';
    
    const telefoneNumerico = this.telefone.replace(/\D/g, '');
    
    if (telefoneNumerico.length === 11) {
      return telefoneNumerico.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    } else if (telefoneNumerico.length === 10) {
      return telefoneNumerico.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }
    
    return this.telefone;
  }

  // Método para criar uma cópia do objeto
  clone() {
    return new PessoaModel(
      this.idPessoa,
      this.nome,
      this.cpf,
      this.email,
      this.telefone,
      this.endereco
    );
  }

  // Método para limpar os dados do objeto
  limpar() {
    this.idPessoa = null;
    this.nome = '';
    this.cpf = '';
    this.email = '';
    this.telefone = '';
    this.endereco = '';
  }
}
export default PessoaModel;