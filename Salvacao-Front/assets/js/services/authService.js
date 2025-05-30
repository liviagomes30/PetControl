const AuthService = {
  USER_KEY: 'petcontrol_user',
  TOKEN_KEY: 'petcontrol_session',

  getLoginPath() {
    const path = window.location.pathname;
    
    if (path.includes('/Salvacao-Front/') && !path.includes('/pages/')) {
      return './login.html';
    }
    
    if (path.includes('/pages/')) {
      const segments = path.split('/');
      const pagesIndex = segments.indexOf('pages');
      if (pagesIndex !== -1) {
        const levelsUp = segments.length - pagesIndex - 1;
        return '../'.repeat(levelsUp) + 'login.html';
      }
    }
    
    return './login.html';
  },

  getIndexPath() {
    const path = window.location.pathname;
    
    if (path.includes('/Salvacao-Front/') && !path.includes('/pages/')) {
      return './index.html';
    }
    
    if (path.includes('/pages/')) {
      const segments = path.split('/');
      const pagesIndex = segments.indexOf('pages');
      if (pagesIndex !== -1) {
        const levelsUp = segments.length - pagesIndex - 1;
        return '../'.repeat(levelsUp) + 'index.html';
      }
    }
    
    return './index.html';
  },

  async login(login, senha) {
    try {
      const userData = await UsuarioService.authenticate(login, senha);
      
      this.setUser(userData);
      
      this.setSession(true);
      
      return userData;
    } catch (error) {
      throw error;
    }
  },

  logout() {
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
    
    window.location.href = this.getLoginPath();
  },

  isAuthenticated() {
    const session = localStorage.getItem(this.TOKEN_KEY);
    const user = localStorage.getItem(this.USER_KEY);
    return session === 'true' && user !== null;
  },

  getCurrentUser() {
    const userData = localStorage.getItem(this.USER_KEY);
    return userData ? JSON.parse(userData) : null;
  },

  setUser(userData) {
    localStorage.setItem(this.USER_KEY, JSON.stringify(userData));
  },

  setSession(isActive) {
    localStorage.setItem(this.TOKEN_KEY, isActive.toString());
  },

  requireAuth() {
    if (!this.isAuthenticated()) {
      window.location.href = this.getLoginPath();
      return false;
    }
    return true;
  },

  redirectIfAuthenticated() {
    if (this.isAuthenticated()) {
      window.location.href = this.getIndexPath();
      return true;
    }
    return false;
  }
};

window.AuthService = AuthService; 