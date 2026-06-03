/* ============================================
   BACKOFFICE MICROFINANCE - Shared UI kernel
   ============================================ */

(function () {
  const pageName = (window.location.pathname.split('/').pop() || 'dashboard.html').toLowerCase();

  const icons = {
    dashboard: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>',
    users: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>',
    card: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>',
    cash: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="6" width="20" height="12" rx="2"/><circle cx="12" cy="12" r="2"/><path d="M6 12h.01M18 12h.01"/></svg>',
    transfer: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>',
    credit: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>',
    settings: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06A1.65 1.65 0 0 0 15 19.4a1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.6 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.6a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09A1.65 1.65 0 0 0 15 4.6a1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9c.14.31.22.65.22 1H21a2 2 0 0 1 0 4h-1.09c0 .35-.08.69-.22 1z"/></svg>',
    audit: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>',
    logout: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>',
    menu: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>',
    bell: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>',
    eye: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>',
    eyeOff: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 20C5 20 1 12 1 12a20.29 20.29 0 0 1 5.06-5.94"/><path d="M9.9 4.24A10.45 10.45 0 0 1 12 4c7 0 11 8 11 8a20.55 20.55 0 0 1-3.06 4.44"/><line x1="1" y1="1" x2="23" y2="23"/></svg>'
  };

  const navGroups = [
    {
      title: 'Clients',
      items: [
        { href: 'clients.html', label: 'Liste clients', icon: icons.users, badge: '248', match: ['clients.html'] },
        { href: 'client-create.html', label: 'Nouveau client', icon: icons.users, match: ['client-create.html'] },
        { href: 'client-detail.html', label: 'Fiche client', icon: icons.users, match: ['client-detail.html'] }
      ]
    },
    {
      title: 'Comptes & Cartes',
      items: [
        { href: 'comptes.html', label: 'Comptes', icon: icons.card, match: ['comptes.html'] },
        { href: 'paiement-carte.html', label: 'Paiement carte', icon: icons.card, match: ['paiement-carte.html'] }
      ]
    },
    {
      title: 'Caisse & Opérations',
      items: [
        { href: 'caisse.html', label: 'Caisse', icon: icons.cash, match: ['caisse.html'] },
        { href: 'guichet.html', label: 'Guichet', icon: icons.cash, match: ['guichet.html'] },
        { href: 'versement.html', label: 'Versement', icon: icons.transfer, match: ['versement.html'] },
        { href: 'virement.html', label: 'Virement', icon: icons.transfer, match: ['virement.html'] },
        { href: 'validation.html', label: 'Validation 4-eyes', icon: icons.audit, badge: '12', match: ['validation.html'] },
        { href: 'historique.html', label: 'Historique', icon: icons.audit, match: ['historique.html'] }
      ]
    },
    {
      title: 'Crédits',
      items: [
        { href: 'credit-simulation.html', label: 'Simulation', icon: icons.credit, match: ['credit-simulation.html'] },
        { href: 'credit-demandes.html', label: 'Demandes', icon: icons.credit, badge: '7', match: ['credit-demandes.html'] },
        { href: 'credit-detail.html', label: 'Dossier crédit', icon: icons.credit, match: ['credit-detail.html'] }
      ]
    },
    {
      title: 'Paramétrage',
      items: [
        { href: 'produits.html', label: 'Produits', icon: icons.settings, match: ['produits.html'] },
        { href: 'agences.html', label: 'Agences', icon: icons.settings, match: ['agences.html'] },
        { href: 'cache.html', label: 'Cache tarification', icon: icons.settings, match: ['cache.html'] }
      ]
    },
    {
      title: 'Pilotage & Audit',
      items: [
        { href: 'dashboard.html', label: 'Tableau de bord', icon: icons.dashboard, match: ['dashboard.html'] },
        { href: 'direction.html', label: 'Direction', icon: icons.dashboard, match: ['direction.html'] },
        { href: 'agios.html', label: 'Agios', icon: icons.credit, match: ['agios.html'] },
        { href: 'audit.html', label: "Journal d'audit", icon: icons.audit, match: ['audit.html'] }
      ]
    }
  ];

  function isActive(item) {
    return item.match.includes(pageName);
  }

  function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
  }

  function showToast(message, type = 'success') {
    const container = document.querySelector('.toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const toastIcons = {
      success: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
      error: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
      warning: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
      info: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>'
    };

    toast.innerHTML = `${toastIcons[type] || toastIcons.success}<span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  }

  function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.add('active');
    modal.setAttribute('aria-modal', 'true');
    modal.setAttribute('role', 'dialog');
    document.body.style.overflow = 'hidden';
  }

  function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.remove('active');
    modal.removeAttribute('aria-modal');
    document.body.style.overflow = '';
  }

  function renderSidebar() {
    const sidebar = document.querySelector('.app-layout > .sidebar');
    if (!sidebar) return;

    const groups = navGroups.map(group => `
      <div class="nav-section-title">${group.title}</div>
      ${group.items.map(item => `
        <a href="${item.href}" class="nav-item ${isActive(item) ? 'active' : ''}" ${isActive(item) ? 'aria-current="page"' : ''}>
          ${item.icon}
          <span>${item.label}</span>
          ${item.badge ? `<span class="nav-badge">${item.badge}</span>` : ''}
        </a>
      `).join('')}
    `).join('');

    sidebar.innerHTML = `
      <div class="sidebar-header">
        <img src="../assets/img/logo.png" alt="Logo MicroFinance" class="sidebar-logo">
        <div class="sidebar-brand">MicroFinance</div>
        <div class="sidebar-subtitle">Backoffice</div>
      </div>
      <div class="sidebar-user">
        <div class="sidebar-avatar">AD</div>
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">Admin Principal</div>
          <div class="sidebar-user-role">Superviseur</div>
        </div>
      </div>
      <nav class="sidebar-nav" aria-label="Navigation backoffice">${groups}</nav>
      <div class="sidebar-footer">
        <a href="login.html" class="nav-item">${icons.logout}<span>Déconnexion</span></a>
      </div>
    `;
  }

  function enhanceTopbar() {
    const topbar = document.querySelector('.topbar');
    if (!topbar) return;

    const left = topbar.querySelector('.topbar-left');
    if (left && !left.querySelector('.mobile-sidebar-toggle')) {
      const toggle = document.createElement('button');
      toggle.type = 'button';
      toggle.className = 'topbar-icon-btn mobile-sidebar-toggle';
      toggle.setAttribute('aria-label', 'Ouvrir le menu');
      toggle.innerHTML = icons.menu;
      left.prepend(toggle);
    }

    const right = topbar.querySelector('.topbar-right');
    if (right && !right.querySelector('.topbar-context')) {
      const context = document.createElement('div');
      context.className = 'topbar-context';
      context.innerHTML = `
        <span class="topbar-chip">Agence Plateau</span>
        <span class="topbar-chip">Caisse C-01</span>
        <span class="topbar-chip">Admin Principal</span>
        <a class="topbar-chip topbar-link" href="login.html">Déco</a>
      `;
      right.prepend(context);
    }
  }

  function initMobileSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.mobile-sidebar-toggle');
    if (!sidebar || !toggle) return;

    toggle.addEventListener('click', () => {
      sidebar.classList.toggle('is-open');
      toggle.setAttribute('aria-expanded', sidebar.classList.contains('is-open') ? 'true' : 'false');
    });

    document.addEventListener('click', event => {
      const clickedOutside = !sidebar.contains(event.target) && !toggle.contains(event.target);
      if (clickedOutside) sidebar.classList.remove('is-open');
    });
  }

  function initModalEvents() {
    document.addEventListener('click', event => {
      if (event.target.classList.contains('modal-overlay')) {
        event.target.classList.remove('active');
        document.body.style.overflow = '';
      }
    });

    document.addEventListener('keydown', event => {
      if (event.key !== 'Escape') return;
      document.querySelectorAll('.modal-overlay.active').forEach(modal => modal.classList.remove('active'));
      document.body.style.overflow = '';
    });
  }

  function validateStep(stepContent) {
    const requiredFields = Array.from(stepContent.querySelectorAll('[required]'));
    const invalid = requiredFields.filter(field => !field.value.trim());
    invalid.forEach(field => {
      field.classList.add('is-invalid');
      const message = field.closest('.form-group')?.querySelector('.field-error');
      if (message) message.classList.add('is-visible');
    });
    if (invalid.length) {
      invalid[0].focus();
      showToast('Veuillez compléter les champs obligatoires de cette étape.', 'warning');
      return false;
    }
    return true;
  }

  function initStepper() {
    const stepper = document.querySelector('.stepper');
    if (!stepper) return;

    const steps = Array.from(stepper.querySelectorAll('.step'));
    const stepContents = Array.from(document.querySelectorAll('.step-content'));
    let currentStep = 0;

    function showStep(index) {
      steps.forEach((step, i) => {
        step.classList.toggle('completed', i < index);
        step.classList.toggle('active', i === index);
      });
      stepContents.forEach((content, i) => {
        content.style.display = i === index ? 'block' : 'none';
      });
      currentStep = index;
      try {
        localStorage.setItem(`${pageName}:step`, String(index));
      } catch (_) {}
    }

    let savedStep = 0;
    try {
      savedStep = Number(localStorage.getItem(`${pageName}:step`) || 0);
    } catch (_) {}
    showStep(Number.isNaN(savedStep) ? 0 : Math.min(savedStep, steps.length - 1));

    document.querySelector('.btn-next-step')?.addEventListener('click', () => {
      if (currentStep >= steps.length - 1) return;
      if (stepContents[currentStep] && !validateStep(stepContents[currentStep])) return;
      showStep(currentStep + 1);
    });

    document.querySelector('.btn-prev-step')?.addEventListener('click', () => {
      if (currentStep > 0) showStep(currentStep - 1);
    });

    steps.forEach((step, index) => {
      step.addEventListener('click', () => {
        if (index <= currentStep || step.classList.contains('completed')) showStep(index);
      });
    });
  }

  function initFilterTabs() {
    document.querySelectorAll('.filter-tabs').forEach(tabGroup => {
      const tabs = Array.from(tabGroup.querySelectorAll('.filter-tab'));
      tabs.forEach(tab => {
        tab.addEventListener('click', () => {
          tabs.forEach(item => item.classList.remove('active'));
          tab.classList.add('active');
        });
      });
    });
  }

  function formatCurrency(amount) {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XOF',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }

  function formatDate(date) {
    return new Intl.DateTimeFormat('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(date));
  }

  function initSearchPredictive(inputSelector, resultsSelector, data) {
    const input = document.querySelector(inputSelector);
    const results = document.querySelector(resultsSelector);
    if (!input || !results) return;

    input.addEventListener('input', function () {
      const query = this.value.toLowerCase().trim();
      if (query.length < 2) {
        results.classList.remove('show');
        return;
      }

      const filtered = data.filter(item =>
        item.name.toLowerCase().includes(query) ||
        item.id.toLowerCase().includes(query)
      );

      results.innerHTML = filtered.map(item => `
        <div class="search-result-item" data-id="${item.id}">
          <strong>${item.name}</strong> - ${item.id}
          ${item.balance ? `<span style="float:right;color:#084355;font-weight:600;">${formatCurrency(item.balance)}</span>` : ''}
        </div>
      `).join('');

      results.classList.add('show');
      results.querySelectorAll('.search-result-item').forEach(item => {
        item.addEventListener('click', () => {
          input.value = item.querySelector('strong').textContent;
          results.classList.remove('show');
        });
      });
    });

    document.addEventListener('click', event => {
      if (!input.contains(event.target) && !results.contains(event.target)) {
        results.classList.remove('show');
      }
    });
  }

  function initSortableTables() {
    document.querySelectorAll('.data-table').forEach(table => {
      const headers = Array.from(table.querySelectorAll('thead th'));
      headers.forEach((header, index) => {
        const label = header.textContent.trim().toLowerCase();
        if (!label || label.includes('action')) return;
        header.dataset.sortable = 'true';
        header.addEventListener('click', () => sortTable(table, header, index));
      });
    });
  }

  function sortTable(table, header, columnIndex) {
    const tbody = table.querySelector('tbody');
    if (!tbody) return;
    const direction = header.classList.contains('sorted-asc') ? 'desc' : 'asc';
    const rows = Array.from(tbody.querySelectorAll('tr'));

    table.querySelectorAll('th').forEach(th => th.classList.remove('sorted-asc', 'sorted-desc'));
    header.classList.add(direction === 'asc' ? 'sorted-asc' : 'sorted-desc');

    rows.sort((a, b) => {
      const aText = a.children[columnIndex]?.innerText.trim() || '';
      const bText = b.children[columnIndex]?.innerText.trim() || '';
      return direction === 'asc'
        ? aText.localeCompare(bText, 'fr', { numeric: true })
        : bText.localeCompare(aText, 'fr', { numeric: true });
    });

    rows.forEach(row => tbody.appendChild(row));
  }

  function initExportButtons() {
    document.querySelectorAll('.export-btn').forEach(button => {
      button.addEventListener('click', () => {
        const label = button.textContent.trim().toUpperCase();
        if (label.includes('CSV')) {
          exportNearestTable(button);
        } else {
          showToast('Export PDF préparé côté interface. Le backend générera le document officiel.', 'info');
        }
      });
    });
  }

  function exportNearestTable(button) {
    const scope = button.closest('.content-area') || document;
    const table = scope.querySelector('.data-table');
    if (!table) {
      showToast('Aucune table à exporter sur cette page.', 'warning');
      return;
    }

    const rows = Array.from(table.querySelectorAll('tr')).map(row =>
      Array.from(row.children).map(cell => `"${cell.innerText.replaceAll('"', '""').trim()}"`).join(',')
    );
    const blob = new Blob([rows.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${pageName.replace('.html', '')}-export.csv`;
    link.click();
    URL.revokeObjectURL(link.href);
    showToast('Export CSV généré.', 'success');
  }

  function initPasswordToggles() {
    document.querySelectorAll('input[type="password"]').forEach(input => {
      const wrapper = input.closest('.input-icon') || input.parentElement;
      if (!wrapper || wrapper.querySelector('.password-toggle')) return;
      wrapper.classList.add('has-action');
      const button = document.createElement('button');
      button.type = 'button';
      button.className = 'password-toggle';
      button.setAttribute('aria-label', 'Afficher le mot de passe');
      button.innerHTML = icons.eye;
      button.addEventListener('click', () => {
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        button.innerHTML = isPassword ? icons.eyeOff : icons.eye;
        button.setAttribute('aria-label', isPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe');
      });
      wrapper.appendChild(button);
    });
  }

  function initOtp() {
    const form = document.querySelector('#otpForm');
    if (!form) return;
    const inputs = Array.from(form.querySelectorAll('.otp-input'));
    const submit = form.querySelector('[type="submit"]');
    const timer = document.querySelector('[data-otp-timer]');
    let secondsLeft = 272;

    function syncSubmitState() {
      if (submit) submit.disabled = inputs.some(input => input.value.length !== 1);
    }

    inputs.forEach((input, index) => {
      input.addEventListener('input', () => {
        input.value = input.value.replace(/\D/g, '').slice(0, 1);
        if (input.value && inputs[index + 1]) inputs[index + 1].focus();
        syncSubmitState();
      });
      input.addEventListener('keydown', event => {
        if (event.key === 'Backspace' && !input.value && inputs[index - 1]) inputs[index - 1].focus();
      });
      input.addEventListener('paste', event => {
        event.preventDefault();
        const value = event.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
        value.split('').forEach((digit, i) => {
          if (inputs[i]) inputs[i].value = digit;
        });
        inputs[Math.min(value.length, inputs.length) - 1]?.focus();
        syncSubmitState();
      });
    });

    const tick = setInterval(() => {
      secondsLeft -= 1;
      if (timer) {
        const minutes = Math.floor(secondsLeft / 60);
        const seconds = String(secondsLeft % 60).padStart(2, '0');
        timer.textContent = `Code valide encore ${minutes}:${seconds}`;
      }
      if (secondsLeft <= 0) {
        clearInterval(tick);
        inputs.forEach(input => input.disabled = true);
        if (timer) timer.textContent = 'Code expiré - Renvoyez un nouveau code';
        showToast('Code expiré. Renvoyez un nouveau code.', 'warning');
      }
    }, 1000);

    form.addEventListener('submit', event => {
      event.preventDefault();
      const code = inputs.map(input => input.value).join('');
      if (code === '123456') {
        showToast('Vérification réussie.', 'success');
        setTimeout(() => { window.location.href = 'dashboard.html'; }, 700);
      } else {
        form.querySelector('.otp-inputs')?.classList.add('is-shaking');
        setTimeout(() => form.querySelector('.otp-inputs')?.classList.remove('is-shaking'), 320);
        showToast('Code incorrect.', 'error');
      }
    });

    syncSubmitState();
    inputs[0]?.focus();
  }

  function initUploadZones() {
    document.querySelectorAll('.upload-zone').forEach(zone => {
      const input = zone.querySelector('input[type="file"]');
      if (!input) return;
      const preview = zone.querySelector('.upload-preview') || document.createElement('div');
      preview.className = 'upload-preview';
      if (!preview.parentElement) zone.appendChild(preview);

      ['dragenter', 'dragover'].forEach(eventName => {
        zone.addEventListener(eventName, event => {
          event.preventDefault();
          zone.classList.add('is-dragover');
        });
      });

      ['dragleave', 'drop'].forEach(eventName => {
        zone.addEventListener(eventName, event => {
          event.preventDefault();
          zone.classList.remove('is-dragover');
        });
      });

      zone.addEventListener('drop', event => {
        input.files = event.dataTransfer.files;
        renderFiles(input.files, preview);
      });

      input.addEventListener('change', () => renderFiles(input.files, preview));
    });
  }

  function renderFiles(files, preview) {
    preview.innerHTML = Array.from(files).map(file => `<span class="upload-file">${file.name}</span>`).join('');
  }

  function initFormDrafts() {
    document.querySelectorAll('form[data-autosave]').forEach(form => {
      const key = `${pageName}:${form.id || 'form'}:draft`;
      try {
        const saved = JSON.parse(localStorage.getItem(key) || '{}');
        Object.entries(saved).forEach(([name, value]) => {
          const field = form.elements[name];
          if (field && field.type !== 'file') field.value = value;
        });
      } catch (_) {}

      form.addEventListener('input', () => {
        const data = {};
        Array.from(form.elements).forEach(field => {
          if (field.name && field.type !== 'file') data[field.name] = field.value;
        });
        try {
          localStorage.setItem(key, JSON.stringify(data));
        } catch (_) {}
      });
    });
  }

  function initFieldValidation() {
    document.addEventListener('input', event => {
      if (!event.target.classList?.contains('is-invalid')) return;
      if (event.target.value.trim()) {
        event.target.classList.remove('is-invalid');
        event.target.closest('.form-group')?.querySelector('.field-error')?.classList.remove('is-visible');
      }
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    renderSidebar();
    enhanceTopbar();
    initMobileSidebar();
    initModalEvents();
    initStepper();
    initFilterTabs();
    initSortableTables();
    initExportButtons();
    initPasswordToggles();
    initOtp();
    initUploadZones();
    initFormDrafts();
    initFieldValidation();
  });

  window.showToast = showToast;
  window.openModal = openModal;
  window.closeModal = closeModal;
  window.initStepper = initStepper;
  window.initFilterTabs = initFilterTabs;
  window.formatCurrency = formatCurrency;
  window.formatDate = formatDate;
  window.initSearchPredictive = initSearchPredictive;
})();

