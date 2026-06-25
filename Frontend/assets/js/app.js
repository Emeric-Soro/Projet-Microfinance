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

    // Si pas de token → rediriger vers login sans tenter le fetch
    if (!token) {
      var currentPage = (window.location.pathname.split('/').pop() || '')
        .replace(/\.html$/i, '');
      if (!['login', 'otp', 'mot-de-passe-oublie'].includes(currentPage)) {
        window.location.href = 'login.html';
      }
      return null;
    }

    var defaultHeaders = { 'Authorization': 'Bearer ' + token };
    if (!(options.body instanceof FormData)) {
      defaultHeaders['Content-Type'] = 'application/json';
    }
    var headers = Object.assign(defaultHeaders, options.headers || {});
    
    // Eviter la mise en cache par le navigateur sur toutes les requetes GET
    var urlPath = path;
    var method = (options.method || 'GET').toUpperCase();
    if (method === 'GET') {
      var separator = urlPath.includes('?') ? '&' : '?';
      urlPath = urlPath + separator + '_t=' + Date.now();
    }

    var res;
    try {
      res = await fetch(API_BASE + urlPath, Object.assign({}, options, { headers: headers }));
    } catch (fetchErr) {
      var msg = 'Impossible de joindre le serveur (localhost:8080). Vérifiez que le backend est démarré.';
      if (fetchErr && fetchErr.message && fetchErr.message.toLowerCase().includes('cors')) {
        msg = 'Erreur CORS : le backend refuse les requêtes depuis cette origine.';
      }
      throw new Error(msg);
    }
    // 401 → session expirée
    if (res.status === 401) {
      clearSession();
      var pg = (window.location.pathname.split('/').pop() || '').replace(/\.html$/i, '');
      if (!['login', 'otp', 'mot-de-passe-oublie'].includes(pg)) {
        if (typeof window.showToast === 'function') window.showToast('Session expirée. Redirection vers la connexion…', 'warning');
        setTimeout(function () { window.location.href = 'login.html'; }, 1200);
      }
      return null;
    }
    // 403 → accès refusé
    if (res.status === 403) {
      if (typeof window.showToast === 'function') window.showToast('Accès refusé : droits insuffisants.', 'error');
      return res;
    }
    return res;
  }

  /* ── Endpoints Auth ─────────────────────────────────── */

  var Auth = {
    login: async function (login, motDePasse) {
      return await fetch(API_BASE + '/api/v1/utilisateurs/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: login, motDePasse: motDePasse })
      });
    },
    verifierOtp: async function (login, codeOtp) {
      return await fetch(API_BASE + '/api/v1/utilisateurs/login/otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: login, challengeId: getChallengeId(), codeOtp: codeOtp })
      });
    },
    logout: async function () {
      if (!confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
        return;
      }
      var token = getToken();
      if (token) {
        try {
          await fetch(API_BASE + '/api/v1/utilisateurs/logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token }
          });
        } catch (_) {}
      }
      clearSession();
      window.location.href = './login.html';
    }
  };

  /* ── Guard de session ───────────────────────────────── */
  // Redirige vers login.html si pas de token, sauf sur les pages publiques.
  // npx serve supprime .html → normaliser le nom de page avant comparaison.

  var AUTH_BASES = ['login', 'otp', 'mot-de-passe-oublie'];

  function guardSession() {
    var currentPage = (window.location.pathname.split('/').pop() || '')
      .replace(/\.html$/i, '');
    if (!AUTH_BASES.includes(currentPage) && !getToken()) {
      window.location.href = 'login.html';
    }
  }

  guardSession(); // s'exécute immédiatement, avant DOMContentLoaded

  /* ── Endpoints Clients ───────────────────────────── */

  var Clients = {
    /** GET /api/v1/clients?page=&size=&sort=idClient,desc */
    lister: async function (page, size, query) {
      page = page || 0; size = size || 20;
      var path = '/api/v1/clients?page=' + page + '&size=' + size + '&sort=idClient,desc';
      if (query) path += '&recherche=' + encodeURIComponent(query);
      return await apiFetch(path);
    },
    /** GET /api/v1/clients/{id} */
    obtenir: async function (idClient) {
      return await apiFetch('/api/v1/clients/' + idClient);
    },
    /** GET /api/v1/clients/{id}/confidentiel (réservé admin) */
    obtenirConfidentiel: async function (idClient) {
      return await apiFetch('/api/v1/clients/' + idClient + '/confidentiel');
    },
    /** POST /api/v1/clients */
    creer: async function (data) {
      return await apiFetch('/api/v1/clients', { method: 'POST', body: JSON.stringify(data) });
    },
    /** PUT /api/v1/clients/{id}/statut?nouveauStatut= */
    modifierStatut: async function (idClient, nouveauStatut) {
      return await apiFetch('/api/v1/clients/' + idClient + '/statut?nouveauStatut=' + encodeURIComponent(nouveauStatut), { method: 'PUT' });
    },
    /** PUT /api/v1/clients/{id} */
    modifier: async function (idClient, data) {
      return await apiFetch('/api/v1/clients/' + idClient, { method: 'PUT', body: JSON.stringify(data) });
    },
    /** PUT /api/v1/clients/{id}/kyc/decision */
    traiterKyc: async function (idClient, data) {
      return await apiFetch('/api/v1/clients/' + idClient + '/kyc/decision', { method: 'PUT', body: JSON.stringify(data) });
    },
    /** PUT /api/v1/clients/{id}/kyc */
    mettreAJourKyc: async function (idClient, data) {
      return await apiFetch('/api/v1/clients/' + idClient + '/kyc', { method: 'PUT', body: JSON.stringify(data) });
    },
    /** POST /api/v1/clients/{id}/documents */
    uploadDocument: async function (idClient, file, categorie) {
      var formData = new FormData();
      formData.append('fichier', file);
      if (categorie) formData.append('categorie', categorie);
      return await apiFetch('/api/v1/clients/' + idClient + '/documents', {
        method: 'POST',
        body: formData
      });
    }
  };

  var Comptes = {
    lister: async function (page, size, search) {
      page = page || 0; size = size || 20;
      var path = '/api/v1/comptes?page=' + page + '&size=' + size;
      if (search) path += '&search=' + encodeURIComponent(search);
      return await apiFetch(path);
    },
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
    /** POST /api/v1/transactions/depot */
    depot: async function (numCompte, montant, idGuichetier) {
      return await apiFetch('/api/v1/transactions/depot', {
        method: 'POST',
        body: JSON.stringify({ numCompte: numCompte, montant: montant, idGuichetier: idGuichetier })
      });
    },
    /** POST /api/v1/transactions/retrait */
    retrait: async function (numCompte, montant, idGuichetier, numeroCarte) {
      var body = { numCompte: numCompte, montant: montant, idGuichetier: idGuichetier };
      if (numeroCarte) body.numeroCarte = numeroCarte;
      return await apiFetch('/api/v1/transactions/retrait', {
        method: 'POST',
        body: JSON.stringify(body)
      });
    },
    /** POST /api/v1/comptes/{numCompte}/depot-initial — sans caisse requise */
    depotInitial: async function (numCompte, montant, idInitiateur) {
      return await apiFetch('/api/v1/comptes/' + encodeURIComponent(numCompte) + '/depot-initial', {
        method: 'POST',
        body: JSON.stringify({ montant: montant, idInitiateur: idInitiateur })
      });
    },
    /** POST /api/v1/transactions/paiement-carte */
    payerCarte: async function (numeroCarte, montant, idGuichetier) {
      return await apiFetch('/api/v1/transactions/paiement-carte', {
        method: 'POST',
        body: JSON.stringify({ numeroCarte: numeroCarte, montant: montant, idGuichetier: idGuichetier })
      });
    },
    /** GET /api/v1/transactions/comptes/{numCompte}/historique */
    historiqueCompte: async function (numCompte, page, size) {
      return await apiFetch(
        '/api/v1/transactions/comptes/' + encodeURIComponent(numCompte)
        + '/historique?page=' + (page || 0) + '&size=' + (size || 20)
      );
    },
    /** GET /api/v1/transactions/{ref}/recu */
    recu: async function (referenceUnique) {
      return await apiFetch('/api/v1/transactions/' + encodeURIComponent(referenceUnique) + '/recu');
    },
    /** POST /api/v1/transactions/virement */
    virement: async function (compteSource, compteDestination, montant, idGuichetier) {
      return await apiFetch('/api/v1/transactions/virement?idGuichetier=' + encodeURIComponent(idGuichetier), {
        method: 'POST',
        body: JSON.stringify({ compteSource: compteSource, compteDestination: compteDestination, montant: montant })
      });
    },
    /** GET /api/v1/transactions/en-attente */
    listerEnAttente: async function (page, size) {
      return await apiFetch('/api/v1/transactions/en-attente?page=' + (page || 0) + '&size=' + (size || 20));
    },
    /** PUT /api/v1/transactions/{ref}/approbation */
    approuver: async function (referenceUnique, idSuperviseur) {
      return await apiFetch('/api/v1/transactions/' + encodeURIComponent(referenceUnique) + '/approbation', {
        method: 'PUT',
        body: JSON.stringify({ idSuperviseur: idSuperviseur })
      });
    },
    /** PUT /api/v1/transactions/{ref}/rejet */
    rejeter: async function (referenceUnique, idSuperviseur, motif) {
      return await apiFetch('/api/v1/transactions/' + encodeURIComponent(referenceUnique) + '/rejet', {
        method: 'PUT',
        body: JSON.stringify({ idSuperviseur: idSuperviseur, motif: motif })
      });
    },
    /** GET /api/v1/transactions */
    listerToutes: async function (page, size) {
      return await apiFetch('/api/v1/transactions?page=' + (page || 0) + '&size=' + (size || 20));
    }
  };

  var Caisses = {
    ouvrir: async function (soldeInitial) {
      return await apiFetch('/api/v1/caisses/ouverture', {
        method: 'POST',
        body: JSON.stringify({ soldeInitial: soldeInitial })
      });
    },
    fermer: async function (soldePhysiqueConstate) {
      return await apiFetch('/api/v1/caisses/fermeture', {
        method: 'POST',
        body: JSON.stringify({ soldePhysiqueConstate: soldePhysiqueConstate })
      });
    },
    etat: async function () {
      return await apiFetch('/api/v1/caisses/etat');
    }
  };

  var Dashboards = {
    /** GET /api/v1/dashboards/agence */
    agence: async function (agenceId, periode) {
      var url = '/api/v1/dashboards/agence?periode=' + (periode || 'JOUR');
      if (agenceId) url += '&agenceId=' + agenceId;
      return await apiFetch(url);
    },
    /** GET /api/v1/dashboards/direction */
    direction: async function () {
      return await apiFetch('/api/v1/dashboards/direction');
    },
    /** GET /api/v1/dashboards/indicateurs */
    indicateurs: async function () {
      return await apiFetch('/api/v1/dashboards/indicateurs');
    },
    /** GET /api/v1/dashboards/graphiques */
    graphiques: async function (agenceId) {
      var url = '/api/v1/dashboards/graphiques';
      if (agenceId) url += '?agenceId=' + agenceId;
      return await apiFetch(url);
    }
  };

  const Conformite = {
    /** GET /api/v1/conformite/alertes-lcbft */
    alertesLcbFt: async function (page = 0, size = 10) {
      return await apiFetch('/api/v1/conformite/alertes-lcbft?page=' + page + '&size=' + size);
    },
    /** GET /api/v1/conformite/kyc/expires */
    kycExpires: async function (page = 0, size = 10) {
      return await apiFetch('/api/v1/conformite/kyc/expires?page=' + page + '&size=' + size);
    }
  };

  async function downloadFile(path, defaultFilename) {
    try {
      if (typeof window.showToast === 'function') window.showToast('Téléchargement du fichier en cours...', 'info');
      var res = await apiFetch(path);
      if (res && res.ok) {
        var blob = await res.blob();
        var blobUrl = URL.createObjectURL(blob);
        var a = document.createElement('a');
        a.href = blobUrl;
        
        var filename = defaultFilename;
        var cd = res.headers.get('Content-Disposition');
        if (cd) {
          var match = cd.match(/filename="?([^"]+)"?/);
          if (match && match[1]) filename = match[1];
        }
        a.download = filename;
        a.click();
        setTimeout(function () { URL.revokeObjectURL(blobUrl); }, 10000);
        if (typeof window.showToast === 'function') window.showToast('Téléchargement terminé.', 'success');
      } else if (res) {
        var err = await res.json().catch(function () { return {}; });
        if (typeof window.showToast === 'function') window.showToast('Erreur : ' + (err.message || 'Impossible de télécharger le fichier.'), 'error');
      }
    } catch (e) {
      console.error('[Download] Error:', e);
      if (typeof window.showToast === 'function') window.showToast('Erreur lors du téléchargement.', 'error');
    }
  }

  /* ── Exposition globale ─────────────────────────── */

  window.SF   = { API_BASE, getToken, setSession, clearSession, getUser, getChallengeId, apiFetch, downloadFile };
  window.Auth = Auth;
  window.Clients = Clients;
  window.Comptes = Comptes;
  window.Cartes  = Cartes;
  window.Beneficiaires = Beneficiaires;
  window.Transactions = Transactions;
  window.Caisses = Caisses;
  window.Dashboards = Dashboards;
  window.Conformite = Conformite;

}());

/* ============================================
   BACKOFFICE SOUTRA FINANCE - Shared UI kernel
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
        { href: 'clients.html', label: 'Liste clients', icon: icons.users, match: ['clients.html', 'client-detail.html'] },
        { href: 'client-create.html', label: 'Nouveau client', icon: icons.users, match: ['client-create.html'] },
        { href: 'kyc-validation.html', label: 'Validation KYC', icon: icons.audit, match: ['kyc-validation.html'] },
        { href: 'recherche-client.html', label: 'Recherche avancée', icon: icons.users, match: ['recherche-client.html'] },
        { href: 'blacklist-client.html', label: 'Blacklist', icon: icons.audit, match: ['blacklist-client.html'] }
      ]
    },
    {
      title: 'Comptes & Cartes',
      items: [
        { href: 'comptes.html', label: 'Comptes', icon: icons.card, match: ['comptes.html', 'detail-compte.html', 'fermeture-compte.html', 'blocage-compte.html'] },
        { href: 'ouverture-compte.html', label: 'Ouverture compte', icon: icons.card, match: ['ouverture-compte.html'] },
        { href: 'cartes.html', label: 'Cartes Visa', icon: icons.card, match: ['cartes.html', 'detail-carte.html', 'demande-carte.html', 'opposition-carte.html'] },
        { href: 'paiement-carte.html', label: 'Paiement carte', icon: icons.card, match: ['paiement-carte.html'] }
      ]
    },
    {
      title: 'Caisse & Opérations',
      items: [
        { href: 'caisse.html', label: 'Caisse', icon: icons.cash, match: ['caisse.html', 'fermeture-caisse.html'] },
        { href: 'guichet.html', label: 'Guichet', icon: icons.cash, match: ['guichet.html'] },
        { href: 'virement.html', label: 'Virement', icon: icons.transfer, match: ['virement.html'] },
        { href: 'retrait-mm.html', label: 'Retrait Mobile Money', icon: icons.cash, match: ['retrait-mm.html'] },
        { href: 'validation.html', label: 'Validation 4-eyes', icon: icons.audit, match: ['validation.html'] },
        { href: 'historique.html', label: 'Historique', icon: icons.audit, match: ['historique.html', 'detail-transaction.html', 'annulation.html', 'export-transactions.html'] }
      ]
    },
    {
      title: 'Crédits',
      items: [
        { href: 'credit-simulation.html', label: 'Simulation', icon: icons.credit, match: ['credit-simulation.html'] },
        { href: 'credit-demandes.html', label: 'Demandes', icon: icons.credit, match: ['credit-demandes.html'] },
        { href: 'credit-detail.html', label: 'Dossier crédit', icon: icons.credit, match: ['credit-detail.html'] },
        { href: 'credit-suivi.html', label: 'Suivi avancé', icon: icons.credit, match: ['credit-suivi.html'] }
      ]
    },
    {
      title: 'Épargne',
      items: [
        { href: 'comptes-a-terme.html', label: 'Comptes à terme (DAT)', icon: icons.credit, match: ['comptes-a-terme.html'] },
        { href: 'interets-courus.html', label: 'Intérêts courus', icon: icons.credit, match: ['interets-courus.html'] }
      ]
    },
    {
      title: 'Paramétrage',
      items: [
        { href: 'produits.html', label: 'Produits', icon: icons.settings, match: ['produits.html'] },
        { href: 'commissions.html', label: 'Commissions', icon: icons.settings, match: ['commissions.html'] },
        { href: 'historique-frais.html', label: 'Historique frais', icon: icons.settings, match: ['historique-frais.html'] },
        { href: 'agences.html', label: 'Agences', icon: icons.settings, match: ['agences.html'] },
        { href: 'calendrier-jours-feries.html', label: 'Jours fériés', icon: icons.settings, match: ['calendrier-jours-feries.html'] },
        { href: 'personnel-create.html', label: 'Ajouter personnel', icon: icons.users, match: ['personnel-create.html'] },
        { href: 'utilisateurs.html', label: 'Utilisateurs', icon: icons.users, match: ['utilisateurs.html'] },
        { href: 'parametres-systeme.html', label: 'Paramètres système', icon: icons.settings, match: ['parametres-systeme.html'] },
        { href: 'cache.html', label: 'Cache tarification', icon: icons.settings, match: ['cache.html'] }
      ]
    },
    {
      title: 'Sécurité',
      items: [
        { href: 'securite.html', label: 'Rôles & permissions', icon: icons.settings, match: ['securite.html'] },
        { href: 'utilisateurs.html', label: 'Utilisateurs', icon: icons.users, match: ['utilisateurs.html'] },
        { href: 'sessions-actives.html', label: 'Sessions actives', icon: icons.audit, match: ['sessions-actives.html', 'sessions-admin.html'] },
        { href: 'parametres-2fa.html', label: 'Paramètres 2FA', icon: icons.settings, match: ['parametres-2fa.html'] }
      ]
    },
    {
      title: 'Agios & Frais',
      items: [
        { href: 'agios.html', label: 'Agios', icon: icons.credit, match: ['agios.html'] },
        { href: 'frais-tenue-compte.html', label: 'Frais tenue compte', icon: icons.credit, match: ['frais-tenue-compte.html'] },
        { href: 'penalite-decouvert.html', label: 'Pénalité découvert', icon: icons.credit, match: ['penalite-decouvert.html'] },
        { href: 'execution-prelevements.html', label: 'Exécution prélèvements', icon: icons.credit, match: ['execution-prelevements.html'] }
      ]
    },
    {
      title: 'Conformité & LCB-FT',
      items: [
        { href: 'conformite.html', label: 'Alertes LCB-FT', icon: icons.audit, match: ['conformite.html'] },
        { href: 'sar-liste.html', label: 'Déclarations SAR', icon: icons.audit, match: ['sar-liste.html', 'sar-creer.html', 'sar-detail.html'] },
        { href: 'reclamations-backoffice.html', label: 'Réclamations', icon: icons.audit, match: ['reclamations-backoffice.html'] },
        { href: 'rgpd-consentement.html', label: 'RGPD Consentement', icon: icons.audit, match: ['rgpd-consentement.html'] },
        { href: 'rgpd-export.html', label: 'RGPD Export', icon: icons.audit, match: ['rgpd-export.html'] },
        { href: 'rgpd-suppression.html', label: 'RGPD Suppression', icon: icons.audit, match: ['rgpd-suppression.html'] },
        { href: 'verification-pep.html', label: 'Vérification PEP', icon: icons.audit, match: ['verification-pep.html'] },
        { href: 'liste-pep.html', label: 'Liste PEP', icon: icons.audit, match: ['liste-pep.html'] }
      ]
    },
    {
      title: 'Exceptions & Escalades',
      items: [
        { href: 'derogation-creer.html', label: 'Créer dérogation', icon: icons.audit, match: ['derogation-creer.html'] },
        { href: 'derogation-liste.html', label: 'Liste dérogations', icon: icons.audit, match: ['derogation-liste.html'] },
        { href: 'derogation-decision.html', label: 'Décision dérogation', icon: icons.audit, match: ['derogation-decision.html'] },
        { href: 'escalade-liste.html', label: 'Liste escalades', icon: icons.audit, match: ['escalade-liste.html'] },
        { href: 'escalade-creer.html', label: 'Créer escalade', icon: icons.audit, match: ['escalade-creer.html'] },
        { href: 'regles-escalade.html', label: 'Règles escalade', icon: icons.settings, match: ['regles-escalade.html'] }
      ]
    },
    {
      title: 'Pilotage & Reporting',
      items: [
        { href: 'dashboard.html', label: 'Tableau de bord', icon: icons.dashboard, match: ['dashboard.html'] },
        { href: 'direction.html', label: 'Direction', icon: icons.dashboard, match: ['direction.html'] },
        { href: 'statistiques-kpi.html', label: 'Statistiques KPI', icon: icons.dashboard, match: ['statistiques-kpi.html'] },
        { href: 'indicateurs-temps-reel.html', label: 'Indicateurs temps réel', icon: icons.dashboard, match: ['indicateurs-temps-reel.html'] },
        { href: 'reporting.html', label: 'Reporting', icon: icons.dashboard, match: ['reporting.html'] },
        { href: 'rapport-operationnel.html', label: 'Rapport opérationnel', icon: icons.dashboard, match: ['rapport-operationnel.html'] },
        { href: 'rapport-financier.html', label: 'Rapport financier', icon: icons.dashboard, match: ['rapport-financier.html'] },
        { href: 'rapport-clients.html', label: 'Rapport clients', icon: icons.dashboard, match: ['rapport-clients.html'] },
        { href: 'rapport-credits.html', label: 'Rapport crédits', icon: icons.dashboard, match: ['rapport-credits.html'] },
        { href: 'rapport-caisse.html', label: 'Rapport caisse', icon: icons.dashboard, match: ['rapport-caisse.html'] },
        { href: 'rapport-bceao.html', label: 'Rapport BCEAO', icon: icons.dashboard, match: ['rapport-bceao.html'] },
        { href: 'export-rapport.html', label: 'Export rapport', icon: icons.dashboard, match: ['export-rapport.html'] },
        { href: 'rapport-personnalise.html', label: 'Rapport personnalisé', icon: icons.dashboard, match: ['rapport-personnalise.html'] }
      ]
    },
    {
      title: 'Notifications',
      items: [
        { href: 'notifications.html', label: 'Notifications', icon: icons.audit, match: ['notifications.html'] },
        { href: 'notifications-gestion.html', label: 'Gestion notifications', icon: icons.audit, match: ['notifications-gestion.html'] },
        { href: 'notifications-preferences.html', label: 'Préférences', icon: icons.settings, match: ['notifications-preferences.html'] },
        { href: 'audit.html', label: "Journal d'audit", icon: icons.audit, match: ['audit.html'] }
      ]
    }
  ];

  function isActive(item) {
    const cleanPage = pageName.replace(/\.html$/, '');
    return item.match.some(m => m.replace(/\.html$/, '') === cleanPage);
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

    // Données utilisateur depuis la session
    const user = (window.SF && window.SF.getUser()) || {};
    const login = user.login || 'Utilisateur';
    const roles = Array.isArray(user.roles) && user.roles.length ? user.roles[0] : 'Agent';
    const initiales = login.substring(0, 2).toUpperCase();

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
        <img src="../assets/img/logo.png" alt="Logo Soutra Finance" class="sidebar-logo">
        <div class="sidebar-brand">SOUTRA FINANCE</div>
        <div class="sidebar-subtitle">Backoffice</div>
      </div>
      <div class="sidebar-user">
        <div class="sidebar-avatar">${initiales}</div>
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">${login}</div>
          <div class="sidebar-user-role">${roles}</div>
        </div>
      </div>
      <nav class="sidebar-nav" aria-label="Navigation backoffice">${groups}</nav>
      <div class="sidebar-footer">
        <a href="#" class="nav-item" id="logoutBtn">${icons.logout}<span>Déconnexion</span></a>
      </div>
    `;

    // Handler de déconnexion (appel API + effacement session)
    const logoutBtn = sidebar.querySelector('#logoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', function (e) {
        e.preventDefault();
        if (window.Auth) Auth.logout();
        else { localStorage.clear(); window.location.href = 'login.html'; }
      });
    }
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
      const user = (window.SF && window.SF.getUser()) || {};
      const displayName = user.login || 'Utilisateur';
      const context = document.createElement('div');
      context.className = 'topbar-context';
      context.innerHTML = `
        <span class="topbar-chip">Agence Plateau</span>
        <span class="topbar-chip">${displayName}</span>
        <a class="topbar-chip topbar-link" href="#" id="topbarLogoutLink">Déco</a>
      `;
      right.prepend(context);
      // Logout depuis topbar
      const topbarLogout = right.querySelector('#topbarLogoutLink');
      if (topbarLogout) {
        topbarLogout.addEventListener('click', function (e) {
          e.preventDefault();
          if (window.Auth) Auth.logout();
          else { localStorage.clear(); window.location.href = 'login.html'; }
        });
      }
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

    const scope = stepper.closest('.content-area') || document;
    const steps = Array.from(stepper.querySelectorAll('.step'));
    const stepContents = Array.from(scope.querySelectorAll('.step-content'));
    const nextButtons = Array.from(scope.querySelectorAll('.btn-next-step'));
    const prevButtons = Array.from(scope.querySelectorAll('.btn-prev-step'));
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

    nextButtons.forEach(button => {
      button.addEventListener('click', () => {
        if (currentStep >= steps.length - 1) return;
        if (stepContents[currentStep] && !validateStep(stepContents[currentStep])) return;
        showStep(currentStep + 1);
      });
    });

    prevButtons.forEach(button => {
      button.addEventListener('click', () => {
        if (currentStep > 0) showStep(currentStep - 1);
      });
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

    form.addEventListener('submit', async event => {
      event.preventDefault();
      const code = inputs.map(input => input.value).join('');
      const login = sessionStorage.getItem('sf_otp_login');

      if (!login || !(window.SF && window.SF.getChallengeId())) {
        showToast('Session OTP expirée. Reconnectez-vous.', 'warning');
        setTimeout(() => { window.location.href = 'login.html'; }, 900);
        return;
      }

      try {
        if (submit) submit.disabled = true;
        const res = await Auth.verifierOtp(login, code);
        const data = await res.json().catch(() => ({}));

        if (res.ok) {
          SF.setSession(data);
          sessionStorage.removeItem('sf_otp_login');
          sessionStorage.removeItem('sf_challenge_id');
          showToast('Vérification réussie.', 'success');
          setTimeout(() => { window.location.href = 'dashboard.html'; }, 700);
          return;
        }

        form.querySelector('.otp-inputs')?.classList.add('is-shaking');
        setTimeout(() => form.querySelector('.otp-inputs')?.classList.remove('is-shaking'), 320);
        showToast(data.message || 'Code incorrect.', 'error');
      } catch (_) {
        showToast('Impossible de joindre le serveur. Vérifiez que le backend est démarré.', 'error');
      } finally {
        syncSubmitState();
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
