// static/assets/js/components/sidebar.js
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

  const basePath = getBasePath();

  sidebar.innerHTML = `
    <div class="sidebar-logo">
      PetControl
    </div>
    <ul class="sidebar-menu">
      <a href="${basePath}index.html" class="sidebar-item" id="menu-home">
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
          <a href="${basePath}pages/animal/gerenciarAnimal.html" class="sidebar-submenu-item" id="submenu-animais-gerenciar">Gerenciar Animais</a>
        </li>
        <li>
          <a href="${basePath}pages/eventos/gerenciarEvento.html" class="sidebar-submenu-item" id="submenu-eventos-gerenciar">Gerenciar Eventos</a>
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
        <a href="${basePath}pages/medicamentos/listarMedicamentos.html" class="sidebar-submenu-item" id="submenu-medicacao-gerenciar">Gerenciar Medicamentos</a>
        <a href="${basePath}pages/medicamentos/efetuarMedicacao.html" class="sidebar-submenu-item" id="submenu-medicacao-efetuar">Efetuar Medicação</a>
        <a href="${basePath}pages/medicamentos/historicoMedicacoes.html" class="sidebar-submenu-item" id="submenu-medicacao-historico">Histórico Medicações</a>
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
          <a href="${basePath}pages/vacina/listarVacinas.html" class="sidebar-submenu-item" id="submenu-vacina-gerenciar-tipos">Gerenciar vacinas</a>
        </li>
        <!--
        <li>
          <a href="${basePath}pages/vacina/cadastrarVacina.html" class="sidebar-submenu-item" id="submenu-vacina-cadastrar-tipo">Cadastrar Tipo de Vacina</a>
        </li>
        -->
        <li>
          <a href="${basePath}pages/vacinacao/cadastrarVacinacao.html" class="sidebar-submenu-item" id="submenu-vacinacao-registrar-ato">Efetuar vacinação</a>
        </li>
         <li>
          <a href="${basePath}pages/vacinacao/listarVacinacoes.html" class="sidebar-submenu-item" id="submenu-vacinacao-listar-atos">Histórico de vacinação</a>
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
        <a href="${basePath}pages/pessoas/listarAdotante.html" class="sidebar-submenu-item" id="submenu-pessoas-gerenciar">Gerenciar Adotante</a>
        <a href="${basePath}pages/doacao/listarDoacao.html" class="sidebar-submenu-item" id="submenu-pessoas-cadastrar">Gerenciar Adoção</a>
      </ul>
      
      <div class="sidebar-item" id="menu-produtos" onclick="toggleSubmenu('produtosSubmenu')">
        <div class="sidebar-item-content">
          <i class="bi bi-plus-circle sidebar-item-icon"></i>
          <span class="sidebar-item-text">Produtos</span>
        </div>
        <i class="bi bi-chevron-down sidebar-item-arrow"></i>
      </div>
      <ul id="produtosSubmenu" class="sidebar-submenu">
        <a href="${basePath}pages/produto/listar.html" class="sidebar-submenu-item" id="submenu-produtos-listar">Lista de Produtos</a>
        <a href="${basePath}pages/produto/novo.html" class="sidebar-submenu-item" id="submenu-produtos-novo">Novo Produto</a>
        <a href="${basePath}pages/produto/categorias.html" class="sidebar-submenu-item" id="submenu-produtos-categorias">Lista de Categorias</a>
        <a href="${basePath}pages/produto/tipo/listarTipos.html" class="sidebar-submenu-item" id="submenu-produtos-tipos">Tipos de Produto</a>
        <a href="${basePath}pages/produto/unidade/listarUnidades.html" class="sidebar-submenu-item" id="submenu-produtos-unidades">Unidades de Medida</a>
      </ul>
      
      <a href="${basePath}pages/acerto-estoque/listarAcertosEstoque.html" class="sidebar-item" id="menu-estoque">
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
  const basePath = getBasePath();

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
    { id: "menu-home", pathCheck: (path) => path === basePath || path === `${basePath}index.html` },
    { id: "menu-animais", pathCheck: (path) => path.startsWith(`${basePath}pages/animal/`) || path.startsWith(`${basePath}pages/eventos/`), submenuId: "animaisSubmenu",
      subItems: [
        { id: "submenu-animais-gerenciar", pathCheck: (path) => path.endsWith("gerenciarAnimal.html")},
        { id: "submenu-eventos-gerenciar", pathCheck: (path) => path.endsWith("gerenciarEvento.html")}
      ]
    },
    { id: "menu-medicacao", pathCheck: (path) => path.startsWith(`${basePath}pages/medicamentos/`), submenuId: "medicacaoSubmenu",
      subItems: [
        { id: "submenu-medicacao-gerenciar", pathCheck: (path) => path.endsWith("listarMedicamentos.html")},
        { id: "submenu-medicacao-efetuar", pathCheck: (path) => path.endsWith("efetuarMedicacao.html")},
        { id: "submenu-medicacao-historico", pathCheck: (path) => path.endsWith("historicoMedicacoes.html")}
      ]
    },
    // Vacinação Submenu AJUSTADO AQUI
    { id: "menu-vacinacao", pathCheck: (path) => path.startsWith(`${basePath}pages/vacina/`) || path.startsWith(`${basePath}pages/vacinacao/`), submenuId: "vacinacaoSubmenu",
      subItems: [
        { id: "submenu-vacina-gerenciar-tipos", pathCheck: (path) => path.endsWith("listarVacinas.html")}, // Para tipos de vacina
        { id: "submenu-vacina-cadastrar-tipo", pathCheck: (path) => path.endsWith("cadastrarVacina.html")}, // Para tipos de vacina
        { id: "submenu-vacinacao-registrar-ato", pathCheck: (path) => path.endsWith("cadastrarVacinacao.html")}, // Para o ATO de vacinar
        { id: "submenu-vacinacao-listar-atos", pathCheck: (path) => path.endsWith("listarVacinacoes.html")} // Para o ATO de vacinar (histórico)
      ]
    },
    { id: "menu-pessoas", pathCheck: (path) => path.startsWith(`${basePath}pages/pessoas/`) || path.startsWith(`${basePath}pages/doacao/`), submenuId: "pessoasSubmenu",
      subItems: [
        { id: "submenu-pessoas-gerenciar", pathCheck: (path) => path.endsWith("listarAdotante.html")},
        { id: "submenu-pessoas-cadastrar", pathCheck: (path) => path.endsWith("listarDoacao.html")}
      ]
    },
    { id: "menu-produtos", pathCheck: (path) => path.startsWith(`${basePath}pages/produto/`), submenuId: "produtosSubmenu",
      subItems: [
        { id: "submenu-produtos-listar", pathCheck: (path) => path.endsWith("/produto/listar.html")},
        { id: "submenu-produtos-novo", pathCheck: (path) => path.endsWith("/produto/novo.html")},
        { id: "submenu-produtos-categorias", pathCheck: (path) => path.endsWith("/produto/categorias.html")},
        { id: "submenu-produtos-tipos", pathCheck: (path) => path.startsWith(`${basePath}pages/produto/tipo/`)},
        { id: "submenu-produtos-unidades", pathCheck: (path) => path.startsWith(`${basePath}pages/produto/unidade/`)}
      ]
    },
    { id: "menu-estoque", pathCheck: (path) => path.startsWith(`${basePath}pages/acerto-estoque/`) }
  ];

  for (const menuItem of menuItems) {
    const menuElement = document.getElementById(menuItem.id);
    // Normaliza o currentPath para remover a barra final se houver e não for a raiz
    const normalizedCurrentPath = currentPath.endsWith('/') && currentPath.length > 1 ? currentPath.slice(0, -1) : currentPath;

    if (menuElement && menuItem.pathCheck(normalizedCurrentPath)) {
      menuElement.classList.add("active");
      activeParentFound = true;
      if (menuItem.submenuId) {
        const submenuElement = document.getElementById(menuItem.submenuId);
        if (submenuElement) {
          let openSubmenu = false;
          if (menuItem.subItems) {
            for (const subItem of menuItem.subItems) {
              const subItemElement = document.getElementById(subItem.id);
              // Para pathCheck dos subitens, passamos o currentPath original pois pathCheck pode ter lógicas de endsWith
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

  if (!activeParentFound) {
    const homePathAbsolute = basePath.endsWith('/') ? basePath + 'index.html' : basePath + '/index.html';
    const homePathSimple = basePath.endsWith('/') ? basePath.slice(0, -1) : basePath;

    const normalizedCurrentPathForHome = currentPath.endsWith('/') && currentPath.length > 1 ? currentPath.slice(0, -1) : currentPath;
    const normalizedCurrentPathIndexForHome = normalizedCurrentPathForHome.endsWith('/index.html') ? normalizedCurrentPathForHome.slice(0, -'/index.html'.length) : normalizedCurrentPathForHome;

    if (normalizedCurrentPathIndexForHome === homePathSimple || normalizedCurrentPathForHome === homePathAbsolute || (basePath === "/" && (currentPath === "/" || currentPath === "/index.html"))) {
      const homeMenu = document.getElementById("menu-home");
      if (homeMenu) homeMenu.classList.add("active");
    }
  }
}

function getBasePath() {
  // Assumindo que o context path do Spring Boot é "/"
  // Se os links são construídos como `${basePath}pages/...` e devem ser absolutos do servidor.
  const contextPath = "/";
  return contextPath;
}