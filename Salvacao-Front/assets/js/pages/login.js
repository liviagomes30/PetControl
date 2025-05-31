class LoginPage {
  constructor() {
    this.form = document.getElementById('loginForm');
    this.loginInput = document.getElementById('login');
    this.passwordInput = document.getElementById('password');
    this.submitButton = document.getElementById('submitButton');
    this.alertContainer = document.getElementById('alertContainer');
    this.rememberCheckbox = document.getElementById('rememberMe');
    
    this.init();
  }

  init() {
    if (AuthService.redirectIfAuthenticated()) {
      return;
    }

    this.loadRememberedData();

    this.setupEventListeners();

    this.loginInput.focus();
  }

  setupEventListeners() {
    this.form.addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleLogin();
    });

    [this.loginInput, this.passwordInput].forEach(input => {
      input.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
          this.handleLogin();
        }
      });
    });

    [this.loginInput, this.passwordInput].forEach(input => {
      input.addEventListener('input', () => {
        this.clearAlert();
        this.validateInput(input);
      });
    });

    const togglePassword = document.getElementById('togglePassword');
    if (togglePassword) {
      togglePassword.addEventListener('click', () => {
        this.togglePasswordVisibility();
      });
    }
  }

  validateInput(input) {
    const isValid = input.value.trim().length > 0;
    
    if (isValid) {
      input.classList.remove('is-invalid');
      input.classList.add('is-valid');
    } else {
      input.classList.remove('is-valid');
      input.classList.add('is-invalid');
    }

    return isValid;
  }

  validateForm() {
    const loginValid = this.validateInput(this.loginInput);
    const passwordValid = this.validateInput(this.passwordInput);

    if (!loginValid) {
      this.showAlert('Por favor, digite seu login.', 'danger');
      this.loginInput.focus();
      return false;
    }

    if (!passwordValid) {
      this.showAlert('Por favor, digite sua senha.', 'danger');
      this.passwordInput.focus();
      return false;
    }

    return true;
  }

  async handleLogin() {
    if (!this.validateForm()) {
      return;
    }

    const login = this.loginInput.value.trim();
    const password = this.passwordInput.value;

    this.setLoading(true);
    this.clearAlert();

    try {
      const userData = await AuthService.login(login, password);

      if (this.rememberCheckbox.checked) {
        this.saveRememberedData(login);
      } else {
        this.clearRememberedData();
      }

      this.showAlert('Login realizado com sucesso! Redirecionando...', 'success');

      setTimeout(() => {
        window.location.href = './index.html';
      }, 1500);

    } catch (error) {
      console.error('Erro no login:', error);
      
      let errorMessage = 'Erro interno do servidor. Tente novamente.';
      
      if (error.message.includes('Credenciais inválidas')) {
        errorMessage = 'Login ou senha incorretos.';
      } else if (error.message.includes('fetch')) {
        errorMessage = 'Erro de conexão. Verifique sua internet.';
      }

      this.showAlert(errorMessage, 'danger');
      this.passwordInput.focus();
      this.passwordInput.select();
      
    } finally {
      this.setLoading(false);
    }
  }

  setLoading(isLoading) {
    if (isLoading) {
      this.submitButton.disabled = true;
      this.submitButton.innerHTML = `
        <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Entrando...
      `;
      this.loginInput.disabled = true;
      this.passwordInput.disabled = true;
    } else {
      this.submitButton.disabled = false;
      this.submitButton.innerHTML = 'Entrar';
      this.loginInput.disabled = false;
      this.passwordInput.disabled = false;
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
    
    if (type === 'success') {
      setTimeout(() => {
        this.clearAlert();
      }, 3000);
    }
  }

  clearAlert() {
    this.alertContainer.innerHTML = '';
  }

  togglePasswordVisibility() {
    const type = this.passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    this.passwordInput.setAttribute('type', type);
    
    const toggleIcon = document.querySelector('#togglePassword i');
    if (toggleIcon) {
      toggleIcon.classList.toggle('bi-eye');
      toggleIcon.classList.toggle('bi-eye-slash');
    }
  }

  saveRememberedData(login) {
    localStorage.setItem('petcontrol_remember_login', login);
    localStorage.setItem('petcontrol_remember_me', 'true');
  }

  loadRememberedData() {
    const rememberMe = localStorage.getItem('petcontrol_remember_me') === 'true';
    const savedLogin = localStorage.getItem('petcontrol_remember_login');

    if (rememberMe && savedLogin) {
      this.loginInput.value = savedLogin;
      this.rememberCheckbox.checked = true;
      this.passwordInput.focus();
    }
  }

  clearRememberedData() {
    localStorage.removeItem('petcontrol_remember_login');
    localStorage.removeItem('petcontrol_remember_me');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  new LoginPage();
}); 