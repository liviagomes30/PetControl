// Salvacao-Front/assets/js/components/sidebar.js
document.addEventListener("DOMContentLoaded", function () {
  const bootstrapIconsLoaded = Array.from(
      document.querySelectorAll("link")
  ).some((link) => link.href.includes("bootstrap-icons"));

  if (!bootstrapIconsLoaded) {
    const iconLink = document.createElement("link");
    iconLink.rel = "stylesheet";
    iconLink.href =
        "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css";
    document.head.appendChild(iconLink);
  }
  const sidebar = document.createElement("div");
  sidebar.className = "sidebar";

  sidebar.innerHTML = `
    <div class="sidebar-logo">
      PetControl
    </div>
    <ul class="sidebar-menu">
      <a href="${getBasePath()}index.html" class="sidebar-item" id="menu-home">
        <div class="sidebar-item-content">
          <i class="bi bi-house sidebar-item-icon"></i>
          <span class="sidebar-item-text">Home</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>

      <div class="sidebar-item" id="menu-animais" onclick="toggleSubmenu('animaisSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-palette sidebar-item-icon"></i>
          <span class="sidebar-item-text">Animais</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="animaisSubmenu" class="sidebar-submenu">
        <li>
          <a href="${getBasePath()}pages/animal/gerenciarAnimal.html" class="sidebar-submenu-item" id="submenu-animais-gerenciar">Gerenciar Animais</a>
        </li>
        <li>
          <a href="${getBasePath()}pages/eventos/gerenciarEvento.html" class="sidebar-submenu-item" id="submenu-eventos-gerenciar">Gerenciar Eventos</a>
        </li>
      </ul>
      
      <div class="sidebar-item" id="menu-medicacao" onclick="toggleSubmenu('medicacaoSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-capsule sidebar-item-icon"></i>
          <span class="sidebar-item-text">Medicação</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="medicacaoSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/medicamentos/listarMedicamentos.html" class="sidebar-submenu-item" id="submenu-medicacao-gerenciar">Gerenciar Medicamentos</a>
        <a href="${getBasePath()}pages/medicamentos/efetuarMedicacao.html" class="sidebar-submenu-item" id="submenu-medicacao-efetuar">Efetuar Medicação</a>
        <a href="${getBasePath()}pages/medicamentos/historicoMedicacoes.html" class="sidebar-submenu-item" id="submenu-medicacao-historico">Histórico Medicações</a>
      </ul>
      
      <div class="sidebar-item" id="menu-vacinacao" onclick="toggleSubmenu('vacinacaoSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-bandaid sidebar-item-icon"></i>
          <span class="sidebar-item-text">Vacinação</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="vacinacaoSubmenu" class="sidebar-submenu">
        <li>
          <a href="${getBasePath()}pages/vacina/listarVacinas.html" class="sidebar-submenu-item" id="submenu-vacina-gerenciar">Gerenciar Vacinas</a>
        </li>
        <li>
          <a href="${getBasePath()}pages/vacina/cadastrarVacina.html" class="sidebar-submenu-item" id="submenu-vacina-cadastrar">Cadastrar Tipo de Vacina</a>
        </li>
        <li>
          <a href="${getBasePath()}pages/vacinacao/efetuarVacinacao.html" class="sidebar-submenu-item" id="submenu-vacinacao-efetuar">Efetuar Vacinação</a>
        </li>
         <li>
          <a href="${getBasePath()}pages/vacinacao/historicoVacinacoes.html" class="sidebar-submenu-item" id="submenu-vacinacao-historico">Histórico de Vacinações</a>
        </li>
      </ul>
      
      <div class="sidebar-item" id="menu-pessoas" onclick="toggleSubmenu('pessoasSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-people sidebar-item-icon"></i>
          <span class="sidebar-item-text">Pessoas</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="pessoasSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/pessoas/listarAdotante.html" class="sidebar-submenu-item" id="submenu-pessoas-gerenciar">Gerenciar Adotante</a>
        <a href="${getBasePath()}pages/doacao/listarDoacao.html" class="sidebar-submenu-item" id="submenu-pessoas-cadastrar">Gerenciar Adoção</a>
      </ul>
      
      <div class="sidebar-item" id="menu-produtos" onclick="toggleSubmenu('produtosSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-plus-circle sidebar-item-icon"></i>
          <span class="sidebar-item-text">Produtos</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="produtosSubmenu" class="sidebar-submenu">
        <a href="${getBasePath()}pages/produto/listar.html" class="sidebar-submenu-item" id="submenu-produtos-listar">Lista de Produtos</a>
        <a href="${getBasePath()}pages/produto/novo.html" class="sidebar-submenu-item" id="submenu-produtos-novo">Novo Produto</a>
        <a href="${getBasePath()}pages/produto/categorias.html" class="sidebar-submenu-item" id="submenu-produtos-categorias">Lista de Categorias</a>
        <a href="${getBasePath()}pages/produto/tipo/listarTipos.html" class="sidebar-submenu-item" id="submenu-produtos-tipos">Tipos de Produto</a>
        <a href="${getBasePath()}pages/produto/unidade/listarUnidades.html" class="sidebar-submenu-item" id="submenu-produtos-unidades">Unidades de Medida</a>
      </ul>
      
      <a href="${getBasePath()}pages/acerto-estoque/listarAcertosEstoque.html" class="sidebar-item" id="menu-estoque">
        <div class="sidebar-item-content">
          <i class="bi bi-box sidebar-item-icon"></i>
          <span class="sidebar-item-text">Estoque</span>
        </div>
        <i class="bi bi-chevron-right sidebar-item-arrow"></i>
      </a>
    </ul>
  `;

  document.body.prepend(sidebar);

  const mainContent =
      document.querySelector(".main-content") || document.createElement("div");
  if (!mainContent.classList.contains("main-content")) {
    mainContent.className = "main-content";
    Array.from(document.body.childNodes).forEach(node => {
      if (node !== sidebar && node !== mainContent) {
        mainContent.appendChild(node);
      }
    });
    document.body.appendChild(mainContent);
  }

  markActiveMenuItem();
});

function toggleSubmenu(submenuId) {
  const submenu = document.getElementById(submenuId);
  if (!submenu) return;
  submenu.classList.toggle("open");

  const parentItem = submenu.previousElementSibling;
  if (!parentItem) return;
  const arrow = parentItem.querySelector(".sidebar-item-arrow");
  if (!arrow) return;

  if (submenu.classList.contains("open")) {
    arrow.classList.remove("bi-chevron-down");
    arrow.classList.add("bi-chevron-up");
  } else {
    arrow.classList.remove("bi-chevron-up");
    arrow.classList.add("bi-chevron-down");
  }
}

function markActiveMenuItem() {
  const currentPath = window.location.pathname;

  document
      .querySelectorAll(".sidebar-item, .sidebar-submenu-item")
      .forEach((item) => {
        item.classList.remove("active");
      });

  document.querySelectorAll('.sidebar-submenu.open').forEach(submenu => {
    submenu.classList.remove('open');
    const parentItem = submenu.previousElementSibling;
    if (parentItem) {
      const arrow = parentItem.querySelector(".sidebar-item-arrow.bi-chevron-up");
      if (arrow) {
        arrow.classList.remove("bi-chevron-up");
        arrow.classList.add("bi-chevron-down");
      }
    }
  });

  let activeParentFound = false;
  const menuItems = [
    { id: "menu-home", pathCheck: (path) => path.includes("index.html") || path.endsWith("/") || path.endsWith("/Salvacao-Front/") },
    { id: "menu-animais", pathCheck: (path) => path.includes("/animal/") || path.includes("/eventos/"), submenuId: "animaisSubmenu",
      subItems: [
        { id: "submenu-animais-gerenciar", pathCheck: (path) => path.includes("/animal/gerenciarAnimal.html")},
        { id: "submenu-eventos-gerenciar", pathCheck: (path) => path.includes("/eventos/gerenciarEvento.html")}
      ]
    },
    { id: "menu-medicacao", pathCheck: (path) => path.includes("/medicamentos/"), submenuId: "medicacaoSubmenu",
      subItems: [
        { id: "submenu-medicacao-gerenciar", pathCheck: (path) => path.includes("listarMedicamentos.html")},
        { id: "submenu-medicacao-efetuar", pathCheck: (path) => path.includes("efetuarMedicacao.html")},
        { id: "submenu-medicacao-historico", pathCheck: (path) => path.includes("historicoMedicacoes.html")}
      ]
    },
    { id: "menu-vacinacao", pathCheck: (path) => path.includes("/vacina/") || path.includes("/vacinacao/"), submenuId: "vacinacaoSubmenu",
      subItems: [
        { id: "submenu-vacina-gerenciar", pathCheck: (path) => path.includes("/vacina/listarVacinas.html")},
        { id: "submenu-vacina-cadastrar", pathCheck: (path) => path.includes("/vacina/cadastrarVacina.html")},
        { id: "submenu-vacinacao-efetuar", pathCheck: (path) => path.includes("/vacinacao/efetuarVacinacao.html")},
        { id: "submenu-vacinacao-historico", pathCheck: (path) => path.includes("/vacinacao/historicoVacinacoes.html")}
      ]
    },
    { id: "menu-pessoas", pathCheck: (path) => path.includes("/pessoas/") || path.includes("/doacao/"), submenuId: "pessoasSubmenu",
      subItems: [
        { id: "submenu-pessoas-gerenciar", pathCheck: (path) => path.includes("/pessoas/listarAdotante.html")},
        { id: "submenu-pessoas-cadastrar", pathCheck: (path) => path.includes("/doacao/listarDoacao.html")}
      ]
    },
    { id: "menu-produtos", pathCheck: (path) => path.includes("/produto/"), submenuId: "produtosSubmenu",
      subItems: [
        { id: "submenu-produtos-listar", pathCheck: (path) => path.includes("/produto/listar.html")},
        { id: "submenu-produtos-novo", pathCheck: (path) => path.includes("/produto/novo.html")},
        { id: "submenu-produtos-categorias", pathCheck: (path) => path.includes("/produto/categorias.html")},
        { id: "submenu-produtos-tipos", pathCheck: (path) => path.includes("/produto/tipo/")},
        { id: "submenu-produtos-unidades", pathCheck: (path) => path.includes("/produto/unidade/")}
      ]
    },
    { id: "menu-estoque", pathCheck: (path) => path.includes("/acerto-estoque/") }
  ];

  for (const menuItem of menuItems) {
    const menuElement = document.getElementById(menuItem.id);
    if (menuElement && menuItem.pathCheck(currentPath)) {
      menuElement.classList.add("active");
      activeParentFound = true;
      if (menuItem.submenuId) {
        const submenuElement = document.getElementById(menuItem.submenuId);
        if (submenuElement) {
          let openSubmenu = false;
          if (menuItem.subItems) {
            for (const subItem of menuItem.subItems) {
              const subItemElement = document.getElementById(subItem.id);
              if (subItemElement && subItem.pathCheck(currentPath)) {
                subItemElement.classList.add("active");
                openSubmenu = true;
                break;
              }
            }
          }
          if(openSubmenu){
            submenuElement.classList.add("open");
            const arrow = menuElement.querySelector(".sidebar-item-arrow");
            if (arrow) {
              arrow.classList.remove("bi-chevron-down");
              arrow.classList.add("bi-chevron-up");
            }
          }
        }
      }
      break;
    }
  }

  if (!activeParentFound && (currentPath === getBasePath() || currentPath === getBasePath() + "index.html")) {
    const homeMenu = document.getElementById("menu-home");
    if (homeMenu) homeMenu.classList.add("active");
  }
}

function getBasePath() {
  const path = window.location.pathname;
  const segments = path.split('/');
  const pagesIndex = segments.indexOf('pages');

  if (pagesIndex !== -1) {
    const levelsUp = segments.length - pagesIndex - 2;
    if (levelsUp < 0) return "./"; // Caso de /pages/arquivo.html
    return '../'.repeat(levelsUp);
  } else {
    // Verifica se está na raiz (ex: /index.html ou /Projeto/index.html)
    // Conta quantos segmentos existem além do nome do arquivo.
    const pathEnd = segments[segments.length - 1];
    const isIndexOrRoot = pathEnd === "" || pathEnd === "index.html";
    let depth = 0;
    if (!isIndexOrRoot) { // Se for algo como /projeto/outrapagina.html
      depth = segments.filter(s => s && !s.includes('.html')).length;
      if (segments[0] === "") depth--; // Ajuste para path absoluto começando com /
    } else { // Se for index.html ou /
      const relevantSegments = segments.filter(s => s && s !== "index.html");
      depth = relevantSegments.length > 1 ? relevantSegments.length -1 : 0;
    }
    if (depth <=0) return "./";

    return "../".repeat(depth);
  }
}