# Couverture Frontend des Endpoints Backend

> Vérification réalisée après ajout des endpoints backend.
> Objectif : identifier les endpoints backoffice qui nécessitent un écran dédié.

## Écrans déjà couverts

| Domaine backend | Endpoints principaux | Écrans frontend |
|---|---|---|
| Authentification | `/api/v1/auth`, `/api/v1/utilisateurs/login` | `login.html`, `otp.html` |
| Clients & KYC | `/api/v1/clients` | `clients.html`, `client-create.html`, `client-detail.html` |
| Comptes | `/api/v1/comptes` | `comptes.html`, `historique.html` |
| Transactions | `/api/v1/transactions/depot`, `/retrait`, `/virement`, `/en-attente` | `versement.html`, `virement.html`, `validation.html`, `historique.html` |
| Caisse | `/api/v1/caisses` | `caisse.html`, `guichet.html` |
| Crédits de base | `/api/v1/credits`, `/api/v1/credits/demandes` | `credit-simulation.html`, `credit-demandes.html`, `credit-detail.html` |
| Produits & agences | `/api/v1/parametrages/produits-*`, `/agences` | `produits.html`, `agences.html` |
| Agios | `/api/v1/agios` | `agios.html` |
| Audit | `/api/v1/audit-logs` | `audit.html` |
| Dashboards | `/api/v1/dashboards`, `/api/v1/statistiques` | `dashboard.html`, `direction.html` |

## Nouveaux écrans ajoutés

| Nouvel écran | Endpoints backend couverts | Usage |
|---|---|---|
| `conformite.html` | `/api/v1/conformite/sar`, `/reclamations`, `/rgpd/*`, `/kyc/expires`, `/pep`, `/alertes-lcbft` | Centre conformité, alertes LCB/FT, SAR, PEP, RGPD et réclamations |
| `securite.html` | `/api/v1/securite/roles`, `/permissions`, `/sessions` | Administration rôles, permissions et sessions |
| `personnel-create.html` | `/api/v1/securite/utilisateurs` | Ajout d’un membre du personnel et préparation de son accès |
| `utilisateurs.html` | `/api/v1/securite/utilisateurs`, `/utilisateurs/{id}`, `/activer-desactiver`, `/sessions` | Gestion complète des comptes utilisateurs |
| `reporting.html` | `/api/v1/reporting/operationnel`, `/financier`, `/clients`, `/credits`, `/caisse`, `/bceao`, `/export`, `/personnalise` | Catalogue et génération de rapports |
| `notifications.html` | `/api/v1/notifications/client/{idClient}`, `/notifications/{id}/lu`, `/notifications/preferences` | Centre de notifications et préférences de canal |
| `beneficiaires.html` | `/api/v1/beneficiaires` | Gestion des bénéficiaires de virement |
| `exceptions.html` | `/api/v1/exceptions/derogations`, `/escalades`, `/regles` | Dérogations, escalades et règles |
| `mobile-money.html` | `/api/v1/transactions/mobile-money/depot`, `/retrait`, `/mobile-money` | Dépôt, retrait et rapprochement mobile money |
| `parametres-systeme.html` | `/api/v1/parametrages/systeme`, `/jours-feries`, `/parametrages/tarification` | Paramètres globaux, jours fériés et tarification |
| `cartes.html` | `/api/v1/cartes-visa` | Liste cartes Visa, statut, opposition et modification |
| `credit-suivi.html` | `/api/v1/credits/{id}/instruire`, `/approuver`, `/garanties`, `/restructurer`, `/passation`, `/echeances-retard` | Suivi avancé crédit, garanties, retard, restructuration |

## Endpoints non transformés en écrans backoffice

| Domaine | Raison |
|---|---|
| `/api/v1/mobile/auth` | Flux destiné à l'application mobile client |
| `/api/v1/mobile/comptes` | Consultation client mobile, pas écran backoffice |
| `/api/v1/mobile/credits` | Demandes et simulation côté client mobile |
| `/api/v1/mobile/virements` | Virement initié par le client mobile |
| `/api/v1/mobile/notifications` | Notifications visibles dans l'application mobile |
| `/api/v1/mobile/profil` | Profil client mobile |
| `/api/v1/mobile/reclamations` | Réclamations côté client mobile, traitées côté backoffice via conformité |

## Navigation mise à jour

Les nouveaux écrans sont ajoutés dans la sidebar centralisée `Frontend/assets/js/app.js` :

| Menu | Nouveaux liens |
|---|---|
| Comptes & Cartes | Cartes Visa, Bénéficiaires |
| Caisse & Opérations | Mobile Money |
| Crédits | Suivi avancé |
| Paramétrage | Ajouter personnel, Utilisateurs, Paramètres système, Sécurité & rôles |
| Pilotage & Audit | Conformité, Dérogations & escalades, Reporting, Notifications |

## Prochaine étape recommandée

Brancher chaque écran sur les endpoints réels via une couche API frontend commune, par exemple :

```text
Frontend/assets/js/api/
  http.js
  conformite.js
  securite.js
  reporting.js
  notifications.js
  comptes.js
  credits.js
```

Cela évitera de disperser les appels `fetch` dans les pages HTML.
