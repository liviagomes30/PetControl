const UsuarioService = {
  async listarUsuarios() {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/listar`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        mode: "cors",
      });
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao listar usuários:", error);
      throw error;
    }
  },

  async buscarUsuario(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        mode: "cors",
      });
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      throw error;
    }
  },

  async criarUsuario(usuario) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/cadastro`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(usuario),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Erro: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao criar usuário:", error);
      throw error;
    }
  },

  async atualizarUsuario(id, usuario) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(usuario),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Erro: ${response.status}`);
      }

      return await response.text();
    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      throw error;
    }
  },

  async deletarUsuario(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Erro: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Erro ao deletar usuário:", error);
      throw error;
    }
  },

  async reativarUsuario(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/${id}/reativar`, {
        method: "PUT",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Erro: ${response.status}`);
      }

      return await response.text();
    } catch (error) {
      console.error("Erro ao reativar usuário:", error);
      throw error;
    }
  },

  async buscarPorNome(nome) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/usuarios/nome/${encodeURIComponent(nome)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar usuários por nome:", error);
      throw error;
    }
  },

  async verificarLogin(login) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/usuarios/check-login/${encodeURIComponent(login)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao verificar login:", error);
      throw error;
    }
  },

  async buscarPorLogin(login) {
    try {
      const response = await fetch(
        `${API_BASE_URL}/usuarios/login/${encodeURIComponent(login)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Erro: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro ao buscar usuário por login:", error);
      throw error;
    }
  },

  async atualizarSenha(id, senhaData) {
    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/${id}/senha`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(senhaData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Erro: ${response.status}`);
      }

      return await response.text();
    } catch (error) {
      console.error("Erro ao atualizar senha:", error);
      throw error;
    }
  },
};

window.UsuarioService = UsuarioService;
