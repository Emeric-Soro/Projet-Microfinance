# 🎯 Plan MVP — Présentation du 2 juillet 2026

> Rédigé le 26/06/2026 — Il reste **6 jours** ouvrés.

---

## Contexte

Le projet est un **backoffice bancaire complet** (Soutra Finance). Il comporte actuellement
**101 pages HTML** et **19 objets API** dans `app.js`. Pour une soutenance/démo d'un MVP,
l'objectif est de montrer un **flux métier crédible et fonctionnel de bout en bout**,
pas de couvrir 100 % des features.

> **Règle d'or MVP** : mieux vaut **5 choses qui marchent vraiment** que
> 50 choses qui s'ouvrent mais sont vides ou cassées.

---

## 🟢 À GARDER — Le cœur du MVP (pages à faire fonctionner correctement)

### 1. Authentification

| Page                | Fichier                    | Pourquoi                                        |
| ------------------- | -------------------------- | ----------------------------------------------- |
| Connexion           | `login.html`               | Porte d'entrée obligatoire de toute démo        |
| OTP / 2FA           | `otp.html`                 | Montre la sécurité du système                   |
| Mot de passe oublié | `mot-de-passe-oublie.html` | Flux complet attendu dans tout système bancaire |

---

### 2. Tableau de bord

| Page      | Fichier          | Pourquoi                                                   |
| --------- | ---------------- | ---------------------------------------------------------- |
| Dashboard | `dashboard.html` | **Première impression** — chiffres clés, KPI en temps réel |

> ⚠️ Ne garder qu'**un seul dashboard**. Fusionner ou supprimer `direction.html`,
> `statistiques-kpi.html`, `indicateurs-temps-reel.html`, `reporting.html`.

---

### 3. Gestion clients _(flux principal de la démo)_

| Page              | Fichier               | Pourquoi                                               |
| ----------------- | --------------------- | ------------------------------------------------------ |
| Liste des clients | `clients.html`        | Vue centrale, montre la recherche, filtres, pagination |
| Créer un client   | `client-create.html`  | Flux de saisie d'un nouveau client + upload CNI        |
| Détail client     | `client-detail.html`  | Affiche toutes les infos, documents KYC, comptes liés  |
| Validation KYC    | `kyc-validation.html` | Processus métier essentiel d'une microfinance          |

> Les pages `recherche-client.html` et `blacklist-client.html` peuvent être démontrées
> depuis `clients.html` elle-même via filtres — pas besoin de pages séparées en démo.

---

### 4. Comptes bancaires

| Page                | Fichier                 | Pourquoi                                           |
| ------------------- | ----------------------- | -------------------------------------------------- |
| Liste des comptes   | `comptes.html`          | Lien naturel après le client                       |
| Détail d'un compte  | `detail-compte.html`    | Solde, historique transactions, relevé             |
| Ouverture de compte | `ouverture-compte.html` | Flux clé : créer un compte pour un client existant |

> `fermeture-compte.html` et `blocage-compte.html` peuvent rester accessibles
> mais pas mis en avant dans la démo.

---

### 5. Opérations de caisse _(cœur d'une microfinance)_

| Page                    | Fichier                   | Pourquoi                   |
| ----------------------- | ------------------------- | -------------------------- |
| Versement               | `versement.html`          | Dépôt d'argent             |
| Retrait                 | `retrait.html`            | Retrait d'argent           |
| Virement                | `virement.html`           | Virement entre comptes     |
| Historique transactions | `historique.html`         | Voir toutes les opérations |
| Détail transaction      | `detail-transaction.html` | Consulter le détail        |

> `caisse.html`, `guichet.html`, `fermeture-caisse.html` sont secondaires
> pour une démo — à montrer rapidement ou ignorer.

---

### 6. Sécurité _(fonctionnel + démontrable)_

| Page                | Fichier             | Pourquoi                        |
| ------------------- | ------------------- | ------------------------------- |
| Rôles & Permissions | `securite.html`     | Montre la granularité des accès |
| Utilisateurs        | `utilisateurs.html` | Activation des accès clients    |

---

### 7. Paramétrage minimal

| Page     | Fichier         | Pourquoi                                        |
| -------- | --------------- | ----------------------------------------------- |
| Agences  | `agences.html`  | Contexte institutionnel, agence de rattachement |
| Produits | `produits.html` | Types de comptes proposés                       |

---

## 🟡 À CONSERVER MAIS MASQUER dans la sidebar MVP

Ces pages existent, elles peuvent fonctionner, mais **ne pas les mettre en avant** pendant la démo.
Les laisser dans le code mais retirer de la navigation principale.

| Section           | Pages à masquer                                                                                                                                                                                                                                                              |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Caisse**        | `caisse.html`, `guichet.html`, `fermeture-caisse.html`                                                                                                                                                                                                                       |
| **Comptes**       | `blocage-compte.html`, `fermeture-compte.html`                                                                                                                                                                                                                               |
| **Cartes Visa**   | `cartes.html`, `detail-carte.html`, `demande-carte.html`, `opposition-carte.html`, `paiement-carte.html` — **Section entière**                                                                                                                                               |
| **Épargne**       | `comptes-a-terme.html`, `interets-courus.html` — **Section entière**                                                                                                                                                                                                         |
| **Crédits**       | `credit-simulation.html`, `credit-demandes.html`, `credit-detail.html`, `credit-suivi.html` — **Section entière**                                                                                                                                                            |
| **Agios & Frais** | `agios.html`, `frais-tenue-compte.html`, `penalite-decouvert.html`, `execution-prelevements.html` — **Section entière**                                                                                                                                                      |
| **Paramétrage**   | `commissions.html`, `historique-frais.html`, `calendrier-jours-feries.html`, `personnel-create.html`, `parametres-systeme.html`, `cache.html`                                                                                                                                |
| **Sécurité**      | `sessions-actives.html`, `parametres-2fa.html`                                                                                                                                                                                                                               |
| **Notifications** | `notifications.html`, `notifications-gestion.html`, `notifications-preferences.html`                                                                                                                                                                                         |
| **Pilotage**      | `direction.html`, `statistiques-kpi.html`, `indicateurs-temps-reel.html`, `reporting.html`, `rapport-operationnel.html`, `rapport-financier.html`, `rapport-clients.html`, `rapport-credits.html`, `rapport-caisse.html`, `export-rapport.html`, `rapport-personnalise.html` |

---

## 🔴 À SUPPRIMER de la navigation (trop avancé, hors scope MVP)

Ces modules ne correspondent pas au cœur du produit pour une première démo.
**Garder les fichiers** (ne pas les supprimer du disque) mais **retirer entièrement les sections** de la sidebar.

| Section complète à retirer                                                                                                                                                                               | Raison                                                          |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| **Conformité & LCB-FT** (`conformite.html`, `sar-liste.html`, `sar-creer.html`, `sar-detail.html`, `reclamations-backoffice.html`, `verification-pep.html`, `liste-pep.html`)                            | Module réglementaire avancé, incomplet, distrait de l'essentiel |
| **RGPD** (`rgpd-consentement.html`, `rgpd-export.html`, `rgpd-suppression.html`)                                                                                                                         | Hors périmètre MVP microfinance africaine                       |
| **Exceptions & Escalades** (`derogation-creer.html`, `derogation-liste.html`, `derogation-decision.html`, `escalade-liste.html`, `escalade-creer.html`, `escalade-traiter.html`, `regles-escalade.html`) | Workflow secondaire non connecté au backend                     |
| **Rapport BCEAO** (`rapport-bceao.html`)                                                                                                                                                                 | Reporting réglementaire spécifique, hors MVP                    |
| **Alertes LCB-FT** (`alertes-lcbft-creer.html`, `alertes-lcbft-traiter.html`)                                                                                                                            | Trop spécialisé pour une démo générale                          |
| **Mobile Money** (`mobile-money.html`, `transactions-mm.html`, `retrait-mm.html`)                                                                                                                        | Sous-système distinct, non intégré                              |
| **Annulation** (`annulation.html`)                                                                                                                                                                       | Action secondaire                                               |
| **Journal d'audit** (`audit.html`)                                                                                                                                                                       | Technique, pas impactant en démo                                |
| **Bénéficiaires** (`beneficiaires.html`)                                                                                                                                                                 | Dépend d'un flux virement non prioritaire                       |
| **Validation 4-eyes** (`validation.html`)                                                                                                                                                                | Workflow avancé à montrer en bonus seulement                    |
| **Rapport personnalisé** (`rapport-personnalise.html`)                                                                                                                                                   | Complexité inutile pour la démo                                 |

---

## 📋 Navigation MVP simplifiée (nouvelle sidebar)

```
├── 🏠 Tableau de bord
│   └── Dashboard
│
├── 👥 Clients
│   ├── Liste clients
│   ├── Nouveau client
│   ├── Détail client
│   └── Validation KYC
│
├── 🏦 Comptes
│   ├── Liste des comptes
│   ├── Ouverture de compte
│   └── Détail compte
│
├── 💸 Opérations
│   ├── Versement
│   ├── Retrait
│   ├── Virement
│   └── Historique
│
├── ⚙️  Paramétrage
│   ├── Agences
│   └── Produits
│
└── 🔐 Sécurité
    ├── Rôles & permissions
    └── Utilisateurs
```

**Total : ~18 pages** contre 101 actuellement dans le projet.

---

## 🗓️ Plan d'action 6 jours

| Jour             | Priorité                                                                           |
| ---------------- | ---------------------------------------------------------------------------------- |
| **26/06 (auj.)** | ✅ Architecture pages Sécurité terminée                                            |
| **27/06**        | Nettoyer la sidebar (retirer les sections hors MVP) — puis polish `dashboard.html` |
| **28/06**        | Vérifier le flux complet : login → dashboard → client → compte → opération         |
| **29/06**        | Tests bout en bout, corriger les bugs visuels                                      |
| **30/06**        | Préparer le scénario de démo (données de démo propres en BDD)                      |
| **01/07**        | Répétition générale de la démo, polish final                                       |
| **02/07**        | 🎤 **Présentation**                                                                |

---

## 💡 Recommandations pour la démo le 2 juillet

1. **Préparer un compte admin de démo** avec de vraies données (clients, comptes, transactions)
2. **Scénario de démo en 10 min** :
   - Connexion → Dashboard (chiffres réels)
   - Créer un client (avec CNI uploadée)
   - Valider son KYC
   - Lui ouvrir un compte
   - Faire un versement
   - Voir l'historique
   - Montrer la gestion des rôles
3. **Ne jamais cliquer sur une page masquée** pendant la démo
4. **Mettre le backend en Docker** la veille pour garantir la stabilité

---

> ℹ️ Les fichiers HTML hors MVP restent sur le disque mais sont simplement
> absents de la navigation. Aucune suppression définitive.
