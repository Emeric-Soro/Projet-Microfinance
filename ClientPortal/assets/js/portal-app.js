/* ============================================
   API CLIENT — Soutra Finance E-Banking Portal
   Couche centralisée : token, fetch, session
   ============================================ */

(function () {
  'use strict';

  var API_BASE = 'http://localhost:8080';

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

}());
