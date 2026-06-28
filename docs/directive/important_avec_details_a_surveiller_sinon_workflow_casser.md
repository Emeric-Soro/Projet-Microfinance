# Récapitulatif de l'intégration Backend — MIS À JOUR (28/06/2026)

**État final : 100% des pages connectées au backend.** Aucune page mockée ne subsiste.
Toutes les 101 pages HTML du projet contiennent des appels API réels.

---

## ✅ Toutes les pages sont CONNECTÉES

Il n'existe **aucune page mockée/déconnectée** dans le projet. Toutes les routes navigables depuis l'application active mènent à des pages fonctionnelles et connectées.

### Objets API disponibles dans `app.js` (19 objets)

SF, Auth, Clients, Comptes, Cartes, Beneficiaires, Transactions, Caisses, Dashboards,
Conformite, Securite, Agences, Credits, ProduitsCredit, ProduitsEpargne, AuditLogs,
Notifications, Tarification, ParametrageSysteme

---

## 📊 Sidebar MVP (18 pages — 6 sections)

| Section          | Pages MVP                                                                     |
| :--------------- | :---------------------------------------------------------------------------- |
| Tableau de bord  | `dashboard.html`                                                              |
| Clients          | `clients.html`, `client-create.html`, `kyc-validation.html`                  |
| Comptes          | `comptes.html`, `ouverture-compte.html`                                       |
| Opérations       | `versement.html`, `retrait.html`, `virement.html`, `historique.html`          |
| Paramétrage      | `agences.html`, `produits.html`                                               |
| Sécurité         | `securite.html`, `utilisateurs.html`                                          |

---

## 📂 Pages hors menu (connectées mais masquées pour MVP)

### Tableau de bord
- `direction.html` — KPIs direction via `/api/v1/statistiques/kpi`
- `dashboard-superviseur.html` — Dashboard supervisé via `/api/v1/dashboards/agence`

### Clients
- `client-detail.html` — Détail + actions client via `Clients.obtenir()`
- `blacklist-client.html` — Liste noire via `/api/v1/clients/blacklist`

### Comptes
- `detail-compte.html` — Solde, historique, relevé via `Comptes`
- `blocage-compte.html` — Blocage/Déblocage via `Comptes.bloquer()`
- `fermeture-compte.html` — Clôture via `Comptes.cloturer()`

### Opérations
- `caisse.html` — État caisse via `Caisses`
- `guichet.html` — Dépôts/Retraits via `Transactions`
- `validation.html` — Validation 4-eyes via `/api/v1/transactions/{ref}/approbation`
- `detail-transaction.html` — Détail opération
- `annulation.html` — Annulation opération
- `export-transactions.html` — Export CSV

### Crédits (4 pages)
- `credit-simulation.html` — Simulation + demande via `Credits.simuler()` + `ProduitsCredit.actifs()`
- `credit-demandes.html` — Liste demandes via `Credits.listerDemandes()`
- `credit-detail.html` — Dossier crédit + échéancier via `Credits.obtenir()`
- `credit-suivi.html` — Suivi retard/garanties via `Credits.echeancesRetard()`

### Paramétrage
- `personnel-create.html` — Création personnel via `Securite`

### Sécurité
- `audit.html` — Journal d'audit via `AuditLogs.lister()`

### Rapports (hors menu)
- `rapport-financier.html` — via `/api/v1/reporting/financier`
- `rapport-operationnel.html` — via `/api/v1/reporting/operationnel`
- `rapport-bceao.html` — via `/api/v1/reporting/bceao`

### Conformité (hors menu)
- `sar-liste.html`, `sar-creer.html`, `sar-detail.html`, `sar-traiter.html` — SAR via `SF.apiFetch`
- `alertes-lcbft-creer.html`, `alertes-lcbft-traiter.html` — LCB-FT via `Conformite`
- `verification-pep.html`, `liste-pep.html` — PEP via `Conformite`
- `reclamations-backoffice.html` — Réclamations
- `derogation-creer.html`, `derogation-liste.html`, `derogation-detail.html` — Dérogations via `SF.apiFetch`
- `escalade-creer.html`, `escalade-liste.html`, `escalade-detail.html`, `escalade-traiter.html` — Escalades

### Autres modules (hors menu)
- `cartes.html`, `detail-carte.html`, `demande-carte.html`, `opposition-carte.html`, `paiement-carte.html` — Cartes Visa
- `commissions.html`, `frais-tenue-compte.html`, `historique-frais.html`, `penalite-decouvert.html` — Tarification
- `sessions-actives.html` — Sessions via `Securite`
- `notifications.html` — Notifications via `Notifications`
- `parametres-systeme.html` — Paramètres via `ParametrageSysteme`
- `cache.html` — Cache tarification via `Tarification`
- `rgpd-consentement.html`, `rgpd-export.html`, `rgpd-suppression.html` — RGPD
