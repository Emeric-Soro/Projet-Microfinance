# Récapitulatif de l'intégration Backend des pages du Backoffice

Ce document répertorie l'état de connexion au backend pour toutes les pages définies dans le menu de navigation principal du backoffice (`app.js`), ainsi que les pages spécifiques mentionnées.

---

## 📊 Résumé global

| Catégorie                | Pages connectées                                                                                                                                               | Pages déconnectées / Mockées                                                                |
| :----------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------ |
| **Tableau de bord**      | `dashboard.html`, `direction.html`, `dashboard-superviseur.html`                                                                                               | _Aucune_                                                                                    |
| **Clients**              | `clients.html`, `client-create.html`, `client-detail.html`, `kyc-validation.html`, `blacklist-client.html`                                                     | _Aucune_                                                                                    |
| **Comptes**              | `comptes.html`, `detail-compte.html`, `blocage-compte.html`, `fermeture-compte.html`, `ouverture-compte.html`                                                  | _Aucune_                                                                                    |
| **Caisse & Opérations**  | `caisse.html`, `guichet.html`, `virement.html`, `validation.html`, `historique.html`, `detail-transaction.html`, `annulation.html`, `export-transactions.html` | _Aucune_                                                                                    |
| **Crédits**              | _Aucune_                                                                                                                                                       | `credit-simulation.html`, `credit-demandes.html`, `credit-detail.html`, `credit-suivi.html` |
| **Paramétrage**          | `personnel-create.html`, `agences.html`                                                                                                                        | `produits.html`                                                                             |
| **Sécurité**             | `securite.html`, `utilisateurs.html`                                                                                                                           | `audit.html` (Journal d'audit)                                                              |
| **Rapports (Hors menu)** | `rapport-financier.html`, `rapport-operationnel.html`                                                                                                          | _Aucune_                                                                                    |

---

## 🚫 Pages déconnectées / Mockées (À intégrer)

Ces pages n'appellent actuellement aucune API backend et n'ont pas de fonctions de requête définies dans `app.js`.

### 1. Section Crédits (4 pages)

- **`credit-simulation.html` (Simulation & Demande)** : Calcule la simulation localement en JS pur. L'envoi du dossier (`submitCreditRequest`) affiche seulement un toast de confirmation mocké.
- **`credit-demandes.html` (Demandes de crédit)** : Affiche des demandes factices codées en dur dans le tableau. Les boutons d'action (Accorder/Refuser) affichent des toasts de confirmation mockés.
- **`credit-detail.html` (Échéancier)** : Affiche un tableau d'amortissement et des métriques entièrement codés en dur pour un client fictif. Les modales de décaissement et remboursement affichent des toasts mockés.
- **`credit-suivi.html` (Suivi avancé)** : Les KPIs et les tableaux de retard ou garanties sont codés en dur. Les formulaires d'actions affichent des toasts mockés.
- _Endpoints backend correspondants disponibles :_
  - `CreditController.java` -> `/api/v1/credits`
  - `DemandeCreditController.java` -> `/api/v1/credits/demandes`

### 2. Paramétrage — Produits (`produits.html`)

- **État :** Le tableau des produits (crédits et épargne) est codé en dur dans le HTML. La soumission de la création d'un produit affiche un toast de réussite local sans appeler d'API.
- _Endpoints backend correspondants disponibles :_
  - `ParametrageCreditController.java` -> `/api/v1/parametrages/produits-credit`
  - `ParametrageEpargneController.java` -> `/api/v1/parametrages/produits-epargne`

### 3. Sécurité — Journal d'audit (`audit.html`)

- **État :** Affiche un historique fixe d'actions d'investigation en dur dans le tableau. Le bouton d'exportation déclenche un message toast de simulation de téléchargement.
- _Endpoints backend correspondants disponibles :_
  - `AuditLogController.java` -> `/api/v1/audit-logs`

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

### 4. Caisse & Opérations

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
