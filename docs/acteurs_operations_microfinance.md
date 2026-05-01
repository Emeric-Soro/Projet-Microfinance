# 🏦 Cartographie Complète — Système de Gestion de Microfinance

> Tout ce qui est informatisable : acteurs, modules, opérations et actions.

---

## 📌 Légende

| Icône | Signification |
|-------|--------------|
| ✅ | Déjà prévu dans votre backend actuel |
| 🆕 | À ajouter (spécifique microfinance) |
| ⚙️ | Automatisable (batch / cron) |

---

## 1. LES ACTEURS (Profils Utilisateurs)

### 1.1 Acteurs Internes

| # | Acteur | Rôle | Statut |
|---|--------|------|--------|
| 1 | **Administrateur Système** | Paramétrage global, gestion des droits, sauvegardes | ✅ |
| 2 | **Directeur / DG** | Tableaux de bord, validation des gros crédits, pilotage stratégique | 🆕 |
| 3 | **Chef d'Agence** | Supervision de l'agence, validation des opérations sensibles | 🆕 |
| 4 | **Agent de Crédit** | Montage des dossiers de prêt, suivi terrain, recouvrement | 🆕 |
| 5 | **Caissier / Guichetier** | Dépôts, retraits, encaissements d'échéances | ✅ |
| 6 | **Comptable** | Écritures comptables, clôtures, rapprochements | 🆕 |
| 7 | **Contrôleur Interne / Auditeur** | Vérification des pistes d'audit, conformité | 🆕 |
| 8 | **Responsable RH** | Gestion du personnel, paie, évaluations | 🆕 |

### 1.2 Acteurs Externes

| # | Acteur | Rôle | Statut |
|---|--------|------|--------|
| 9 | **Client / Membre** | Consultation solde, demande de prêt, épargne | ✅ (partiel) |
| 10 | **Groupe Solidaire** | Caution mutuelle pour les prêts de groupe | 🆕 |
| 11 | **Autorité de Tutelle (BCEAO/COBAC)** | Réception des rapports réglementaires | 🆕 |
| 12 | **Partenaire Mobile Money** | Passerelle de paiement (Orange Money, MTN MoMo, Wave) | 🆕 |

---

## 2. LES MODULES & OPÉRATIONS INFORMATISABLES

---

### MODULE 1 : Gestion de la Clientèle (KYC) ✅ (existe partiellement)

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Inscription d'un nouveau client/membre | CRUD | Collecte nom, prénom, CNI, photo, biométrie, adresse |
| 2 | Vérification KYC (Know Your Customer) | Validation | Contrôle des pièces d'identité, liste noire anti-blanchiment |
| 3 | Attribution d'un code client unique | Auto ⚙️ | Génération automatique à l'inscription |
| 4 | Gestion des parts sociales | Métier | Souscription obligatoire pour devenir membre (coopérative) |
| 5 | Modification du profil client | CRUD | Mise à jour adresse, téléphone, situation familiale |
| 6 | Blocage / Déblocage d'un client | Statut | En cas de fraude ou impayés graves |
| 7 | Catégorisation du client | Classification | Particulier, PME, groupe solidaire, salarié |
| 8 | Historique complet du client | Consultation | Vue 360° : comptes, crédits, épargne, incidents |
| 9 | Gestion des groupes solidaires | CRUD 🆕 | Création, ajout/retrait de membres, désignation du chef de groupe |
| 10 | Scoring client | Calcul ⚙️ 🆕 | Score de risque basé sur l'historique de remboursement |

---

### MODULE 2 : Gestion des Comptes ✅ (existe)

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Ouverture de compte | CRUD | Courant, épargne à vue, épargne à terme, DAT |
| 2 | Génération automatique du numéro de compte | Auto ⚙️ | Format RIB conforme BCEAO |
| 3 | Consultation du solde | Lecture | Temps réel, multi-canal (guichet, mobile, web) |
| 4 | Consultation du relevé de compte | Lecture | Historique paginé avec filtres (date, type, montant) |
| 5 | Modification du découvert autorisé | Admin | Plafond ajustable par le chef d'agence |
| 6 | Clôture de compte | Statut | Vérification solde = 0, archivage |
| 7 | Gel / Dégel de compte | Statut 🆕 | Blocage judiciaire ou administratif |
| 8 | Gestion des comptes d'épargne à terme (DAT) | Métier 🆕 | Durée, taux, pénalité de retrait anticipé |
| 9 | Calcul automatique des intérêts sur épargne | Batch ⚙️ 🆕 | Journalier/mensuel selon le produit |
| 10 | Gestion multi-devises | Config 🆕 | FCFA, Euro, USD avec taux de change |

---

### MODULE 3 : Gestion des Crédits / Prêts 🆕

> [!IMPORTANT]
> C'est le **cœur métier** de la microfinance. Ce module n'existe pas encore dans votre backend.

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| **Origination** ||||
| 1 | Demande de crédit | Formulaire | Client remplit : montant, objet, durée souhaitée |
| 2 | Étude de dossier | Workflow | Agent de crédit : visite terrain, analyse capacité de remboursement |
| 3 | Scoring automatique | Calcul ⚙️ | Basé sur historique, revenus, garanties, ancienneté |
| 4 | Comité de crédit (validation) | Workflow | Approbation multi-niveaux (agent → chef agence → DG selon montant) |
| 5 | Notification de décision | Communication | SMS/Email : accepté, refusé, en attente de complément |
| **Décaissement** ||||
| 6 | Génération du contrat de prêt | Document ⚙️ | PDF avec tableau d'amortissement, conditions, signatures |
| 7 | Génération du tableau d'amortissement | Calcul ⚙️ | Méthodes : taux dégressif, taux constant, in fine |
| 8 | Décaissement effectif | Transaction | Virement sur le compte du client ou remise en espèces |
| **Suivi & Remboursement** ||||
| 9 | Encaissement d'une échéance | Transaction | Au guichet, par virement, par Mobile Money |
| 10 | Remboursement anticipé (partiel ou total) | Transaction | Recalcul du tableau d'amortissement |
| 11 | Report d'échéance | Métier | Accord de grâce en cas de difficulté temporaire |
| 12 | Suivi du portefeuille de crédits | Dashboard | PAR (Portfolio At Risk), taux de remboursement |
| **Impayés & Recouvrement** ||||
| 13 | Détection automatique des impayés | Batch ⚙️ | Comparaison date échéance vs paiements reçus |
| 14 | Calcul des pénalités de retard | Calcul ⚙️ | Taux de pénalité × montant impayé × nombre de jours |
| 15 | Reclassification du crédit | Auto ⚙️ | Sain → Douteux → Contentieux selon ancienneté impayé |
| 16 | Provisionnement automatique | Comptable ⚙️ | Selon normes BCEAO (0%, 25%, 50%, 100%) |
| 17 | Mise en demeure | Document ⚙️ | Génération courrier/SMS de relance (1ère, 2ème, 3ème) |
| 18 | Passage en perte (write-off) | Comptable | Radiation du crédit après épuisement des recours |
| **Garanties** ||||
| 19 | Enregistrement des garanties | CRUD | Type : caution solidaire, nantissement, hypothèque, gage |
| 20 | Évaluation des garanties | Métier | Valeur estimée, photos, documents justificatifs |
| 21 | Mainlevée de garantie | Statut | Après remboursement total du prêt |

---

### MODULE 4 : Opérations de Caisse / Guichet ✅ (existe)

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Dépôt d'espèces | Transaction | Sur compte courant ou épargne |
| 2 | Retrait d'espèces | Transaction | Avec vérification solde + découvert autorisé |
| 3 | Virement interne | Transaction | Entre deux comptes de la même IMF |
| 4 | Virement externe | Transaction 🆕 | Vers une autre institution (si interconnexion) |
| 5 | Encaissement d'échéance de prêt | Transaction 🆕 | Avec ventilation capital / intérêts / pénalités |
| 6 | Paiement des parts sociales | Transaction 🆕 | À l'adhésion du membre |
| 7 | Ouverture / Fermeture de caisse | Procédure | Comptage espèces, rapprochement début/fin de journée |
| 8 | Approvisionnement / Déchargement de caisse | Procédure 🆕 | Transfert coffre ↔ caisse avec double validation |
| 9 | Impression du reçu de transaction | Document ⚙️ | PDF ou impression thermique |
| 10 | Journal de caisse | Rapport ⚙️ | Liste de toutes les opérations du jour par caissier |

---

### MODULE 5 : Comptabilité & Finance 🆕

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Plan comptable paramétrable | Config | Conforme au référentiel BCEAO/OHADA |
| 2 | Génération automatique des écritures | Auto ⚙️ | Chaque transaction génère ses écritures comptables |
| 3 | Grand livre | Rapport | Par compte comptable, avec filtres de période |
| 4 | Balance générale | Rapport | Balance des comptes (débit/crédit) |
| 5 | Bilan | Rapport ⚙️ | Actif / Passif à une date donnée |
| 6 | Compte de résultat | Rapport ⚙️ | Charges / Produits sur une période |
| 7 | Tableau de flux de trésorerie | Rapport 🆕 | Entrées / Sorties de cash |
| 8 | Rapprochement bancaire | Procédure | Comparaison compte banque interne vs relevé bancaire |
| 9 | Clôture journalière | Batch ⚙️ | Verrouillage des écritures du jour |
| 10 | Clôture mensuelle / annuelle | Batch ⚙️ | Report à nouveau, calcul résultat |
| 11 | Gestion de la TVA et impôts | Calcul ⚙️ | Déclarations fiscales automatisées |
| 12 | Gestion des immobilisations | CRUD | Amortissements automatiques du matériel |

---

### MODULE 6 : Tarification & Frais ✅ (existe partiellement)

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Paramétrage des grilles tarifaires | Config | Par produit : frais de dossier, commissions, pénalités |
| 2 | Frais de tenue de compte | Batch ⚙️ | Prélèvement mensuel automatique |
| 3 | Frais de dossier de crédit | Calcul | % du montant emprunté, prélevé au décaissement |
| 4 | Commission sur virement | Calcul ⚙️ | Fixe ou % du montant |
| 5 | Pénalités de retard sur prêt | Calcul ⚙️ | Taux × montant impayé × durée |
| 6 | Pénalité de retrait anticipé (DAT) | Calcul 🆕 | Perte d'intérêts si retrait avant terme |
| 7 | Frais de fermeture de compte | Calcul | Montant fixe paramétrable |
| 8 | Assurance emprunteur | Calcul 🆕 | Prime intégrée aux échéances du prêt |

---

### MODULE 7 : Reporting & Conformité Réglementaire 🆕

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| **Rapports internes** ||||
| 1 | Tableau de bord direction | Dashboard | KPIs en temps réel (encours, PAR, rentabilité) |
| 2 | Rapport de portefeuille de crédits | Rapport ⚙️ | Ventilation par statut, agent, agence, produit |
| 3 | Rapport de collecte d'épargne | Rapport ⚙️ | Évolution des dépôts par produit et agence |
| 4 | Rapport de productivité par agent | Rapport ⚙️ | Nombre de clients, encours géré, taux de recouvrement |
| 5 | Rapport de trésorerie | Rapport ⚙️ | Position de liquidité quotidienne |
| **Rapports réglementaires (BCEAO)** ||||
| 6 | États périodiques BCEAO | Rapport ⚙️ | Formats imposés (mensuels, trimestriels, annuels) |
| 7 | Déclaration des grands risques | Rapport ⚙️ | Crédits > seuil réglementaire |
| 8 | Ratio de solvabilité | Calcul ⚙️ | Fonds propres / Encours pondérés |
| 9 | Ratio de liquidité | Calcul ⚙️ | Actifs liquides / Passifs exigibles |
| 10 | Déclaration de soupçon (LCB-FT) | Alerte 🆕 | Anti-blanchiment : opérations suspectes |
| **Audit** ||||
| 11 | Piste d'audit complète | Log ⚙️ | Qui a fait quoi, quand, depuis quelle IP |
| 12 | Contrôle Maker-Checker | Workflow | Double validation pour opérations sensibles |

---

### MODULE 8 : Communication & Notifications ✅ (existe partiellement)

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Alerte SMS après transaction | Auto ⚙️ | Dépôt, retrait, virement reçu |
| 2 | Rappel d'échéance de prêt | Auto ⚙️ 🆕 | SMS J-3, J-1 avant la date d'échéance |
| 3 | Notification d'impayé | Auto ⚙️ 🆕 | SMS/Email à J+1, J+7, J+30 |
| 4 | Alerte connexion suspecte | Sécurité ⚙️ | Connexion depuis un nouvel appareil |
| 5 | Notification de décision de crédit | Workflow 🆕 | Acceptation ou refus du dossier |
| 6 | Communication de masse | Marketing 🆕 | Campagnes SMS pour nouveaux produits |
| 7 | Messagerie interne | Outil 🆕 | Communication entre agents et agences |

---

### MODULE 9 : Gestion des Ressources Humaines 🆕

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Gestion des fiches employés | CRUD | Informations personnelles, contrat, poste, agence |
| 2 | Gestion des rôles et permissions | Admin | RBAC : Caissier, Agent crédit, Chef agence, DG |
| 3 | Affectation aux agences | Admin | Mutation, rattachement hiérarchique |
| 4 | Calcul de la paie | Batch ⚙️ | Salaire brut, cotisations, net à payer |
| 5 | Gestion des congés | Workflow | Demande → Validation → Décompte du solde |
| 6 | Évaluation de performance | Rapport | Objectifs vs réalisations (taux recouvrement, nb clients) |
| 7 | Planning des agents terrain | Planification | Tournées de recouvrement, visites clients |

---

### MODULE 10 : Paramétrage & Administration Système 🆕

| # | Opération | Type | Détail |
|---|-----------|------|--------|
| 1 | Gestion des agences/points de service | Config | Création, adresse, horaires, caissiers rattachés |
| 2 | Paramétrage des produits d'épargne | Config | Nom, taux d'intérêt, plafonds, conditions |
| 3 | Paramétrage des produits de crédit | Config | Nom, taux, durée min/max, garanties exigées, méthode calcul |
| 4 | Gestion des exercices comptables | Config | Ouverture/clôture d'exercice fiscal |
| 5 | Paramétrage des devises et taux de change | Config | Multi-devises UEMOA |
| 6 | Gestion des jours fériés | Config | Impact sur les calculs d'intérêts et échéances |
| 7 | Sauvegarde automatique de la base | Batch ⚙️ | Backup quotidien chiffré |
| 8 | Journal des connexions | Sécurité ⚙️ | Logs de toutes les sessions utilisateurs |
| 9 | Gestion des API partenaires | Config | Clés API Mobile Money, SMS gateway, etc. |

---

## 3. MATRICE ACTEURS × MODULES

| Module | Admin | DG | Chef Agence | Agent Crédit | Caissier | Comptable | Auditeur | Client |
|--------|-------|----|-------------|-------------|----------|-----------|----------|--------|
| Clientèle (KYC) | ✅ | 👁️ | ✅ | ✅ | 👁️ | ❌ | 👁️ | 👁️ |
| Comptes | ✅ | 👁️ | ✅ | 👁️ | ✅ | 👁️ | 👁️ | 👁️ |
| **Crédits** | ✅ | ✅ | ✅ | ✅ | 👁️ | 👁️ | 👁️ | 👁️ |
| Caisse/Guichet | ❌ | 👁️ | ✅ | ❌ | ✅ | 👁️ | 👁️ | ❌ |
| Comptabilité | ✅ | 👁️ | 👁️ | ❌ | ❌ | ✅ | 👁️ | ❌ |
| Tarification | ✅ | ✅ | 👁️ | ❌ | ❌ | 👁️ | 👁️ | ❌ |
| Reporting | ✅ | ✅ | ✅ | 👁️ | ❌ | ✅ | ✅ | ❌ |
| Notifications | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | 👁️ |
| RH | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ | 👁️ | ❌ |
| Admin Système | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | 👁️ | ❌ |

> ✅ = Accès complet | 👁️ = Lecture seule | ❌ = Pas d'accès

---

## 4. PROCESSUS AUTOMATISABLES (BATCH / CRON)

Ces opérations tournent **sans intervention humaine** à intervalles réguliers :

| Fréquence | Opération | Module |
|-----------|-----------|--------|
| **Quotidien** | Calcul des intérêts sur épargne | Comptes |
| **Quotidien** | Détection des échéances impayées | Crédits |
| **Quotidien** | Envoi des rappels d'échéance (J-3, J-1) | Notifications |
| **Quotidien** | Clôture comptable journalière | Comptabilité |
| **Quotidien** | Sauvegarde de la base de données | Admin |
| **Hebdomadaire** | Rapport de trésorerie | Reporting |
| **Mensuel** | Prélèvement frais de tenue de compte | Tarification |
| **Mensuel** | Calcul de la paie | RH |
| **Mensuel** | Reclassification des crédits impayés | Crédits |
| **Mensuel** | Calcul du provisionnement | Comptabilité |
| **Mensuel** | Génération des rapports BCEAO | Reporting |
| **Annuel** | Clôture d'exercice comptable | Comptabilité |

---

## 5. ÉTAT ACTUEL DE VOTRE BACKEND vs CIBLE MICROFINANCE

| Module | Existant dans votre backend | À développer |
|--------|-----------------------------|--------------|
| Clientèle (KYC) | ✅ `ClientService` basique | Scoring, groupes solidaires, parts sociales |
| Comptes | ✅ `CompteService` + `CarteVisaService` | Épargne à terme (DAT), calcul intérêts |
| **Crédits** | ❌ **Absent** | **Module complet à créer** (priorité #1) |
| Caisse | ✅ `TransactionService` | Ouverture/fermeture caisse, approvisionnement |
| Comptabilité | ❌ Absent | Plan comptable OHADA, grand livre, bilan |
| Tarification | ✅ `AgioService` partiel | Grilles tarifaires paramétrables |
| Reporting | ❌ Absent | Dashboards, rapports BCEAO |
| Notifications | ✅ `NotificationService` basique | Rappels échéances, relances impayés |
| RH | ❌ Absent | Fiches employés, paie, évaluations |
| Admin | ✅ Sécurité JWT partielle | Paramétrage produits, agences, devises |

> [!WARNING]
> Le **Module Crédits** est la priorité absolue. C'est ce qui différencie une microfinance d'une banque classique. Sans lui, le système n'est qu'un gestionnaire de comptes courants.
