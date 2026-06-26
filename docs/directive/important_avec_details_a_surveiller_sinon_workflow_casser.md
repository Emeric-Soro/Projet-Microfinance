# Récapitulatif de l'intégration Backend des pages du Backoffice

Ce document répertorie l'état de connexion au backend pour toutes les pages définies dans le menu de navigation principal du backoffice (`app.js`), ainsi que les pages spécifiques hors menu.

---

## 🔍 Pages hors-menu navigables depuis l'application (Flux Actifs)

Ces pages ne figurent pas dans la barre de navigation latérale de `app.js`, mais l'utilisateur peut y accéder en cliquant sur des liens ou boutons depuis les tableaux de bord connectés :

1.  **`rapport-financier.html`** (Accessible via le bouton _« Rapport financier → »_ et _« Détail → »_ sur le tableau de bord de direction `direction.html`) : **CONNECTÉ** (Appelle `/api/v1/reporting/financier`).
2.  **`rapport-operationnel.html`** (Accessible via le bouton _« Rapport opérationnel → »_ sur `direction.html` et `dashboard-superviseur.html`) : **CONNECTÉ** (Appelle `/api/v1/reporting/operationnel`).

**Constat :** Il n'existe **aucune page mockée/déconnectée** qui soit accessible directement par un lien depuis les tableaux de bord ou les pages opérationnelles principales actuellement connectées. Toutes les routes navigables depuis l'application active mènent à des pages fonctionnelles et connectées.

---

## 📊 Résumé global (Menu Principal)

| Catégorie               | Pages connectées                                                                                                                                               | Pages déconnectées / Mockées                                                                |
| :---------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------ |
| **Tableau de bord**     | `dashboard.html`, `direction.html`, `dashboard-superviseur.html`                                                                                               | _Aucune_                                                                                    |
| **Clients**             | `clients.html`, `client-create.html`, `client-detail.html`, `kyc-validation.html`, `blacklist-client.html`                                                     | _Aucune_                                                                                    |
| **Comptes**             | `comptes.html`, `detail-compte.html`, `blocage-compte.html`, `fermeture-compte.html`, `ouverture-compte.html`                                                  | _Aucune_                                                                                    |
| **Caisse & Opérations** | `caisse.html`, `guichet.html`, `virement.html`, `validation.html`, `historique.html`, `detail-transaction.html`, `annulation.html`, `export-transactions.html` | _Aucune_                                                                                    |
| **Crédits**             | _Aucune_                                                                                                                                                       | `credit-simulation.html`, `credit-demandes.html`, `credit-detail.html`, `credit-suivi.html` |
| **Paramétrage**         | `personnel-create.html`, `agences.html`                                                                                                                        | `produits.html`                                                                             |
| **Sécurité**            | `securite.html`, `utilisateurs.html`                                                                                                                           | `audit.html` (Journal d'audit)                                                              |

---

## 📂 Pages hors menu principal (Statut de connexion)

Voici l'analyse des pages du projet qui ne figurent pas directement dans le menu de navigation principal de `app.js` :

### 🔗 Pages Hors-Menu CONNECTÉES au Backend

Ces pages disposent de scripts JS actifs et effectuent des requêtes réelles vers le backend :

- **Rapports** : `rapport-financier.html`, `rapport-operationnel.html`, `rapport-bceao.html`, `rapport-caisse.html`, `rapport-clients.html`, `rapport-credits.html`, `rapport-personnalise.html`, `export-rapport.html`
- **Conformité (Détails)** : `alertes-lcbft-creer.html`, `alertes-lcbft-traiter.html`, `verification-pep.html`, `liste-pep.html`
- **Dérogations & Escalades** : `derogation-creer.html`, `derogation-decision.html`, `derogation-liste.html`, `escalade-creer.html`, `escalade-detail.html`, `escalade-liste.html`, `escalade-traiter.html`, `regles-escalade.html`
- **Comptes & Cartes** : `cartes.html`, `demande-carte.html`, `detail-carte.html`, `opposition-carte.html`, `paiement-carte.html`, `comptes-a-terme.html`, `beneficiaires.html`
- **Frais & Tarification** : `commissions.html`, `frais-tenue-compte.html`, `historique-frais.html`, `penalite-decouvert.html`, `interets-courus.html`
- **Autres Opérations** : `retrait.html`, `versement.html`, `retrait-mm.html`, `transactions-mm.html`, `execution-prelevements.html`, `fermeture-caisse.html`
- **Pilotage & Sécurité** : `sessions-actives.html`, `statistiques-kpi.html`, `indicateurs-temps-reel.html`, `parametres-2fa.html`, `reclamations-backoffice.html`
- **Divers** : `rgpd-consentement.html`, `rgpd-export.html`, `rgpd-suppression.html`, `sar-creer.html`, `sar-detail.html`, `sar-liste.html`

### 🚫 Pages Hors-Menu NON CONNECTÉES (Mockées / Statiques / Orphelines)

Ces pages sont uniquement visuelles et ne communiquent pas avec le serveur. Elles sont également « orphelines » (aucun lien n'y mène depuis les flux actifs de l'application) :

- **`agios.html`** (Frais, agios, pénalités) : Contient une animation de barre de progression factice pour simuler des prélèvements sans appel API.
- **`conformite.html`** (Centre conformité) : Un hub / tableau de bord visuel dont toutes les données de la table et des compteurs sont codées en dur. _(Note: Ses sous-pages comme alertes-lcbft-traiter sont cependant connectées)_.
- **`reporting.html`** (Reporting principal) : Simple page de navigation statique contenant des liens vers les autres rapports.
- **`mobile-money.html`** (Opérations Mobile Money) : Page d'accueil statique du module mobile money.
- **`exceptions.html`** (Gestion des exceptions) : Page de log/visualisation statique (liée uniquement depuis la création de dérogations/escalades).
- **`notifications.html`** (Hub notifications) : Version statique de la liste des notifications.
- **`parametres-systeme.html`** (Paramètres système) : Formulaire statique sans bouton d'enregistrement opérationnel.
- **`cache.html`** (Outils de cache) : Page utilitaire non connectée.

---

## 🚫 Pages du Menu Principal Mockées (À intégrer)

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

## 🔗 Pages du Menu Principal Connectées (Détails)

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
