/* ============================================
   API CLIENT — Soutra Finance Backoffice
   Couche centralisée : token, fetch, session
   ============================================ */

(function () {
  'use strict';

  var API_BASE = 'http://localhost:8080';

  /* ── Gestion du token & session ─────────────────────── */

  function getToken() {
    return localStorage.getItem('sf_token') || null;
  }

  function setSession(data) {
    if (data.token) localStorage.setItem('sf_token', data.token);
    else localStorage.removeItem('sf_token');

    if (data.refreshToken) localStorage.setItem('sf_refresh_token', data.refreshToken);
    else localStorage.removeItem('sf_refresh_token');

    if (data.utilisateur) localStorage.setItem('sf_user', JSON.stringify(data.utilisateur));
    else localStorage.removeItem('sf_user');

    // Stocker challengeId si OTP requis
    if (data.challengeId) {
      sessionStorage.setItem('sf_challenge_id', data.challengeId);
    }
  }

  function clearSession() {
    localStorage.removeItem('sf_token');
    localStorage.removeItem('sf_refresh_token');
    localStorage.removeItem('sf_user');
    sessionStorage.removeItem('sf_challenge_id');
  }

  function getUser() {
    try {
      return JSON.parse(localStorage.getItem('sf_user') || 'null');
    } catch (_) {
      return null;
    }
  }

  function getChallengeId() {
    return sessionStorage.getItem('sf_challenge_id') || null;
  }

  /* ── fetch générique authentifié ───────────────────── */

  async function apiFetch(path, options) {
    options = options || {};
    var token = getToken();

    // Si pas de token → rediriger directement vers login sans tenter le fetch
    if (!token) {
      var currentPage = (window.location.pathname.split('/').pop() || '')
        .replace(/\.html$/i, '');
      if (!['login', 'otp'].includes(currentPage)) {
        window.location.href = 'login.html';
      }
      return null;
    }

    var headers = Object.assign(
      { 'Content-Type': 'application/json' },
      { 'Authorization': 'Bearer ' + token },
      options.headers || {}
    );

    var res;
    try {
      res = await fetch(API_BASE + path, Object.assign({}, options, { headers: headers }));
    } catch (fetchErr) {
      // Distinguer erreur CORS (TypeError) d'une erreur réseau
      var msg = 'Impossible de joindre le serveur (localhost:8080). Vérifiez que le backend est démarré.';
      if (fetchErr && fetchErr.message && fetchErr.message.toLowerCase().includes('cors')) {
        msg = 'Erreur CORS : le backend refuse les requêtes depuis cette origine. Vérifiez la configuration CORS.';
      }
      throw new Error(msg);
    }

    // 401 → session expirée, effacer et rediriger vers login
    if (res.status === 401) {
      clearSession();
      var pg = (window.location.pathname.split('/').pop() || '').replace(/\.html$/i, '');
      if (!['login', 'otp'].includes(pg)) {
        showToastIfAvailable('Session expirée. Redirection vers la connexion…', 'warning');
        setTimeout(function () { window.location.href = 'login.html'; }, 1200);
      }
      return null;
    }

    // 403 → non autorisé (mauvais rôle)
    if (res.status === 403) {
      showToastIfAvailable('Accès refusé : vous n\'avez pas les droits nécessaires pour cette action.', 'error');
      return res;
    }

    return res;
  }

  /* ── Helper toast (disponible après chargement de app.js) ── */
  function showToastIfAvailable(msg, type) {
    if (typeof window.showToast === 'function') window.showToast(msg, type || 'info');
    else console.warn('[SF]', msg);
  }

  /* ── Endpoints Auth ─────────────────────────────────── */

  var Auth = {

    /**
     * Connexion initiale
     * POST /api/v1/utilisateurs/login
     * @returns {Response}
     */
    login: async function (login, motDePasse) {
      return await fetch(API_BASE + '/api/v1/utilisateurs/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: login, motDePasse: motDePasse })
      });
    },

    /**
     * Vérification OTP (second facteur)
     * POST /api/v1/utilisateurs/login/otp
     * @returns {Response}
     */
    verifierOtp: async function (login, codeOtp) {
      var challengeId = getChallengeId();
      return await fetch(API_BASE + '/api/v1/utilisateurs/login/otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: login, challengeId: challengeId, codeOtp: codeOtp })
      });
    },

    /**
     * Déconnexion — révoque le JWT côté serveur
     * POST /api/v1/utilisateurs/logout
     */
    logout: async function () {
      var token = getToken();
      if (token) {
        try {
          await fetch(API_BASE + '/api/v1/utilisateurs/logout', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + token
            }
          });
        } catch (_) {
          // On efface la session même si le serveur est injoignable
        }
      }
      clearSession();
      window.location.href = 'login.html';
    }
  };

  /* ── Guard de session ───────────────────────────────── */
  // Redirige vers login.html si aucun token valide,
  // sauf sur les pages publiques (login, otp).

  var AUTH_BASES = ['login', 'otp'];

  function guardSession() {
    // npx serve supprime l'extension .html → normaliser avant comparaison
    var currentPage = (window.location.pathname.split('/').pop() || '')
      .replace(/\.html$/i, '');
    if (!AUTH_BASES.includes(currentPage) && !getToken()) {
      window.location.href = 'login.html';
    }
  }

  // Exécuter immédiatement (avant DOMContentLoaded)
  guardSession();

  /* ── Endpoints Clients ──────────────────────────────── */

  var Clients = {

    /**
     * Lister les clients (paginé)
     * GET /api/v1/clients?page=&size=
     * @returns {Response|null}
     */
    lister: async function (page, size, query) {
      page = page || 0;
      size = size || 20;
      var path = '/api/v1/clients?page=' + page + '&size=' + size + '&sort=idClient,desc';
      if (query) path += '&recherche=' + encodeURIComponent(query);
      return await apiFetch(path);
    },

    /**
     * Obtenir les détails d'un client
     * GET /api/v1/clients/{idClient}
     * @returns {Response|null}
     */
    obtenir: async function (idClient) {
      return await apiFetch('/api/v1/clients/' + idClient);
    },

    /**
     * Créer un nouveau client
     * POST /api/v1/clients
     * @returns {Response|null}
     */
    creer: async function (data) {
      return await apiFetch('/api/v1/clients', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },

    /**
     * Modifier le statut d'un client
     * PUT /api/v1/clients/{idClient}/statut?nouveauStatut=
     * @returns {Response|null}
     */
    modifierStatut: async function (idClient, nouveauStatut) {
      return await apiFetch('/api/v1/clients/' + idClient + '/statut?nouveauStatut=' + encodeURIComponent(nouveauStatut), {
        method: 'PUT'
      });
    },

    /**
     * Traiter un dossier KYC
     * PUT /api/v1/clients/{idClient}/kyc/decision
     * @returns {Response|null}
     */
    traiterKyc: async function (idClient, data) {
      return await apiFetch('/api/v1/clients/' + idClient + '/kyc/decision', {
        method: 'PUT',
        body: JSON.stringify(data)
      });
    }
  };

  var Comptes = {
    obtenir: async function (numCompte) { return await apiFetch('/api/v1/comptes/' + numCompte); },
    ouvrir: async function (idClient, type, depot) { return await apiFetch('/api/v1/comptes', { method: 'POST', body: JSON.stringify({ idClient: idClient, codeTypeCompte: type, depotInitial: depot }) }); },
    bloquer: async function (numCompte, motif) { return await apiFetch('/api/v1/comptes/' + numCompte + '/blocage', { method: 'PUT', body: JSON.stringify({ motif: motif }) }); },
    debloquer: async function (numCompte, motif) { return await apiFetch('/api/v1/comptes/' + numCompte + '/deblocage', { method: 'PUT', body: JSON.stringify({ motif: motif }) }); },
    cloturer: async function (numCompte, motif) { return await apiFetch('/api/v1/comptes/' + numCompte + '/cloture', { method: 'PUT', body: JSON.stringify({ motif: motif }) }); },
    modifierDecouvert: async function (numCompte, nouveauPlafond) { return await apiFetch('/api/v1/comptes/decouvert', { method: 'PUT', body: JSON.stringify({ numCompte: numCompte, nouveauPlafond: nouveauPlafond }) }); },
    listerClientComptes: async function (idClient, page, size) { return await apiFetch('/api/v1/comptes/client/' + idClient + '?page=' + (page || 0) + '&size=' + (size || 20)); }
  };

  var Cartes = {
    lister: async function (numCompte, page, size) { 
      let path = '/api/v1/cartes-visa?page=' + (page || 0) + '&size=' + (size || 20);
      if (numCompte) path += '&numCompte=' + encodeURIComponent(numCompte);
      return await apiFetch(path); 
    },
    commander: async function (numCompte) { return await apiFetch('/api/v1/cartes-visa?numCompte=' + encodeURIComponent(numCompte), { method: 'POST' }); },
    opposer: async function (numCarte) { return await apiFetch('/api/v1/cartes-visa/' + numCarte + '/opposition', { method: 'PUT' }); },
    obtenir: async function (numeroCarte) { return await apiFetch('/api/v1/cartes-visa/' + numeroCarte); },
    opposerParId: async function (idCarte) { return await apiFetch('/api/v1/cartes-visa/id/' + idCarte + '/opposition', { method: 'PUT' }); }
  };

  var Beneficiaires = {
    lister: async function (clientId) { 
      let path = '/api/v1/beneficiaires';
      if (clientId) path += '?clientId=' + clientId;
      return await apiFetch(path); 
    },
    creer: async function (clientId, data) { 
      return await apiFetch('/api/v1/beneficiaires?clientId=' + clientId, { method: 'POST', body: JSON.stringify(data) }); 
    },
    modifier: async function (id, clientId, data) {
      return await apiFetch('/api/v1/beneficiaires/' + id + '?clientId=' + clientId, { method: 'PUT', body: JSON.stringify(data) });
    },
    supprimer: async function (id, clientId) { 
      return await apiFetch('/api/v1/beneficiaires/' + id + '?clientId=' + clientId, { method: 'DELETE' }); 
    }
  };

  var Transactions = {
    payerCarte: async function (numeroCarte, montant, idGuichetier) {
      return await apiFetch('/api/v1/transactions/paiement-carte', { method: 'POST', body: JSON.stringify({ numeroCarte, montant, idGuichetier }) });
    }
  };

  /* ── Exposition globale ─────────────────────────────── */

  window.SF = {
    API_BASE:       API_BASE,
    getToken:       getToken,
    setSession:     setSession,
    clearSession:   clearSession,
    getUser:        getUser,
    getChallengeId: getChallengeId,
    apiFetch:       apiFetch
  };

  window.Auth    = Auth;
  window.Clients = Clients;
  window.Comptes = Comptes;
  window.Cartes  = Cartes;
  window.Beneficiaires = Beneficiaires;
  window.Transactions = Transactions;

}());
