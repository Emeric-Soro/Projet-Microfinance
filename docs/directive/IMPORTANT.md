# Récapitulatif de l'intégration Backend des pages du Backoffice

Ce document répertorie l'état de connexion au backend pour toutes les pages du backoffice. **Le sidebar MVP (2 juillet 2026) affiche 18 pages** dans 6 sections. Les autres pages existent et sont connectées mais masquées du menu principal.

---

## 📊 Résumé global

### Sidebar MVP (18 pages — 6 sections)

| Section               | Pages (MVP)                                                              | Pages hors-menu (connectées mais masquées)                                                  |
| :-------------------- | :----------------------------------------------------------------------- | :------------------------------------------------------------------------------------------ |
| **Tableau de bord**   | `dashboard.html`                                                         | `direction.html`, `dashboard-superviseur.html`                                              |
| **Clients**           | `clients.html`, `client-create.html`, `kyc-validation.html`             | `client-detail.html`, `blacklist-client.html`                                               |
| **Comptes**           | `comptes.html`, `ouverture-compte.html`                                  | `detail-compte.html`, `blocage-compte.html`, `fermeture-compte.html`                       |
| **Opérations**        | `versement.html`, `retrait.html`, `virement.html`, `historique.html`     | `caisse.html`, `guichet.html`, `validation.html`, `detail-transaction.html`, `annulation.html`, `export-transactions.html` |
| **Paramétrage**       | `agences.html`, `produits.html`                                          | `personnel-create.html`                                                                     |
| **Sécurité**          | `securite.html`, `utilisateurs.html`                                     | `audit.html`                                                                                |

_Toutes les 101 pages existantes sont connectées au backend. Le sidebar n'affiche que les 18 pages MVP pour la démonstration du 2 juillet._

---

## ✅ Pages Previously Mocked — Now Connected (28/06/2026)

All previously mocked pages have been connected to the backend API.

### 1. Section Crédits (4 pages) — CONNECTÉES

- **`credit-simulation.html`** : Utilise `ProduitsCredit.actifs()`, `Credits.simuler()`, `Credits.soumettreDemande()`.
- **`credit-demandes.html`** : Utilise `Credits.listerDemandes()`, `Credits.deciderDemande()`.
- **`credit-detail.html`** : Utilise `Credits.obtenir()`, `Credits.echeancier()`, `Credits.decaisser()`, `Credits.rembourser()`.
- **`credit-suivi.html`** : Utilise `Credits.echeancesRetard()`, `Credits.instruire()`, `Credits.approuver()`, `Credits.passerSouffrance()`, `Credits.restructurer()`, `Credits.ajouterGaranties()`.

### 2. Paramétrage — Produits (`produits.html`) — CONNECTÉE

- Utilise `ProduitsCredit.lister/creer/supprimer()`, `ProduitsEpargne.lister/creer/supprimer()`.

### 3. Sécurité — Journal d'audit (`audit.html`) — CONNECTÉE

- Utilise `AuditLogs.lister()`.

### 4. Notifications & Paramétrage Système — CONNECTÉES

- **`notifications.html`** : Utilise `Notifications.listerClient()`, `Notifications.marquerLue()`.
- **`parametres-systeme.html`** : Utilise `ParametrageSysteme.obtenir()`, `ParametrageSysteme.mettreAJour()`.
- **`cache.html`** : Utilise `Tarification.refreshCache()`.

### 5. Pages hors-menu créées (28/06/2026)

- **`sar-traiter.html`** : Page de traitement des SAR (TRAITEE/REJETE). Connectée via `SF.apiFetch('/api/v1/conformite/sar/{id}')`.
- **`derogation-detail.html`** : Page de détail des dérogations. Connectée via `SF.apiFetch('/api/v1/exceptions/derogations/{id}')`.

---

## 🚫 Previous Mock Status (Historical — Resolved)

---

## 🔗 Pages connectées (Détails de l'intégration)

### 1. Tableau de bord

- **`dashboard.html`** : Appelle `Dashboards.agence(null, 'JOUR')` et `Dashboards.graphiques()` pour charger les indicateurs de l'agence et les graphiques.
- **`direction.html` & `dashboard-superviseur.html`** : Chargent les KPIs globaux via `/api/v1/statistiques/kpi` et les rapports opérationnels/financiers.

### 2. Clients

- **`clients.html` & `client-detail.html`** : Recherche et chargement via `Clients.lister()` et `Clients.obtenir()`. Upload des documents de KYC et modification du statut.
- **`client-create.html`** : Création complète via `Clients.creer()`.
- **`kyc-validation.html`** : Liste les clients à valider et soumet la décision via `Clients.traiterKyc()`.
- **`blacklist-client.html`** : Récupère la liste noire `/api/v1/clients/blacklist`, permet d'ajouter/retirer via `/api/v1/clients/{id}/blacklist`.

### 3. Comptes

- **`comptes.html` & `detail-compte.html`** : Lister les comptes et obtenir les détails via `Comptes.lister()`.
- **`blocage-compte.html` & `fermeture-compte.html`** : Bloquer/Débloquer et Clôturer les comptes via `Comptes.bloquer()` / `Comptes.cloturer()`.
- **`ouverture-compte.html`** : Vérifie l'éligibilité et ouvre un compte via `Comptes.ouvrir()`.

### 4. Opérations

- **`caisse.html`** : Gère l'ouverture, l'état et la fermeture de caisse via `Caisses`.
- **`guichet.html`** : Dépôts, retraits (espèces et carte) via `Transactions`.
- **`virement.html`** : Virements de compte à compte via `Transactions.virement()`.
- **`validation.html`** : Approbation ou rejet des opérations par le superviseur (4-eyes) via `/api/v1/transactions/{ref}/approbation`.
- **`historique.html` & `detail-transaction.html` / `annulation.html`** : Recherche historique, réédition des reçus et opérations d'annulation.

### 5. Paramétrage

- **`personnel-create.html`** : Connecté à `/api/v1/securite/utilisateurs/collaborateur` pour la création d'accès du personnel.
- **`agences.html`** : Connecté à `Agences` pour la liste, création, édition et désactivation.

### 6. Sécurité

- **`securite.html`** : Rôles et sessions actives connectés via `Securite` service.
- **`utilisateurs.html`** : Création d'accès e-banking et activation/désactivation connectées via `Securite`.

### 7. Rapports (Hors menu principal)

- **`rapport-financier.html` & `rapport-operationnel.html`** : Connectés aux endpoints de reporting `/api/v1/reporting/financier` et `/api/v1/reporting/operationnel`.
- _Note d'ergonomie :_ Bien que connectées au backend, ces pages ne sont pas accessibles via le menu principal dans `app.js` (aucun lien n'est présent dans la sidebar).

---

## 🛠️ Bugs et éléments à finaliser

1.  **Visibilité des modales (Résolu)** : Les pages `utilisateurs.html`, `securite.html`, `detail-compte.html` et `detail-carte.html` avaient des règles de styles locales conflictuelles masquant les modales overlays. Ils ont tous été nettoyés et réalignés sur le système de modales standard (`openModal` / `closeModal` avec la classe `.active`).
2.  **Navigation des rapports** : Pour faciliter l'accès aux rapports financier et opérationnel, il faudrait ajouter des liens dans le menu de navigation de `app.js`.
