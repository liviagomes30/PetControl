class CadastroPage {
  constructor() {
    this.form = document.getElementById('cadastroForm');
    this.submitButton = document.getElementById('submitButton');
    this.alertContainer = document.getElementById('alertContainer');
    
    this.nomeInput = document.getElementById('nome');
    this.cpfInput = document.getElementById('cpf');
    this.telefoneInput = document.getElementById('telefone');
    this.emailInput = document.getElementById('email');
    this.enderecoInput = document.getElementById('endereco');
    this.loginInput = document.getElementById('login');
    this.senhaInput = document.getElementById('senha');
    this.confirmarSenhaInput = document.getElementById('confirmarSenha');
    this.aceitarTermosInput = document.getElementById('aceitarTermos');
    
    this.loginFeedback = document.getElementById('loginFeedback');
    this.loginIcon = document.getElementById('loginIcon');
    
    this.reqLength = document.getElementById('req-length');
    this.reqLetter = document.getElementById('req-letter');
    this.reqNumber = document.getElementById('req-number');
    
    this.init();
  }

  init() {
    this.setupEventListeners();
    this.setupInputFormatting();
    this.nomeInput.focus();
  }

  setupEventListeners() {
    this.form.addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleCadastro();
    });

    this.nomeInput.addEventListener('input', () => this.validateNome());
    this.cpfInput.addEventListener('input', () => this.validateCPF());
    this.telefoneInput.addEventListener('input', () => this.validateTelefone());
    this.emailInput.addEventListener('input', () => this.validateEmail());
    this.enderecoInput.addEventListener('input', () => this.validateEndereco());
    this.loginInput.addEventListener('input', () => this.validateLogin());
    this.senhaInput.addEventListener('input', () => this.validateSenha());
    this.confirmarSenhaInput.addEventListener('input', () => this.validateConfirmarSenha());

    let loginTimeout;
    this.loginInput.addEventListener('input', () => {
      clearTimeout(loginTimeout);
      loginTimeout = setTimeout(() => {
        this.checkLoginAvailability();
      }, 500);
    });

    document.getElementById('toggleSenha').addEventListener('click', () => {
      this.togglePasswordVisibility('senha', 'toggleSenha');
    });

    document.getElementById('toggleConfirmarSenha').addEventListener('click', () => {
      this.togglePasswordVisibility('confirmarSenha', 'toggleConfirmarSenha');
    });

    const allInputs = [
      this.nomeInput, this.cpfInput, this.telefoneInput, this.emailInput,
      this.enderecoInput, this.loginInput, this.senhaInput, this.confirmarSenhaInput
    ];

    allInputs.forEach(input => {
      input.addEventListener('input', () => this.clearAlert());
    });
  }

  setupInputFormatting() {
    this.cpfInput.addEventListener('input', (e) => {
      let value = e.target.value.replace(/\D/g, '');
      value = value.replace(/(\d{3})(\d)/, '$1.$2');
      value = value.replace(/(\d{3})(\d)/, '$1.$2');
      value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      e.target.value = value;
    });

    this.telefoneInput.addEventListener('input', (e) => {
      let value = e.target.value.replace(/\D/g, '');
      value = value.replace(/(\d{2})(\d)/, '($1) $2');
      value = value.replace(/(\d{4,5})(\d{4})$/, '$1-$2');
      e.target.value = value;
    });
  }

  validateNome() {
    const nome = this.nomeInput.value.trim();
    const isValid = nome.length >= 3;
    
    this.setInputValidation(this.nomeInput, isValid);
    return isValid;
  }

  validateCPF() {
    const cpf = this.cpfInput.value.replace(/\D/g, '');
    const isValid = cpf.length === 11 && this.isValidCPF(cpf);
    
    this.setInputValidation(this.cpfInput, isValid);
    return isValid;
  }

  validateTelefone() {
    const telefone = this.telefoneInput.value.replace(/\D/g, '');
    const isValid = telefone.length >= 10;
    
    this.setInputValidation(this.telefoneInput, isValid);
    return isValid;
  }

  validateEmail() {
    const email = this.emailInput.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const isValid = emailRegex.test(email);
    
    this.setInputValidation(this.emailInput, isValid);
    return isValid;
  }

  validateEndereco() {
    const endereco = this.enderecoInput.value.trim();
    const isValid = endereco.length >= 5;
    
    this.setInputValidation(this.enderecoInput, isValid);
    return isValid;
  }

  validateLogin() {
    const login = this.loginInput.value.trim();
    const isValid = login.length >= 3;
    
    this.setInputValidation(this.loginInput, isValid);
    return isValid;
  }

  validateSenha() {
    const senha = this.senhaInput.value;
    
    const hasLength = senha.length >= 6;
    const hasLetter = /[a-zA-Z]/.test(senha);
    const hasNumber = /\d/.test(senha);
    
    this.updateRequirement(this.reqLength, hasLength);
    this.updateRequirement(this.reqLetter, hasLetter);
    this.updateRequirement(this.reqNumber, hasNumber);
    
    const isValid = hasLength && hasLetter && hasNumber;
    this.setInputValidation(this.senhaInput, isValid);
    
    if (this.confirmarSenhaInput.value) {
      this.validateConfirmarSenha();
    }
    
    return isValid;
  }

  validateConfirmarSenha() {
    const senha = this.senhaInput.value;
    const confirmarSenha = this.confirmarSenhaInput.value;
    const isValid = confirmarSenha === senha && senha.length > 0;
    
    this.setInputValidation(this.confirmarSenhaInput, isValid);
    return isValid;
  }

  updateRequirement(element, isMet) {
    if (isMet) {
      element.className = 'requirement-met';
    } else {
      element.className = 'requirement-unmet';
    }
  }

  async checkLoginAvailability() {
    const login = this.loginInput.value.trim();
    
    if (login.length < 3) {
      this.setLoginFeedback('', '');
      return;
    }

    try {
      const response = await UsuarioService.verificarLogin(login);
      
      if (response.exists) {
        this.setLoginFeedback('Login já está em uso', 'danger');
        this.loginIcon.className = 'bi bi-x-circle';
        this.loginIcon.style.color = '#dc3545';
      } else {
        this.setLoginFeedback('Login disponível', 'success');
        this.loginIcon.className = 'bi bi-check-circle';
        this.loginIcon.style.color = '#28a745';
      }
    } catch (error) {
      console.error('Erro ao verificar login:', error);
      this.setLoginFeedback('', '');
    }
  }

  setLoginFeedback(message, type) {
    if (!message) {
      this.loginFeedback.innerHTML = '';
      this.loginIcon.className = 'bi bi-person-check';
      this.loginIcon.style.color = '';
      return;
    }

    const textClass = type === 'success' ? 'text-success' : 'text-danger';
    this.loginFeedback.innerHTML = `<small class="${textClass}">${message}</small>`;
  }

  setInputValidation(input, isValid) {
    if (isValid) {
      input.classList.remove('is-invalid');
      input.classList.add('is-valid');
    } else {
      input.classList.remove('is-valid');
      input.classList.add('is-invalid');
    }
  }

  isValidCPF(cpf) {
    if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
      return false;
    }

    let sum = 0;
    for (let i = 0; i < 9; i++) {
      sum += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let remainder = (sum * 10) % 11;
    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.charAt(9))) return false;

    sum = 0;
    for (let i = 0; i < 10; i++) {
      sum += parseInt(cpf.charAt(i)) * (11 - i);
    }
    remainder = (sum * 10) % 11;
    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.charAt(10))) return false;

    return true;
  }

  validateForm() {
    const validations = [
      { fn: () => this.validateNome(), input: this.nomeInput, message: 'Nome é obrigatório (mínimo 3 caracteres)' },
      { fn: () => this.validateCPF(), input: this.cpfInput, message: 'CPF é obrigatório e deve ser válido' },
      { fn: () => this.validateTelefone(), input: this.telefoneInput, message: 'Telefone é obrigatório' },
      { fn: () => this.validateEmail(), input: this.emailInput, message: 'E-mail válido é obrigatório' },
      { fn: () => this.validateEndereco(), input: this.enderecoInput, message: 'Endereço é obrigatório' },
      { fn: () => this.validateLogin(), input: this.loginInput, message: 'Login é obrigatório (mínimo 3 caracteres)' },
      { fn: () => this.validateSenha(), input: this.senhaInput, message: 'Senha deve atender aos requisitos' },
      { fn: () => this.validateConfirmarSenha(), input: this.confirmarSenhaInput, message: 'Confirmação de senha deve ser igual à senha' }
    ];

    const loginExists = this.loginFeedback.innerHTML.includes('já está em uso');
    if (loginExists) {
      this.showAlert('Login já está em uso. Escolha outro login.', 'danger');
      this.loginInput.focus();
      return false;
    }

    if (!this.aceitarTermosInput.checked) {
      this.showAlert('É necessário aceitar os termos de uso.', 'danger');
      this.aceitarTermosInput.focus();
      return false;
    }

    for (const validation of validations) {
      if (!validation.fn()) {
        this.showAlert(validation.message, 'danger');
        validation.input.focus();
        return false;
      }
    }

    return true;
  }

  async handleCadastro() {
    if (!this.validateForm()) {
      return;
    }

    const formData = this.getFormData();
    
    this.setLoading(true);
    this.clearAlert();

    try {
      const response = await UsuarioService.criarUsuario(formData);
      
      this.showAlert('Cadastro realizado com sucesso! Redirecionando para o login...', 'success');
      
      setTimeout(() => {
        window.location.href = '../../login.html';
      }, 2000);

    } catch (error) {
      console.error('Erro no cadastro:', error);
      
      let errorMessage = 'Erro interno do servidor. Tente novamente.';
      
      if (error.message.includes('Login já existe')) {
        errorMessage = 'Login já está em uso. Escolha outro login.';
        this.loginInput.focus();
      } else if (error.message.includes('CPF já cadastrado')) {
        errorMessage = 'CPF já está cadastrado no sistema.';
        this.cpfInput.focus();
      } else if (error.message.includes('E-mail já cadastrado')) {
        errorMessage = 'E-mail já está cadastrado no sistema.';
        this.emailInput.focus();
      } else if (error.message.includes('fetch')) {
        errorMessage = 'Erro de conexão. Verifique sua internet.';
      } else if (error.message) {
        errorMessage = error.message;
      }

      this.showAlert(errorMessage, 'danger');
      
    } finally {
      this.setLoading(false);
    }
  }

  getFormData() {
    return {
      pessoa: {
        nome: this.nomeInput.value.trim(),
        cpf: this.cpfInput.value.replace(/\D/g, ''),
        telefone: this.telefoneInput.value.replace(/\D/g, ''),
        email: this.emailInput.value.trim(),
        endereco: this.enderecoInput.value.trim()
      },
      usuario: {
        login: this.loginInput.value.trim(),
        senha: this.senhaInput.value
      }
    };
  }

  setLoading(isLoading) {
    const allInputs = [
      this.nomeInput, this.cpfInput, this.telefoneInput, this.emailInput,
      this.enderecoInput, this.loginInput, this.senhaInput, this.confirmarSenhaInput,
      this.aceitarTermosInput
    ];

    if (isLoading) {
      this.submitButton.disabled = true;
      this.submitButton.innerHTML = `
        <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Cadastrando...
      `;
      allInputs.forEach(input => input.disabled = true);
    } else {
      this.submitButton.disabled = false;
      this.submitButton.innerHTML = '<i class="bi bi-person-plus me-2"></i>Cadastrar';
      allInputs.forEach(input => input.disabled = false);
    }
  }

  showAlert(message, type) {
    const alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
    const icon = type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill';
    
    this.alertContainer.innerHTML = `
      <div class="alert ${alertClass} d-flex align-items-center" role="alert">
        <i class="bi ${icon} me-2"></i>
        <div>${message}</div>
      </div>
    `;
    
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    if (type === 'success') {
      setTimeout(() => {
        this.clearAlert();
      }, 5000);
    }
  }

  clearAlert() {
    this.alertContainer.innerHTML = '';
  }

  togglePasswordVisibility(inputId, toggleId) {
    const input = document.getElementById(inputId);
    const toggle = document.getElementById(toggleId);
    const icon = toggle.querySelector('i');
    
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);
    
    if (icon) {
      icon.classList.toggle('bi-eye');
      icon.classList.toggle('bi-eye-slash');
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  new CadastroPage();
}); 