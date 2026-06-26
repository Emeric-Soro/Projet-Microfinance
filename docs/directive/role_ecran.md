# 🔐 Matrice des rôles & écrans — MVP Soutra Finance

> Référence pour le développement et la démo du 2 juillet 2026

---

## Vue d'ensemble — 8 rôles conservés

| #   | Rôle                 | Type d'espace               | Priorité démo                    |
| --- | -------------------- | --------------------------- | -------------------------------- |
| 1   | **ADMIN**            | Backoffice complet          | 🔴 Essentiel                     |
| 2   | **GUICHETIER**       | Backoffice filtré           | 🔴 Essentiel                     |
| 3   | **SUPERVISEUR**      | Backoffice filtré           | 🔴 Essentiel (validation 4-eyes) |
| 4   | **AGENT_COMMERCIAL** | Backoffice filtré           | 🔴 Essentiel                     |
| 5   | **AGENT_CREDIT**     | Backoffice filtré           | 🟡 Important                     |
| 6   | **CHEF_AGENCE**      | Backoffice filtré           | 🟡 Important                     |
| 7   | **DIRECTEUR**        | Backoffice filtré (lecture) | 🟢 Bonus                         |
| 8   | **CLIENT**           | Portail e-banking séparé    | 🔴 Essentiel                     |

> **COMPTABLE** → non retenu pour le MVP (pages Agios/Rapports hors scope)

---

---

## 1. 🔑 ADMIN

**Profil** : Administrateur système. Accès total. Gère les rôles, les utilisateurs et le paramétrage.

### Backoffice — Tous les écrans

| Section         | Écran                 | Fichier                  | Accès                |
| --------------- | --------------------- | ------------------------ | -------------------- |
| Tableau de bord | Dashboard             | `dashboard.html`         | ✅ Lecture + KPI     |
| Clients         | Liste clients         | `clients.html`           | ✅ Lecture + Actions |
|                 | Nouveau client        | `client-create.html`     | ✅ Écriture          |
|                 | Détail client         | `client-detail.html`     | ✅ Lecture + Actions |
|                 | Validation KYC        | `kyc-validation.html`    | ✅ Validation        |
|                 | Blacklist             | `blacklist-client.html`  | ✅ Écriture          |
| Comptes         | Liste des comptes     | `comptes.html`           | ✅ Lecture + Actions |
|                 | Ouverture de compte   | `ouverture-compte.html`  | ✅ Écriture          |
|                 | Détail compte         | `detail-compte.html`     | ✅ Lecture           |
| Caisse & Ops    | Caisse                | `caisse.html`            | ✅ Lecture           |
|                 | Guichet               | `guichet.html`           | ✅ Écriture          |
|                 | Versement             | `versement.html`         | ✅ Écriture          |
|                 | Retrait               | `retrait.html`           | ✅ Écriture          |
|                 | Virement              | `virement.html`          | ✅ Écriture          |
|                 | Fermeture caisse      | `fermeture-caisse.html`  | ✅ Écriture          |
|                 | Historique            | `historique.html`        | ✅ Lecture           |
|                 | **Validation 4-eyes** | `validation.html`        | ✅ Validation        |
| Crédits         | Simulation            | `credit-simulation.html` | ✅ Lecture           |
|                 | Demandes              | `credit-demandes.html`   | ✅ Lecture + Actions |
|                 | Dossier crédit        | `credit-detail.html`     | ✅ Lecture           |
|                 | Suivi avancé          | `credit-suivi.html`      | ✅ Lecture           |
| Paramétrage     | Agences               | `agences.html`           | ✅ Écriture          |
|                 | Produits              | `produits.html`          | ✅ Écriture          |
| Sécurité        | Rôles & permissions   | `securite.html`          | ✅ Écriture          |
|                 | Utilisateurs          | `utilisateurs.html`      | ✅ Écriture          |

---

## 2. 🏧 GUICHETIER

**Profil** : Caissier / Agent de guichet. Effectue les opérations du quotidien (dépôts, retraits, virements). Ses opérations sensibles partent en validation 4-eyes.

| Section      | Écran                 | Fichier                 | Accès                                |
| ------------ | --------------------- | ----------------------- | ------------------------------------ |
| Caisse & Ops | Caisse                | `caisse.html`           | ✅ Lecture (état de sa caisse)       |
|              | Guichet               | `guichet.html`          | ✅ Écriture                          |
|              | Versement             | `versement.html`        | ✅ Écriture                          |
|              | Retrait               | `retrait.html`          | ✅ Écriture                          |
|              | Virement              | `virement.html`         | ✅ Écriture                          |
|              | Fermeture caisse      | `fermeture-caisse.html` | ✅ Écriture                          |
|              | Historique            | `historique.html`       | 👁️ Lecture (ses propres opérations)  |
|              | **Validation 4-eyes** | `validation.html`       | 👁️ Lecture (voit ses ops en attente) |

> **Règle** : Le guichetier **voit** ses opérations en attente de validation dans `validation.html`
> mais ne peut pas **valider** lui-même — bouton désactivé.

---

## 3. 🔍 SUPERVISEUR

**Profil** : Contrôle les opérations de caisse. Sa mission principale = **valider ou rejeter** les opérations qui dépassent les seuils.

| Section      | Écran                 | Fichier              | Accès                    |
| ------------ | --------------------- | -------------------- | ------------------------ |
| Caisse & Ops | Caisse                | `caisse.html`        | 👁️ Lecture (vue globale) |
|              | Historique            | `historique.html`    | 👁️ Lecture               |
|              | **Validation 4-eyes** | `validation.html`    | ✅ **Valider / Rejeter** |
| Clients      | Liste clients         | `clients.html`       | 👁️ Lecture               |
|              | Détail client         | `client-detail.html` | 👁️ Lecture               |

> **Page principale du SUPERVISEUR** : `validation.html`

---

## 4. 💼 AGENT_COMMERCIAL

**Profil** : Chargé de clientèle / Agent terrain. Inscrit les nouveaux clients, ouvre les comptes, soumet le KYC.

| Section | Écran               | Fichier                 | Accès                |
| ------- | ------------------- | ----------------------- | -------------------- |
| Clients | Liste clients       | `clients.html`          | ✅ Lecture + Créer   |
|         | Nouveau client      | `client-create.html`    | ✅ Écriture          |
|         | Détail client       | `client-detail.html`    | ✅ Lecture + Édition |
|         | Validation KYC      | `kyc-validation.html`   | ✅ Soumettre KYC     |
|         | Blacklist           | `blacklist-client.html` | 👁️ Lecture           |
| Comptes | Liste des comptes   | `comptes.html`          | ✅ Lecture           |
|         | Ouverture de compte | `ouverture-compte.html` | ✅ Écriture          |
|         | Détail compte       | `detail-compte.html`    | 👁️ Lecture           |

---

## 5. 💳 AGENT_CREDIT

**Profil** : Analyste crédit. Traite les demandes de prêt, fait les simulations, suit les remboursements.

| Section | Écran          | Fichier                  | Accès                              |
| ------- | -------------- | ------------------------ | ---------------------------------- |
| Clients | Liste clients  | `clients.html`           | 👁️ Lecture                         |
|         | Détail client  | `client-detail.html`     | 👁️ Lecture (voir profil financier) |
| Crédits | Simulation     | `credit-simulation.html` | ✅ Écriture                        |
|         | Demandes       | `credit-demandes.html`   | ✅ Traiter les demandes            |
|         | Dossier crédit | `credit-detail.html`     | ✅ Lecture + Écriture              |
|         | Suivi avancé   | `credit-suivi.html`      | ✅ Lecture                         |

---

## 6. 🏢 CHEF_AGENCE

**Profil** : Responsable d'agence. Supervise toutes les activités, valide les opérations importantes, accès dashboard local.

| Section         | Écran                 | Fichier                  | Accès                           |
| --------------- | --------------------- | ------------------------ | ------------------------------- |
| Tableau de bord | Dashboard             | `dashboard.html`         | ✅ KPI de son agence            |
| Clients         | Liste clients         | `clients.html`           | ✅ Lecture + Actions            |
|                 | Nouveau client        | `client-create.html`     | ✅ Écriture                     |
|                 | Détail client         | `client-detail.html`     | ✅ Lecture + Actions            |
|                 | Validation KYC        | `kyc-validation.html`    | ✅ Valider                      |
|                 | Blacklist             | `blacklist-client.html`  | ✅ Écriture                     |
| Comptes         | Liste des comptes     | `comptes.html`           | ✅ Lecture + Actions            |
|                 | Ouverture de compte   | `ouverture-compte.html`  | ✅ Écriture                     |
|                 | Détail compte         | `detail-compte.html`     | ✅ Lecture                      |
| Caisse & Ops    | Caisse                | `caisse.html`            | ✅ Lecture (toutes les caisses) |
|                 | Historique            | `historique.html`        | ✅ Lecture                      |
|                 | **Validation 4-eyes** | `validation.html`        | ✅ **Valider / Rejeter**        |
| Crédits         | Simulation            | `credit-simulation.html` | 👁️ Lecture                      |
|                 | Demandes              | `credit-demandes.html`   | ✅ Approuver / Refuser          |
|                 | Dossier crédit        | `credit-detail.html`     | ✅ Lecture                      |
|                 | Suivi avancé          | `credit-suivi.html`      | ✅ Lecture                      |

---

## 7. 📊 DIRECTEUR

**Profil** : Direction générale. Vision globale, aucune action opérationnelle directe. Consulte les indicateurs.

| Section         | Écran             | Fichier                | Accès            |
| --------------- | ----------------- | ---------------------- | ---------------- |
| Tableau de bord | Dashboard         | `dashboard.html`       | ✅ KPI globaux   |
| Clients         | Liste clients     | `clients.html`         | 👁️ Lecture seule |
|                 | Détail client     | `client-detail.html`   | 👁️ Lecture seule |
| Comptes         | Liste des comptes | `comptes.html`         | 👁️ Lecture seule |
|                 | Détail compte     | `detail-compte.html`   | 👁️ Lecture seule |
| Caisse & Ops    | Historique        | `historique.html`      | 👁️ Lecture seule |
| Crédits         | Demandes          | `credit-demandes.html` | 👁️ Lecture seule |
|                 | Suivi avancé      | `credit-suivi.html`    | 👁️ Lecture seule |

> Le DIRECTEUR **ne voit pas** Caisse, Guichet, Versement, Retrait, Virement — il ne fait pas d'opérations.

---

## 8. 📱 CLIENT (Portail séparé)

**Espace** : `ClientPortal/` — Application e-banking distincte du backoffice.
**Backend** : endpoints `/api/v1/mobile/**`

| Catégorie  | Écran                   | Fichier                        | Accès                         |
| ---------- | ----------------------- | ------------------------------ | ----------------------------- |
| Auth       | Connexion               | `login.html`                   | ✅                            |
|            | Inscription             | `register.html`                | ✅                            |
|            | Validation OTP          | `otp.html`                     | ✅                            |
| Accueil    | Mon tableau de bord     | `dashboard.html`               | ✅ Soldes, mouvements récents |
| Comptes    | Mes comptes             | `comptes.html`                 | ✅ Lecture                    |
|            | Détail d'un compte      | `detail-compte.html`           | ✅ Lecture + relevé           |
|            | Historique transactions | `historique-transactions.html` | ✅ Lecture                    |
| Opérations | Faire un virement       | `virement.html`                | ✅ Écriture                   |
| Crédits    | Mes crédits             | `credits.html`                 | ✅ Lecture                    |
|            | Détail crédit           | `detail-credit.html`           | ✅ Lecture                    |
|            | Simulation              | `simulation-credit.html`       | ✅ Lecture                    |
| Épargne    | Mon épargne             | `epargne.html`                 | ✅ Lecture                    |
| Profil     | Mon profil              | `profil.html`                  | ✅ Lecture + Édition          |
|            | Mes documents           | `documents.html`               | ✅ Upload + Lecture           |
| Services   | Mes réclamations        | `reclamations.html`            | ✅ Lecture                    |
|            | Créer réclamation       | `reclamations-creer.html`      | ✅ Écriture                   |
|            | Notifications           | `notifications-client.html`    | ✅ Lecture                    |

---

## 📋 Matrice globale d'accès (résumé)

| Écran                 | ADMIN | GUICHETIER | SUPERVISEUR | AGENT_COM | AGENT_CREDIT | CHEF_AGENCE | DIRECTEUR |
| --------------------- | :---: | :--------: | :---------: | :-------: | :----------: | :---------: | :-------: |
| Dashboard             |  ✅   |     ❌     |     ❌      |    ❌     |      ❌      |     ✅      |    ✅     |
| Clients               |  ✅   |     ❌     |     👁️      |    ✅     |      👁️      |     ✅      |    👁️     |
| KYC validation        |  ✅   |     ❌     |     ❌      |    ✅     |      ❌      |     ✅      |    ❌     |
| Comptes               |  ✅   |     ❌     |     ❌      |    ✅     |      ❌      |     ✅      |    👁️     |
| Caisse                |  ✅   |     ✅     |     👁️      |    ❌     |      ❌      |     ✅      |    ❌     |
| Versement/Retrait     |  ✅   |     ✅     |     ❌      |    ❌     |      ❌      |     ❌      |    ❌     |
| Virement              |  ✅   |     ✅     |     ❌      |    ❌     |      ❌      |     ❌      |    ❌     |
| Historique            |  ✅   |     👁️     |     👁️      |    ❌     |      ❌      |     ✅      |    👁️     |
| **Validation 4-eyes** |  ✅   |     👁️     |     ✅      |    ❌     |      ❌      |     ✅      |    ❌     |
| Crédits               |  ✅   |     ❌     |     ❌      |    ❌     |      ✅      |     ✅      |    👁️     |
| Paramétrage           |  ✅   |     ❌     |     ❌      |    ❌     |      ❌      |     ❌      |    ❌     |
| Sécurité              |  ✅   |     ❌     |     ❌      |    ❌     |      ❌      |     ❌      |    ❌     |

> ✅ = Accès complet (lecture + écriture) &nbsp;&nbsp; 👁️ = Lecture seule &nbsp;&nbsp; ❌ = Pas d'accès

---

## 🛠️ Implémentation technique

Le filtrage se fait dans **`app.js`** au moment de la construction de la sidebar :

```javascript
// Pseudo-code — à implémenter dans app.js
const user = SF.getUser(); // { roles: ["GUICHETIER"] }
const role = user.roles[0]; // "GUICHETIER"

// Filtrer navGroups selon le rôle
const ACCESS = {
  GUICHETIER: ["Caisse & Opérations"],
  SUPERVISEUR: ["Caisse & Opérations", "Clients"],
  AGENT_COMMERCIAL: ["Clients", "Comptes"],
  AGENT_CREDIT: ["Clients", "Crédits"],
  CHEF_AGENCE: [
    "Tableau de bord",
    "Clients",
    "Comptes",
    "Caisse & Opérations",
    "Crédits",
  ],
  DIRECTEUR: [
    "Tableau de bord",
    "Clients",
    "Comptes",
    "Caisse & Opérations",
    "Crédits",
  ],
  ADMIN: "ALL",
};
```
