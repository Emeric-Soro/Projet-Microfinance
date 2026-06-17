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

    var headers = Object.assign(
      { 'Content-Type': 'application/json' },
      token ? { 'Authorization': 'Bearer ' + token } : {},
      options.headers || {}
    );

    var res;
    try {
      res = await fetch(API_BASE + path, Object.assign({}, options, { headers: headers }));
    } catch (_) {
      throw new Error('Impossible de joindre le serveur. Vérifiez que le backend est démarré.');
    }

    if (res.status === 401) {
      clearSession();
      var currentPage = window.location.pathname.split('/').pop() || '';
      if (currentPage !== 'login.html') {
        window.location.href = 'login.html';
      }
      return null;
    }

    return res;
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

  window.Auth = Auth;

}());
