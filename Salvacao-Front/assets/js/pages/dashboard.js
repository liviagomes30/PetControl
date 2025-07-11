// assets/js/pages/dashboard.js
import CardFiltrosEventos from "../components/cardFiltrosEventos.js";
import CardFiltrosMedicacoes from "../components/CardFiltrosMedicacoes.js";
import CardEventos from "../components/CardEventos.js";
import CardEventosInativos from "../components/CardEventosInativos.js";
import CardMedicacoes from "../components/CardMedicacoes.js";
import EventoService from "../services/EventoService.js";
import AnimalService from "../services/AnimalService.js";
import DashboardDataService from "../services/DashboardDataService.js";

class DashboardController {
  constructor() {
    this.container = document.getElementById("dashboard-container");

    // Serviços
    this.eventoService = new EventoService();
    this.animalService = new AnimalService();
    this.dashboardDataService = new DashboardDataService();

    // Caches de dados
    this.todosEventos = [];
    this.todosCompromissosMedicacao = [];
    this.animaisMap = new Map();
  }

  async inicializar() {
    if (!this.container) {
      console.error("Container do dashboard não encontrado!");
      return;
    }
    await this.carregarDadosIniciais();
    this.renderizarLayout();
    this.vincularEventos();
  }

  async carregarDadosIniciais() {
    try {
      const [eventos, animais, medicacoes] = await Promise.all([
        this.eventoService.listarTodos(),
        this.animalService.listarTodos(),
        this.dashboardDataService.listarCompromissosDeMedicacao(),
      ]);

      this.animaisMap = new Map(
        animais.map((animal) => [animal.id, animal.nome])
      );
      this.todosEventos = eventos; //
      this.todosCompromissosMedicacao = medicacoes; //
    } catch (error) {
      console.error("Erro ao carregar dados iniciais:", error);
      this.container.innerHTML = `<div class="alert alert-danger">Erro fatal ao carregar os dados. Verifique a conexão e atualize a página.</div>`;
    }
  }

  recarregarDashboard() {
    this.inicializar();
  }

  calcularEstatisticas() {
    const eventosAtivos = this.todosEventos.filter(
      (e) => e.status !== "Cancelado" && e.status !== "Adiado"
    ).length;

    const medicacoesPlanejadas = this.todosCompromissosMedicacao.filter(
      (m) => m.status.toLowerCase() === "planejado"
    ).length;

    const totalAnimais = this.animaisMap.size;

    // Lógica para contar as medicações de hoje
    const hojeStr = new Date().toISOString().slice(0, 10); // Formato "YYYY-MM-DD"
    const medicacoesHoje = this.todosCompromissosMedicacao.filter((comp) => {
      if (comp.status.toLowerCase() !== "planejado") {
        return false;
      }

      let dataCompromissoStr = "";
      if (Array.isArray(comp.dataHora)) {
        // Formato [YYYY, MM, DD, ...]
        const [ano, mes, dia] = comp.dataHora;
        dataCompromissoStr = `${ano}-${String(mes).padStart(2, "0")}-${String(
          dia
        ).padStart(2, "0")}`;
      } else if (typeof comp.dataHora === "string") {
        // Formato "YYYY-MM-DDTHH:MM:SS"
        dataCompromissoStr = comp.dataHora.slice(0, 10);
      }

      return dataCompromissoStr === hojeStr;
    }).length;

    return {
      eventosAtivos,
      medicacoesPlanejadas,
      totalAnimais,
      medicacoesHoje,
    };
  }

  renderizarLayout() {
    const stats = this.calcularEstatisticas();

    this.container.innerHTML = `
      <style>
        .stat-card-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: var(--spacing-lg, 24px);
        }
        .stat-card {
          background-color: white;
          border-radius: var(--border-radius-md, 8px);
          padding: var(--spacing-lg, 24px);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          border-left: 5px solid var(--laranja-suave, #f2a03d);
          transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        .stat-card h4 {
          font-size: 2.25rem;
          color: var(--vermelho-escuro, #400101);
          margin-bottom: var(--spacing-xs, 4px);
        }
        .stat-card p {
          color: var(--text-secondary, #666);
          font-size: 1rem;
          margin: 0;
        }
        /* Estilo especial para o card de hoje */
        .stat-card.today {
            border-left-color: var(--laranja-vibrante, #f2541b);
        }
      </style>

      <div class="content-header">
        <h1>Dashboard</h1>
      </div>

      <div class="stat-card-grid mb-5">
        <div class="stat-card today">
          <h4>${stats.medicacoesHoje}</h4>
          <p>Medicações para Hoje</p>
        </div>
        <div class="stat-card">
          <h4>${stats.eventosAtivos}</h4>
          <p>Eventos Ativos</p>
        </div>
        <div class="stat-card">
          <h4>${stats.medicacoesPlanejadas}</h4>
          <p>Total de Medicações Planejadas</p>
        </div>
        <div class="stat-card">
          <h4>${stats.totalAnimais}</h4>
          <p>Animais Cadastrados</p>
        </div>
      </div>

      <div class="form-container mb-4">
        <h2 class="h4 mb-4">Agenda de Eventos</h2>
        <div id="eventos-section"></div>
      </div>
      
      <div class="form-container">
        <h2 class="h4 mb-4">Agenda de Medicações</h2>
        <div id="medicamentos-section"></div>
      </div>
    `;
    // --- FIM DA MODIFICAÇÃO ---

    this.renderizarSecaoEventos();
    this.renderizarSecaoMedicacoes();
  }

  renderizarSecaoEventos(eventosParaRenderizar = null) {
    const container = document.getElementById("eventos-section");
    if (!container) return;

    const eventos = eventosParaRenderizar ?? this.todosEventos;

    const eventosFormatados = eventos.map((evento) => ({
      ...evento,
      data: new Date(evento.data).toLocaleDateString("pt-BR", {
        timeZone: "UTC",
      }),
      animal: this.animaisMap.get(evento.animalIdAnimal) || "Geral",
      tipo: "Evento",
      status: evento.status || "Indefinido",
    }));

    const eventosAtivos = eventosFormatados.filter(
      (e) => e.status !== "Cancelado" && e.status !== "Adiado"
    );
    const eventosInativos = eventosFormatados.filter(
      (e) => e.status === "Cancelado" || e.status === "Adiado"
    );

    container.innerHTML = `
      <div id="filtros-eventos-container"></div>
      <div id="card-eventos-container"></div>
      <div id="card-eventos-inativos-container"></div>
    `;

    new CardFiltrosEventos("filtros-eventos-container").render();
    new CardEventos(
      "card-eventos-container",
      eventosAtivos,
      this.recarregarDashboard.bind(this)
    ).render();
    new CardEventosInativos(
      "card-eventos-inativos-container",
      eventosInativos,
      this.recarregarDashboard.bind(this)
    ).render();
  }

  renderizarSecaoMedicacoes(medicacoesParaRenderizar = null) {
    const container = document.getElementById("medicamentos-section");
    if (!container) return;

    const compromissos =
      medicacoesParaRenderizar ?? this.todosCompromissosMedicacao;

    container.innerHTML = `
      <div id="filtros-medicacoes-container"></div>
      <div id="card-medicacoes-container"></div>
    `;

    new CardFiltrosMedicacoes("filtros-medicacoes-container").render();

    // --- INÍCIO DA MODIFICAÇÃO ---
    // Passamos o `this.animaisMap` para o construtor do CardMedicacoes.
    new CardMedicacoes(
      "card-medicacoes-container",
      compromissos,
      this.animaisMap
    ).render();
    // --- FIM DA MODIFICAÇÃO ---

    const selectAnimal = document.getElementById("filtroAnimalMedicacao");
    if (selectAnimal) {
      this.animaisMap.forEach((nome, id) => {
        const option = new Option(nome, id);
        selectAnimal.appendChild(option);
      });
    }
  }

  vincularEventos() {
    this.container.addEventListener("click", (e) => {
      const targetId = e.target.closest("[id]")?.id;

      switch (targetId) {
        case "btnFiltrarEventos":
          this.aplicarFiltrosEventos();
          break;
        case "btnLimparFiltrosEventos":
          this.limparFiltrosEventos();
          break;
        case "btnFiltrarMedicacoes":
          this.aplicarFiltrosMedicacoes();
          break;
      }
    });
  }

  aplicarFiltrosEventos() {
    const status = document
      .getElementById("filtroStatusEvento")
      .value.toLowerCase();
    const data = document.getElementById("filtroDataEvento").value;

    let eventosFiltrados = this.todosEventos;

    if (status) {
      eventosFiltrados = eventosFiltrados.filter(
        (e) => e.status && e.status.toLowerCase() === status
      );
    }
    if (data) {
      eventosFiltrados = eventosFiltrados.filter((e) =>
        e.data.startsWith(data)
      );
    }

    this.renderizarSecaoEventos(eventosFiltrados);
  }

  limparFiltrosEventos() {
    const form = document.getElementById("eventosFilterForm");
    if (form) form.reset();
    this.renderizarSecaoEventos(this.todosEventos);
  }

  aplicarFiltrosMedicacoes() {
    const status = document.getElementById("filtroStatusMedicacao").value;
    const animalId = document.getElementById("filtroAnimalMedicacao").value;
    const dataInicio = document.getElementById(
      "filtroDataInicioMedicacoes"
    ).value;
    const dataFim = document.getElementById("filtroDataFimMedicacoes").value;

    let medicacoesFiltradas = this.todosCompromissosMedicacao;

    if (status) {
      medicacoesFiltradas = medicacoesFiltradas.filter(
        (m) => m.status.toLowerCase() === status.toLowerCase()
      );
    }
    if (animalId) {
      medicacoesFiltradas = medicacoesFiltradas.filter((m) => {
        const animal = [...this.animaisMap.entries()].find(
          ([id, nome]) => nome === m.animalNome
        );
        return animal && animal[0] == animalId;
      });
    }
    if (dataInicio) {
      medicacoesFiltradas = medicacoesFiltradas.filter(
        (m) => new Date(m.dataHora) >= new Date(dataInicio + "T00:00:00")
      );
    }
    if (dataFim) {
      const dataFimObj = new Date(dataFim + "T23:59:59");
      medicacoesFiltradas = medicacoesFiltradas.filter(
        (m) => new Date(m.dataHora) <= dataFimObj
      );
    }

    this.renderizarSecaoMedicacoes(medicacoesFiltradas);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const dashboard = new DashboardController();
  dashboard.inicializar();
});
