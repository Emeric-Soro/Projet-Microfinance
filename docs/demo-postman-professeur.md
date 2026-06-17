# Parcours de démonstration Postman - Microfinance

Ce parcours est prévu pour une démonstration complète devant le professeur : authentification, caisse, clients, KYC, comptes, transactions, crédit et statistiques.

## 1. Préparer l'environnement

Dans `backend` :

```powershell
docker compose up -d --build backend
docker compose logs -f backend
```

Vérifier dans les logs que le backend démarre et que `DemoDataLoader` prépare les données démo.

Base URL Postman :

```text
http://localhost:8080
```

Compte de démonstration :

```text
login: demo.admin
mot de passe: Demo@12345
```

Comptes bancaires déjà créés par le seeder :

```text
CI23CB000100000001  // compte admin / Awa Kouadio
CI23CB000100000005  // compte Fatoumata Bamba
CI23CB000100000010  // compte Affoue Traore
```

Si le login ne fonctionne pas après plusieurs essais, repartir d'une base propre :

```powershell
docker compose down -v
docker compose up -d --build
```

## 2. Configurer Postman

Créer une collection `Demo Microfinance`.

Dans la collection, créer ces variables :

| Variable | Valeur initiale |
| --- | --- |
| `baseUrl` | `http://localhost:8080` |
| `token` | vide |
| `idUser` | vide |
| `idClient` | vide |
| `seedCompteAdmin` | `CI23CB000100000001` |
| `seedCompteClientA` | `CI23CB000100000005` |
| `seedCompteClientB` | `CI23CB000100000010` |
| `newClientId` | vide |
| `newAccountNumber` | vide |
| `idDemande` | vide |
| `idCredit` | vide |
| `caisseSoldeCourant` | vide |

Pour toutes les requêtes protégées :

```text
Authorization -> Bearer Token -> {{token}}
```

Le fichier Postman prêt à importer est :

```text
docs/Microfinance_Demo_Prof.postman_collection.json
```

Dans cette collection :

- les requêtes `01` à `24` correspondent au parcours professeur dans l'ordre ;
- le dossier `25 - Nouveaux endpoints backend (tous les tests)` reprend tous les nouveaux endpoints ajoutés après les modifications GitHub ;
- les variables communes (`clientId`, `compteNumero`, `userId`, `roleId`, `transactionRef`, etc.) sont déjà préparées pour tester rapidement les nouveaux modules.

## 3. Authentification

### Requête : se connecter

```http
POST {{baseUrl}}/api/v1/utilisateurs/login
```

Body JSON :

```json
{
  "login": "demo.admin",
  "motDePasse": "Demo@12345"
}
```

Dans l'onglet `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("token", body.token);
pm.collectionVariables.set("idUser", body.utilisateur.idUser);
pm.collectionVariables.set("idClient", body.utilisateur.idClient);
```

À expliquer au professeur : l'API retourne un JWT, utilisé ensuite dans toutes les requêtes sécurisées.

## 4. Vérifier les données initiales

### Lister les clients

```http
GET {{baseUrl}}/api/v1/clients?page=0&size=10
```

À montrer : les noms ivoiriens créés par le seeder.

### Lister les produits de crédit

```http
GET {{baseUrl}}/api/v1/parametrages/produits-credit
```

À montrer : `MC-COMMERCE`, `MC-AGRICULTURE`, `PRET-SALARIE`.

### Consulter les KPI

```http
GET {{baseUrl}}/api/v1/statistiques/kpi
```

À montrer : nombre de clients actifs, total des dépôts, crédits en cours.

## 5. Ouvrir une caisse

Avant dépôt ou retrait, l'utilisateur doit ouvrir une caisse.

```http
POST {{baseUrl}}/api/v1/caisses/ouverture
```

Body JSON :

```json
{
  "soldeInitial": 1000000
}
```

Si Postman répond que la caisse est déjà ouverte, ce n'est pas bloquant : continuer avec l'état de caisse.

### Vérifier l'état de caisse

```http
GET {{baseUrl}}/api/v1/caisses/etat
```

Dans `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("caisseSoldeCourant", body.soldeCourant);
```

## 6. Consulter un solde avant opération

```http
GET {{baseUrl}}/api/v1/comptes/{{seedCompteClientA}}/solde
```

À montrer : le solde initial du compte.

## 7. Faire un dépôt

```http
POST {{baseUrl}}/api/v1/transactions/depot
```

Body JSON :

```json
{
  "numCompte": "{{seedCompteClientA}}",
  "montant": 25000,
  "idGuichetier": {{idUser}}
}
```

À expliquer : le dépôt crédite le compte client et augmente le solde de la caisse.

## 8. Faire un retrait

```http
POST {{baseUrl}}/api/v1/transactions/retrait
```

Body JSON :

```json
{
  "numCompte": "{{seedCompteClientA}}",
  "montant": 10000,
  "idGuichetier": {{idUser}}
}
```

À expliquer : le retrait débite le compte client et diminue le solde de la caisse.

## 9. Faire un virement

```http
POST {{baseUrl}}/api/v1/transactions/virement?idGuichetier={{idUser}}
```

Body JSON :

```json
{
  "compteSource": "{{seedCompteClientA}}",
  "compteDestination": "{{seedCompteClientB}}",
  "montant": 15000
}
```

À expliquer : un virement débite un compte et crédite un autre.

## 10. Consulter l'historique du compte

```http
GET {{baseUrl}}/api/v1/transactions/comptes/{{seedCompteClientA}}/historique?page=0&size=20
```

À montrer : les lignes d'écriture `DEBIT` et `CREDIT`.

## 11. Créer un nouveau client

```http
POST {{baseUrl}}/api/v1/clients
```

Body JSON :

```json
{
  "nom": "Akissi",
  "prenom": "Mariam",
  "dateNaissance": "1995-04-12",
  "email": "mariam.akissi.demo@microfin.local",
  "telephone": "+225 07 88 10 10 10",
  "adresse": "Abidjan, Koumassi",
  "profession": "Commercante",
  "employeur": "Marche de Koumassi",
  "typePieceIdentite": "CNI",
  "numeroPieceIdentite": "CNI-CI-DEMO-PROF-001",
  "dateExpirationPieceIdentite": "2031-12-31",
  "photoIdentiteUrl": "demo/kyc/mariam/photo.jpg",
  "justificatifDomicileUrl": "demo/kyc/mariam/domicile.pdf",
  "justificatifRevenusUrl": "demo/kyc/mariam/revenus.pdf",
  "paysNationalite": "Cote d'Ivoire",
  "paysResidence": "Cote d'Ivoire",
  "pep": false
}
```

Dans `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("newClientId", body.idClient);
```

À expliquer : le client est créé, mais son dossier KYC doit être traité avant ouverture de compte.

## 12. Soumettre le KYC du client

```http
PUT {{baseUrl}}/api/v1/clients/{{newClientId}}/kyc
```

Body JSON :

```json
{
  "profession": "Commercante",
  "employeur": "Marche de Koumassi",
  "typePieceIdentite": "CNI",
  "numeroPieceIdentite": "CNI-CI-DEMO-PROF-001",
  "dateExpirationPieceIdentite": "2031-12-31",
  "photoIdentiteUrl": "demo/kyc/mariam/photo.jpg",
  "justificatifDomicileUrl": "demo/kyc/mariam/domicile.pdf",
  "justificatifRevenusUrl": "demo/kyc/mariam/revenus.pdf",
  "paysNationalite": "Cote d'Ivoire",
  "paysResidence": "Cote d'Ivoire",
  "pep": false,
  "dateSoumission": "2026-05-27"
}
```

## 13. Valider le KYC

```http
PUT {{baseUrl}}/api/v1/clients/{{newClientId}}/kyc/decision
```

Body JSON :

```json
{
  "statutKyc": "VALIDE",
  "niveauRisque": "FAIBLE",
  "commentaire": "Dossier complet et conforme pour la demo.",
  "validateurKyc": "demo.admin"
}
```

À expliquer : après validation KYC, le client devient actif.

## 14. Ouvrir un compte au nouveau client

```http
POST {{baseUrl}}/api/v1/comptes
```

Body JSON :

```json
{
  "idClient": {{newClientId}},
  "codeTypeCompte": "EPARGNE",
  "depotInitial": 50000
}
```

Dans `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("newAccountNumber", body.numCompte);
```

À expliquer : le système génère automatiquement le numéro de compte.

## 15. Simuler un crédit

```http
POST {{baseUrl}}/api/v1/credits/simulation
```

Body JSON :

```json
{
  "montant": 300000,
  "taux": 18,
  "duree": 12,
  "methode": "DEGRESSIF"
}
```

À montrer : un tableau d'amortissement théorique.

## 16. Soumettre une demande de crédit

```http
POST {{baseUrl}}/api/v1/credits/demandes
```

Body JSON :

```json
{
  "idClient": {{newClientId}},
  "codeProduitCredit": "MC-COMMERCE",
  "montantDemande": 300000,
  "dureeSouhaitee": 12,
  "objetCredit": "Renforcement du stock de marchandises",
  "idAgentCredit": {{idUser}}
}
```

Dans `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("idDemande", body.idDemande);
```

## 17. Approuver la demande

```http
PUT {{baseUrl}}/api/v1/credits/demandes/{{idDemande}}/decision
```

Body JSON :

```json
{
  "idDemande": {{idDemande}},
  "decision": "APPROUVEE",
  "motifRejet": null
}
```

Dans `Tests`, mettre :

```javascript
const body = pm.response.json();
pm.collectionVariables.set("idCredit", body.idCredit);
```

## 18. Décaisser le crédit

```http
POST {{baseUrl}}/api/v1/credits/{{idCredit}}/decaissement
```

Body JSON :

```json
{
  "numCompteCible": "{{newAccountNumber}}"
}
```

À expliquer : le crédit approuvé est versé sur le compte du client.

## 19. Consulter l'échéancier du crédit

```http
GET {{baseUrl}}/api/v1/credits/{{idCredit}}/echeancier
```

À montrer : les mensualités générées.

## 20. Enregistrer un remboursement

```http
POST {{baseUrl}}/api/v1/credits/{{idCredit}}/remboursement
```

Body JSON :

```json
{
  "montant": 30000
}
```

À expliquer : le remboursement diminue le capital restant dû.

## 21. Revoir les KPI

```http
GET {{baseUrl}}/api/v1/statistiques/kpi
```

À montrer : les chiffres ont évolué après opérations.

## 22. Fermer la caisse

Avant de fermer, refaire :

```http
GET {{baseUrl}}/api/v1/caisses/etat
```

Copier `soldeCourant`, puis :

```http
POST {{baseUrl}}/api/v1/caisses/fermeture
```

Body JSON :

```json
{
  "soldePhysiqueConstate": {{caisseSoldeCourant}}
}
```

À expliquer : si le solde physique est égal au solde informatique, l'écart de caisse est zéro.

## 23. Tester les nouveaux endpoints

Après le parcours principal, ouvrir dans Postman le dossier :

```text
25 - Nouveaux endpoints backend (tous les tests)
```

Ce dossier permet de tester les modules ajoutés par les dernières modifications :

- Paramétrage : agences, produits crédit, produits épargne, système, jours fériés et tarification.
- Comptes avancés : bénéficiaires, cartes Visa, découvert, blocage, déblocage, clôture et relevé.
- Transactions avancées : paiement carte, Mobile Money, validation, rejet, reçu, reversement et export.
- Crédit avancé : instruction, garanties, restructuration, passation et échéances en retard.
- Pilotage : dashboards agence/direction, reporting, exports, statistiques et audit logs.
- Administration : sécurité, rôles, permissions, utilisateurs, sessions et notifications.
- Conformité : SAR, réclamations, RGPD, KYC expirés, PEP, alertes LCB-FT, dérogations et escalades.
- Mobile : authentification, comptes, crédits, virements, profil, notifications, bénéficiaires et opérations.

Conseil pour la démonstration : présenter seulement 2 ou 3 familles en plus du parcours principal, par exemple `Reporting`, `Sécurité` et `Conformité`, pour ne pas perdre le professeur dans 200 requêtes.

## Plan oral conseillé

1. Montrer que l'utilisateur se connecte avec JWT.
2. Montrer le référentiel : clients, produits, agences.
3. Ouvrir la caisse : contrôle métier obligatoire.
4. Faire dépôt, retrait, virement : opérations bancaires.
5. Montrer l'historique : traçabilité comptable.
6. Créer un client : entrée en relation.
7. Valider KYC : conformité.
8. Ouvrir un compte : seulement après KYC valide.
9. Simuler, demander, approuver, décaisser et rembourser un crédit.
10. Terminer avec les KPI et la fermeture de caisse.

## Phrases utiles devant le professeur

- "L'API est sécurisée par JWT : seules les routes publiques comme login et création client sont accessibles sans token."
- "Le workflow caisse empêche un guichetier de faire un dépôt ou retrait sans caisse ouverte."
- "Le KYC bloque l'ouverture de compte tant que le dossier n'est pas validé."
- "Chaque transaction produit des lignes d'écriture, ce qui permet l'historique et l'audit."
- "Le module crédit couvre la simulation, la demande, la décision, le décaissement, l'échéancier et le remboursement."
