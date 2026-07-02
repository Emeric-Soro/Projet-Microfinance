/* ============================================
   API CLIENT — Soutra Finance E-Banking Portal
   Couche centralisée : token, fetch, session
   ============================================ */

(function () {
  'use strict';

  var API_BASE = 'http://192.168.195.25:8080';

  /* ── Gestion du token & session ─────────────────────── */

  function getToken() {
    return localStorage.getItem('sf_client_token') || null;
  }

  function setSession(data) {
    if (data.token) {
      localStorage.setItem('sf_client_token', data.token);
      if (data.refreshToken) localStorage.setItem('sf_client_refresh_token', data.refreshToken);
      if (data.utilisateur) localStorage.setItem('sf_client_user', JSON.stringify(data.utilisateur));
    } else {
      clearSession();
    }
  }

  function clearSession() {
    localStorage.removeItem('sf_client_token');
    localStorage.removeItem('sf_client_refresh_token');
    localStorage.removeItem('sf_client_user');
  }

  function getUser() {
    var u = localStorage.getItem('sf_client_user');
    return u ? JSON.parse(u) : null;
  }

  /* ── Intercepteur Fetch centralisé ──────────────────── */

  async function apiFetch(path, options) {
    options = options || {};
    options.headers = options.headers || {};

    var token = getToken();
    var isPublic = path.includes('/auth/login') || 
                   path.includes('/clients') || 
                   path.includes('/utilisateurs') || 
                   path.includes('/auth/mot-de-passe/');
    if (token && !isPublic) {
      options.headers['Authorization'] = 'Bearer ' + token;
    }

    if (!(options.body instanceof FormData) && !options.headers['Content-Type']) {
      options.headers['Content-Type'] = 'application/json';
    }

    var url = API_BASE + path;
    var res = await fetch(url, options);

    if (res.status === 401 && path !== '/api/v1/mobile/auth/login') {
      // Rediriger vers login si session expirée
      clearSession();
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/pages/login';
      }
    }

    return res;
  }

  /* ── Helper de dé-wrappe des réponses API ───────────── */

  /**
   * Dé-enveloppe une réponse API si elle utilise le format
   * { status: 'success', data: [...] }, sinon retourne l'objet tel quel.
   * Certains endpoints mobiles (dashboard, profil) retournent du JSON plat,
   * d'autres (comptes, crédits, réclamations) utilisent l'enveloppe ApiEnvelope.
   */
  function unwrapApiData(res) {
    if (res && typeof res === 'object' && res.status === 'success' && res.hasOwnProperty('data')) {
      return res.data;
    }
    return res;
  }

  if (typeof window !== 'undefined') {
    window.unwrapApiData = unwrapApiData;
  }

  /* ── Services E-Banking Client ──────────────────────── */

  var ClientAuth = {
    login: async function (login, password) {
      return await apiFetch('/api/v1/mobile/auth/login', {
        method: 'POST',
        body: JSON.stringify({ login: login, motDePasse: password })
      });
    },
    loginOtp: async function (login, challengeId, codeOtp) {
      return await apiFetch('/api/v1/mobile/auth/login/otp', {
        method: 'POST',
        body: JSON.stringify({ login: login, challengeId: challengeId, codeOtp: codeOtp })
      });
    },
    logout: async function () {
      var res = await apiFetch('/api/v1/mobile/auth/logout', { method: 'POST' });
      clearSession();
      return res;
    },
    signup: async function (clientData) {
      // Étape 1 : Créer le client (public)
      return await apiFetch('/api/v1/clients', {
        method: 'POST',
        body: JSON.stringify(clientData)
      });
    },
    createUserAccess: async function (codeClient, email, dateNaissance, password) {
      // Étape 2 : Créer le compte utilisateur web (public)
      return await apiFetch('/api/v1/utilisateurs', {
        method: 'POST',
        body: JSON.stringify({
          codeClient: codeClient,
          email: email,
          dateNaissance: dateNaissance,
          motDePasseBrut: password
        })
      });
    }
  };

  var ClientDashboard = {
    getOverview: async function () {
      return await apiFetch('/api/v1/mobile/dashboard');
    }
  };

  var ClientComptes = {
    lister: async function () {
      return await apiFetch('/api/v1/mobile/comptes');
    },
    detail: async function (idCompte) {
      return await apiFetch('/api/v1/mobile/comptes/' + idCompte);
    },
    operations: async function (idCompte, page, size) {
      page = page || 0;
      size = size || 10;
      return await apiFetch('/api/v1/mobile/comptes/' + idCompte + '/operations?page=' + page + '&size=' + size);
    },
    releve: async function (idCompte) {
      return await apiFetch('/api/v1/mobile/comptes/' + idCompte + '/releve');
    }
  };

  var ClientVirements = {
    initier: async function (compteSource, compteDestination, montant) {
      return await apiFetch('/api/v1/mobile/virements', {
        method: 'POST',
        body: JSON.stringify({
          compteSource: compteSource,
          compteDestination: compteDestination,
          montant: montant
        })
      });
    },
    confirmerOtp: async function (reference, codeOtp) {
      return await apiFetch('/api/v1/mobile/virements/' + reference + '/confirmer-otp', {
        method: 'POST',
        body: JSON.stringify({ codeOtp: codeOtp })
      });
    },
    annuler: async function (reference) {
      return await apiFetch('/api/v1/mobile/virements/' + reference + '/annuler', {
        method: 'POST'
      });
    },
    listerBeneficiaires: async function () {
      return await apiFetch('/api/v1/mobile/beneficiaires');
    },
    ajouterBeneficiaire: async function (nom, prenom, compteBeneficiaire, banque) {
      return await apiFetch('/api/v1/mobile/beneficiaires', {
        method: 'POST',
        body: JSON.stringify({
          nom: nom,
          prenom: prenom,
          compteBeneficiaire: compteBeneficiaire,
          banque: banque || 'Soutra Finance'
        })
      });
    }
  };

  var ClientCredits = {
    lister: async function (page, size) {
      page = page || 0;
      size = size || 10;
      return await apiFetch('/api/v1/mobile/credits?page=' + page + '&size=' + size);
    },
    detail: async function (idCredit) {
      return await apiFetch('/api/v1/mobile/credits/' + idCredit);
    },
    echeancier: async function (idCredit) {
      return await apiFetch('/api/v1/mobile/credits/' + idCredit + '/echeancier');
    },
    simuler: async function (montant, tauxAnnuel, dureeMois, methodeCalcul) {
      return await apiFetch('/api/v1/mobile/credits/simulation', {
        method: 'POST',
        body: JSON.stringify({
          montant: montant,
          tauxAnnuel: tauxAnnuel,
          dureeMois: dureeMois,
          methodeCalcul: methodeCalcul || 'DEGRESSIF'
        })
      });
    },
    soumettreDemande: async function (codeProduit, montant, dureeMois, objetCredit) {
      return await apiFetch('/api/v1/mobile/credits/demandes', {
        method: 'POST',
        body: JSON.stringify({
          codeProduitCredit: codeProduit,
          montantDemande: montant,
          dureeSouhaitee: dureeMois,
          objetCredit: objetCredit
        })
      });
    },
    listerDemandes: async function (page, size) {
      page = page || 0;
      size = size || 10;
      return await apiFetch('/api/v1/mobile/credits/demandes?page=' + page + '&size=' + size);
    }
  };

  var ClientReclamations = {
    lister: async function () {
      return await apiFetch('/api/v1/mobile/reclamations');
    },
    creer: async function (typeReclamation, description) {
      return await apiFetch('/api/v1/mobile/reclamations', {
        method: 'POST',
        body: JSON.stringify({
          typeReclamation: typeReclamation,
          description: description
        })
      });
    },
    detail: async function (idReclamation) {
      return await apiFetch('/api/v1/mobile/reclamations/' + idReclamation);
    }
  };

  var ClientEpargne = {
    listerComptes: async function () {
      return await apiFetch('/api/v1/mobile/epargne/comptes');
    },
    detailCompte: async function (idCompte) {
      return await apiFetch('/api/v1/mobile/epargne/comptes/' + idCompte);
    },
    listerDats: async function () {
      return await apiFetch('/api/v1/mobile/epargne/dats');
    },
    detailDat: async function (idDat) {
      return await apiFetch('/api/v1/mobile/epargne/dats/' + idDat);
    }
  };

  var ClientProfil = {
    consulterProfil: async function () {
      return await apiFetch('/api/v1/mobile/profil');
    },
    consulterKyc: async function () {
      return await apiFetch('/api/v1/mobile/profil/kyc');
    },
    mettreAJourKyc: async function (profession, secteurActivite, revenuMensuel) {
      return await apiFetch('/api/v1/mobile/profil/kyc', {
        method: 'PUT',
        body: JSON.stringify({
          profession: profession,
          secteurActivite: secteurActivite,
          revenuMensuel: revenuMensuel
        })
      });
    },
    enregistrerDocumentKyc: async function (typeDocument, nomFichier, contenuBase64) {
      return await apiFetch('/api/v1/mobile/profil/documents', {
        method: 'POST',
        body: JSON.stringify({
          typeDocument: typeDocument,
          nomFichier: nomFichier,
          contenuBase64: contenuBase64
        })
      });
    }
  };

  /* ── Exposition globale ─────────────────────────── */

  window.SF_Client = { API_BASE, getToken, setSession, clearSession, getUser, apiFetch };
  window.ClientAuth = ClientAuth;
  window.ClientDashboard = ClientDashboard;
  window.ClientComptes = ClientComptes;
  window.ClientVirements = ClientVirements;
  window.ClientCredits = ClientCredits;
  window.ClientEpargne = ClientEpargne;
  window.ClientReclamations = ClientReclamations;
  window.ClientProfil = ClientProfil;

}());

/* ============================================
   SHARED UI MECHANISMS FOR CLIENT PORTAL
   ============================================ */

(function () {
  'use strict';

  // Session verification for secure pages
  var path = window.location.pathname.toLowerCase();
  var isPublicPage = path.includes('/login') || 
                     path.includes('/register') || 
                     path.includes('/otp') || 
                     path.endsWith('/') || 
                     path.endsWith('/index.html') ||
                     path.endsWith('/clientportal');
  
  if (!isPublicPage && !window.SF_Client.getToken()) {
    window.location.href = '/pages/login';
  }

  // Active navigation highlight & Layout injection helper
  document.addEventListener('DOMContentLoaded', function () {
    var logoutBtn = document.getElementById('portalLogoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', async function (e) {
        e.preventDefault();
        try {
          await window.ClientAuth.logout();
        } catch (err) {
          console.error(err);
        }
        window.SF_Client.clearSession();
        window.location.href = '/pages/login';
      });
    }

    // Display client username if layout has user-name container
    var user = window.SF_Client.getUser();
    var nameEl = document.getElementById('portalUserName');
    if (nameEl && user) {
      nameEl.textContent = (user.prenom || '') + ' ' + (user.nom || '');
      
      // Update avatar if photo is present
      var avatarEl = document.querySelector('.sidebar-user .user-avatar');
      if (avatarEl) {
        var setAvatarImg = function (url) {
          var img = document.createElement('img');
          img.src = window.SF_Client.API_BASE + '/' + url;
          img.alt = 'Avatar';
          img.style.width = '100%';
          img.style.height = '100%';
          img.style.objectFit = 'cover';
          img.style.borderRadius = '50%';
          img.style.display = 'block';
          
          var originalSvg = avatarEl.innerHTML;
          img.onerror = function() {
            avatarEl.innerHTML = originalSvg;
          };
          avatarEl.innerHTML = '';
          avatarEl.appendChild(img);
        };

        if (user.photoIdentiteUrl) {
          setAvatarImg(user.photoIdentiteUrl);
        } else {
          window.ClientProfil.consulterProfil().then(async function (res) {
            if (res.ok) {
              var prof = await res.json();
              if (prof.photoIdentiteUrl) {
                user.photoIdentiteUrl = prof.photoIdentiteUrl;
                localStorage.setItem('sf_client_user', JSON.stringify(user));
                setAvatarImg(prof.photoIdentiteUrl);
              }
            }
          }).catch(console.error);
        }
      }
    }
  });

  // Global premium toast notification system
  window.showToast = function (message, type) {
    type = type || 'success';
    var container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    var toast = document.createElement('div');
    toast.className = 'toast ' + type;
    
    var icon = '✓';
    if (type === 'error') icon = '✕';
    else if (type === 'warning') icon = '⚠';
    else if (type === 'info') icon = 'ℹ';

    toast.innerHTML = '<span class="toast-icon">' + icon + '</span><span class="toast-message">' + message + '</span>';
    container.appendChild(toast);

    setTimeout(function () { toast.classList.add('show'); }, 10);
    setTimeout(function () {
      toast.classList.remove('show');
      setTimeout(function () { toast.remove(); }, 300);
    }, 4000);
  };

  window.showConfirm = function(title, message, options = {}) {
    const {
      confirmText = 'Confirmer',
      cancelText = 'Annuler',
      confirmClass = 'btn-danger',
      icon = 'warning'
    } = options;

    return new Promise((resolve) => {
      const previousActiveElement = document.activeElement;
      const titleId = `custom-modal-title-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
      const overlay = document.createElement('div');
      overlay.className = 'modal-overlay';
      overlay.setAttribute('role', 'dialog');
      overlay.setAttribute('aria-modal', 'true');
      overlay.setAttribute('aria-labelledby', titleId);
      overlay.style.position = 'fixed';
      overlay.style.inset = '0';
      overlay.style.zIndex = '2500';
      overlay.style.background = 'rgba(5, 45, 58, 0.58)';
      overlay.style.display = 'flex';
      overlay.style.alignItems = 'center';
      overlay.style.justifyContent = 'center';
      overlay.style.opacity = '0';
      overlay.style.transition = 'opacity 0.2s ease';

      const icons = {
        warning: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#E65100" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        danger: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D93025" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
        info: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#084355" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
        success: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2E7D32" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>'
      };

      overlay.innerHTML = `
        <div class="modal" style="max-width: 440px; transform: scale(0.95); transition: transform 0.2s ease; display: flex; flex-direction: column; background: var(--color-surface, #fff); border-radius: var(--radius-lg, 16px); box-shadow: var(--shadow-lg);">
          <div class="modal-header" style="display:flex;align-items:center;justify-content:space-between;padding:20px 24px;border-bottom:1px solid var(--color-border,#e0e0e0);">
            <div style="display:flex;align-items:center;gap:12px;">
              <span class="confirm-icon" style="display:inline-flex;align-items:center;justify-content:center;">${icons[icon] || icons.warning}</span>
              <h3 id="${titleId}" class="modal-title" style="margin:0;font-size:16px;font-weight:700;color:var(--color-primary,#084355);">${title}</h3>
            </div>
            <button class="modal-close confirm-cancel" aria-label="Fermer" style="width: 32px; height: 32px; border-radius: var(--radius-sm); border: none; background: var(--color-bg, #f5f5f5); cursor: pointer; display: flex; align-items: center; justify-content: center; color: var(--color-primary, #666); font-size:18px;">&times;</button>
          </div>
          <div class="modal-body" style="padding:24px;overflow-y:auto;max-height:60vh;">
            <p style="color:var(--color-primary,#555);line-height:1.6;margin:0;font-size:14px;opacity:0.8;">${message}</p>
          </div>
          <div class="modal-footer" style="display:flex;align-items:center;justify-content:flex-end;gap:12px;padding:16px 24px;border-top:1px solid var(--color-border,#e0e0e0);background:var(--color-bg,#fcfcfc);">
            <button class="btn btn-secondary confirm-cancel">${cancelText}</button>
            <button class="btn ${confirmClass} confirm-ok">${confirmText}</button>
          </div>
        </div>
      `;

      document.body.appendChild(overlay);
      const modalEl = overlay.querySelector('.modal');

      requestAnimationFrame(() => {
        overlay.style.opacity = '1';
        modalEl.style.transform = 'scale(1)';
      });

      const bodyWasOverflowHidden = document.body.style.overflow === "hidden";
      document.body.style.overflow = "hidden";

      const cleanup = (result) => {
        overlay.style.opacity = '0';
        modalEl.style.transform = 'scale(0.95)';
        
        document.removeEventListener('keydown', onKeydown);
        
        setTimeout(() => {
          overlay.remove();
          if (!bodyWasOverflowHidden) {
            const activeModals = document.querySelectorAll('.modal-overlay.active, div[style*="z-index: 2500"]');
            if (activeModals.length === 0) {
              document.body.style.overflow = "";
            }
          }
          if (previousActiveElement && typeof previousActiveElement.focus === 'function') {
            previousActiveElement.focus();
          }
          resolve(result);
        }, 200);
      };

      overlay.querySelector('.confirm-ok').onclick = () => cleanup(true);
      overlay.querySelectorAll('.confirm-cancel').forEach(btn => {
        btn.onclick = () => cleanup(false);
      });
      overlay.onclick = (e) => {
        if (e.target === overlay) cleanup(false);
      };

      const focusableElements = overlay.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
      const firstFocusable = focusableElements[0];
      const lastFocusable = focusableElements[focusableElements.length - 1];

      const onKeydown = (e) => {
        if (e.key === 'Escape') {
          cleanup(false);
        } else if (e.key === 'Tab') {
          if (e.shiftKey) {
            if (document.activeElement === firstFocusable) {
              lastFocusable.focus();
              e.preventDefault();
            }
          } else {
            if (document.activeElement === lastFocusable) {
              firstFocusable.focus();
              e.preventDefault();
            }
          }
        }
      };
      document.addEventListener('keydown', onKeydown);

      overlay.querySelector('.confirm-ok').focus();
    });
  };

  window.showAlert = function(title, message, options = {}) {
    const {
      buttonText = 'OK',
      confirmClass = 'btn-primary',
      icon = 'info'
    } = options;

    return new Promise((resolve) => {
      const previousActiveElement = document.activeElement;
      const titleId = `custom-modal-title-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
      const overlay = document.createElement('div');
      overlay.className = 'modal-overlay';
      overlay.setAttribute('role', 'dialog');
      overlay.setAttribute('aria-modal', 'true');
      overlay.setAttribute('aria-labelledby', titleId);
      overlay.style.position = 'fixed';
      overlay.style.inset = '0';
      overlay.style.zIndex = '2500';
      overlay.style.background = 'rgba(5, 45, 58, 0.58)';
      overlay.style.display = 'flex';
      overlay.style.alignItems = 'center';
      overlay.style.justifyContent = 'center';
      overlay.style.opacity = '0';
      overlay.style.transition = 'opacity 0.2s ease';

      const icons = {
        warning: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#E65100" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        danger: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D93025" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
        info: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#084355" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
        success: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2E7D32" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>'
      };

      overlay.innerHTML = `
        <div class="modal" style="max-width: 440px; transform: scale(0.95); transition: transform 0.2s ease; display: flex; flex-direction: column; background: var(--color-surface, #fff); border-radius: var(--radius-lg, 16px); box-shadow: var(--shadow-lg);">
          <div class="modal-header" style="display:flex;align-items:center;justify-content:space-between;padding:20px 24px;border-bottom:1px solid var(--color-border,#e0e0e0);">
            <div style="display:flex;align-items:center;gap:12px;">
              <span class="confirm-icon" style="display:inline-flex;align-items:center;justify-content:center;">${icons[icon] || icons.info}</span>
              <h3 id="${titleId}" class="modal-title" style="margin:0;font-size:16px;font-weight:700;color:var(--color-primary,#084355);">${title}</h3>
            </div>
            <button class="modal-close alert-ok" aria-label="Fermer" style="width: 32px; height: 32px; border-radius: var(--radius-sm); border: none; background: var(--color-bg, #f5f5f5); cursor: pointer; display: flex; align-items: center; justify-content: center; color: var(--color-primary, #666); font-size:18px;">&times;</button>
          </div>
          <div class="modal-body" style="padding:24px;overflow-y:auto;max-height:60vh;">
            <p style="color:var(--color-primary,#555);line-height:1.6;margin:0;font-size:14px;opacity:0.8;">${message}</p>
          </div>
          <div class="modal-footer" style="display:flex;align-items:center;justify-content:flex-end;gap:12px;padding:16px 24px;border-top:1px solid var(--color-border,#e0e0e0);background:var(--color-bg,#fcfcfc);">
            <button class="btn ${confirmClass} alert-ok">${buttonText}</button>
          </div>
        </div>
      `;

      document.body.appendChild(overlay);
      const modalEl = overlay.querySelector('.modal');

      requestAnimationFrame(() => {
        overlay.style.opacity = '1';
        modalEl.style.transform = 'scale(1)';
      });

      const bodyWasOverflowHidden = document.body.style.overflow === "hidden";
      document.body.style.overflow = "hidden";

      const cleanup = () => {
        overlay.style.opacity = '0';
        modalEl.style.transform = 'scale(0.95)';
        
        document.removeEventListener('keydown', onKeydown);
        
        setTimeout(() => {
          overlay.remove();
          if (!bodyWasOverflowHidden) {
            const activeModals = document.querySelectorAll('.modal-overlay.active, div[style*="z-index: 2500"]');
            if (activeModals.length === 0) {
              document.body.style.overflow = "";
            }
          }
          if (previousActiveElement && typeof previousActiveElement.focus === 'function') {
            previousActiveElement.focus();
          }
          resolve();
        }, 200);
      };

      overlay.querySelectorAll('.alert-ok').forEach(btn => {
        btn.onclick = cleanup;
      });
      overlay.onclick = (e) => {
        if (e.target === overlay) cleanup();
      };

      const focusableElements = overlay.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
      const firstFocusable = focusableElements[0];
      const lastFocusable = focusableElements[focusableElements.length - 1];

      const onKeydown = (e) => {
        if (e.key === 'Escape') {
          cleanup();
        } else if (e.key === 'Tab') {
          if (e.shiftKey) {
            if (document.activeElement === firstFocusable) {
              lastFocusable.focus();
              e.preventDefault();
            }
          } else {
            if (document.activeElement === lastFocusable) {
              firstFocusable.focus();
              e.preventDefault();
            }
          }
        }
      };
      document.addEventListener('keydown', onKeydown);

      overlay.querySelector('.alert-ok').focus();
    });
  };

  window.showPrompt = function(title, message, options = {}) {
    const {
      placeholder = '',
      defaultValue = '',
      inputType = 'text',
      validate = null,
      errorMessage = 'Saisie invalide',
      confirmText = 'Saisir',
      cancelText = 'Annuler',
      icon = 'info'
    } = options;

    return new Promise((resolve) => {
      const previousActiveElement = document.activeElement;
      const titleId = `custom-modal-title-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
      const overlay = document.createElement('div');
      overlay.className = 'modal-overlay';
      overlay.setAttribute('role', 'dialog');
      overlay.setAttribute('aria-modal', 'true');
      overlay.setAttribute('aria-labelledby', titleId);
      overlay.style.position = 'fixed';
      overlay.style.inset = '0';
      overlay.style.zIndex = '2500';
      overlay.style.background = 'rgba(5, 45, 58, 0.58)';
      overlay.style.display = 'flex';
      overlay.style.alignItems = 'center';
      overlay.style.justifyContent = 'center';
      overlay.style.opacity = '0';
      overlay.style.transition = 'opacity 0.2s ease';

      const icons = {
        warning: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#E65100" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        danger: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D93025" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
        info: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#084355" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
        success: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2E7D32" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>'
      };

      overlay.innerHTML = `
        <div class="modal" style="max-width: 440px; transform: scale(0.95); transition: transform 0.2s ease; display: flex; flex-direction: column; background: var(--color-surface, #fff); border-radius: var(--radius-lg, 16px); box-shadow: var(--shadow-lg);">
          <div class="modal-header" style="display:flex;align-items:center;justify-content:space-between;padding:20px 24px;border-bottom:1px solid var(--color-border,#e0e0e0);">
            <div style="display:flex;align-items:center;gap:12px;">
              <span class="confirm-icon" style="display:inline-flex;align-items:center;justify-content:center;">${icons[icon] || icons.info}</span>
              <h3 id="${titleId}" class="modal-title" style="margin:0;font-size:16px;font-weight:700;color:var(--color-primary,#084355);">${title}</h3>
            </div>
            <button class="modal-close prompt-cancel" aria-label="Fermer" style="width: 32px; height: 32px; border-radius: var(--radius-sm); border: none; background: var(--color-bg, #f5f5f5); cursor: pointer; display: flex; align-items: center; justify-content: center; color: var(--color-primary, #666); font-size:18px;">&times;</button>
          </div>
          <div class="modal-body" style="padding:24px;overflow-y:auto;max-height:60vh;display:flex;flex-direction:column;gap:10px;">
            <p style="color:var(--color-primary,#555);line-height:1.6;margin:0;font-size:14px;opacity:0.8;">${message}</p>
            <input type="${inputType}" class="prompt-input" placeholder="${placeholder}" value="${defaultValue}" style="width:100%; padding:10px 14px; border:1px solid var(--color-border,#e0e0e0); border-radius:8px; font-size:14px; outline:none; transition: border-color 0.2s ease; background: var(--color-bg); color: var(--color-primary);" />
            <div class="prompt-error" style="color:var(--color-danger, #D93025); font-size:12px; display:none; align-items:center; gap:4px;">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              <span class="error-text">${errorMessage}</span>
            </div>
          </div>
          <div class="modal-footer" style="display:flex;align-items:center;justify-content:flex-end;gap:12px;padding:16px 24px;border-top:1px solid var(--color-border,#e0e0e0);background:var(--color-bg,#fcfcfc);">
            <button class="btn btn-secondary prompt-cancel">${cancelText}</button>
            <button class="btn btn-primary prompt-ok">${confirmText}</button>
          </div>
        </div>
      `;

      document.body.appendChild(overlay);
      const modalEl = overlay.querySelector('.modal');
      const inputEl = overlay.querySelector('.prompt-input');
      const errorEl = overlay.querySelector('.prompt-error');

      inputEl.onfocus = () => {
        inputEl.style.borderColor = 'var(--color-primary, #084355)';
        inputEl.style.boxShadow = '0 0 0 3px rgba(8, 67, 85, 0.15)';
      };
      inputEl.onblur = () => {
        inputEl.style.borderColor = 'var(--color-border,#e0e0e0)';
        inputEl.style.boxShadow = 'none';
      };

      requestAnimationFrame(() => {
        overlay.style.opacity = '1';
        modalEl.style.transform = 'scale(1)';
      });

      const bodyWasOverflowHidden = document.body.style.overflow === "hidden";
      document.body.style.overflow = "hidden";

      const cleanup = (result) => {
        overlay.style.opacity = '0';
        modalEl.style.transform = 'scale(0.95)';
        
        document.removeEventListener('keydown', onKeydown);
        
        setTimeout(() => {
          overlay.remove();
          if (!bodyWasOverflowHidden) {
            const activeModals = document.querySelectorAll('.modal-overlay.active, div[style*="z-index: 2500"]');
            if (activeModals.length === 0) {
              document.body.style.overflow = "";
            }
          }
          if (previousActiveElement && typeof previousActiveElement.focus === 'function') {
            previousActiveElement.focus();
          }
          resolve(result);
        }, 200);
      };

      const handleOk = () => {
        const value = inputEl.value;
        let isValid = true;
        let errText = errorMessage;

        if (typeof validate === 'function') {
          const check = validate(value);
          if (check === false) {
            isValid = false;
          } else if (typeof check === 'string') {
            isValid = false;
            errText = check;
          }
        }

        if (!isValid) {
          errorEl.querySelector('.error-text').textContent = errText;
          errorEl.style.display = 'flex';
          inputEl.style.borderColor = 'var(--color-danger, #D93025)';
          inputEl.focus();
          return;
        }

        cleanup(value);
      };

      overlay.querySelector('.prompt-ok').onclick = handleOk;
      overlay.querySelectorAll('.prompt-cancel').forEach(btn => {
        btn.onclick = () => cleanup(null);
      });
      overlay.onclick = (e) => {
        if (e.target === overlay) cleanup(null);
      };

      const focusableElements = overlay.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
      const firstFocusable = focusableElements[0];
      const lastFocusable = focusableElements[focusableElements.length - 1];

      const onKeydown = (e) => {
        if (e.key === 'Escape') {
          cleanup(null);
        } else if (e.key === 'Enter') {
          if (document.activeElement === inputEl) {
            e.preventDefault();
            handleOk();
          }
        } else if (e.key === 'Tab') {
          if (e.shiftKey) {
            if (document.activeElement === firstFocusable) {
              lastFocusable.focus();
              e.preventDefault();
            }
          } else {
            if (document.activeElement === lastFocusable) {
              firstFocusable.focus();
              e.preventDefault();
            }
          }
        }
      };
      document.addEventListener('keydown', onKeydown);

      inputEl.focus();
    });
  };

}());






