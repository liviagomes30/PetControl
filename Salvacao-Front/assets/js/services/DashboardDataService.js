// assets/js/services/DashboardDataService.js

class DashboardDataService {
  constructor() {
    this.baseURL = "http://localhost:8080/dashboard";
  }

  /**
   * Busca a lista unificada de compromissos de medicação (aplicados e planejados)
   * a partir do endpoint dedicado do dashboard no backend.
   */
  async listarCompromissosDeMedicacao() {
    try {
      const response = await fetch(`${this.baseURL}/compromissos-medicacao`);
      if (!response.ok) {
        if (response.status === 404) {
          console.warn(
            "Endpoint de compromissos de medicação não encontrado (404). Retornando lista vazia."
          );
          return [];
        }
        throw new Error(`Erro HTTP ao buscar medicações: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Erro no DashboardDataService:", error);
      throw new Error("Não foi possível carregar a lista de medicações.");
    }
  }
}

export default DashboardDataService;
