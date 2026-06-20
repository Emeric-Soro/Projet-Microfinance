# Acteurs, Menus et Écrans du Backoffice Microfinance

> Référence fonctionnelle pour organiser les droits, la navigation et les écrans visibles par profil utilisateur.
> Ce document complète `UI_UX_Specifications_Backoffice.md`.

## 1. Principes de Navigation par Rôle

Chaque utilisateur doit voir uniquement les menus utiles à son métier. Les actions sensibles doivent être masquées ou désactivées côté interface selon le rôle, puis contrôlées côté backend.

### Groupes de menus principaux

| Menu | Usage principal |
|---|---|
| Clients | Création client, KYC, consultation fiche client, statut client |
| Comptes & Cartes | Ouverture compte, consultation solde, blocage, déblocage, clôture, cartes |
| Caisse & Opérations | Versement, retrait, virement, guichet, paiement carte, validation 4-eyes |
| Crédits | Simulation, demande, décision, décaissement, remboursement, échéancier |
| Paramétrage | Produits, agences, cache tarification |
| Pilotage & Audit | Tableaux de bord, agios, journal d’audit, rapports direction |

## 2. Liste des Acteurs

## 2.1 Utilisateur non authentifié

### Mission
Accéder au backoffice de façon sécurisée.

### Menus visibles
| Menu | Visible |
|---|---|
| Connexion | Oui |
| Vérification OTP | Oui, après login |
| Tous les autres menus | Non |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Connexion | Logo, titre "Connexion Backoffice", champs identifiant/mot de passe, bouton se connecter, lien mot de passe oublié |
| OTP | 6 champs numériques, compteur d’expiration, bouton valider, lien renvoyer le code |

## 2.2 Agent commercial / Chargé clientèle

### Mission
Créer et gérer les clients, préparer les dossiers KYC et ouvrir les comptes de base.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Oui | Liste, création, détail, KYC |
| Comptes & Cartes | Oui | Ouverture compte, consultation compte, commande carte |
| Caisse & Opérations | Partiel | Historique consultable, pas de validation sensible |
| Crédits | Partiel | Simulation et consultation client |
| Paramétrage | Non | Aucun accès |
| Pilotage & Audit | Non | Aucun accès |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Liste clients | Recherche, filtres, badges KYC/statut, bouton nouveau client, actions voir/modifier |
| Création client | Stepper Identité, Documents KYC, Validation, sauvegarde brouillon |
| Fiche client | Informations personnelles, KYC, comptes, crédits, activité |
| Saisie KYC | Formulaire KYC, uploads, commentaire interne, soumission |
| Ouverture compte | Sélecteur client, contrôle KYC validé, type de compte, dépôt initial |
| Consultation compte | Solde, statut, titulaire, historique court |
| Commande carte | Type de carte, compte rattaché, frais, mode de remise |
| Simulation crédit | Montant, durée, taux, tableau d’amortissement prévisionnel |

### Actions interdites
- Valider ou rejeter un KYC.
- Bloquer, débloquer ou clôturer un compte.
- Modifier les produits, tarifs ou agences.
- Accéder au journal d’audit complet.

## 2.3 Agent guichet

### Mission
Traiter les opérations simples au comptoir et consulter les comptes nécessaires au service client.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Partiel | Recherche et consultation client |
| Comptes & Cartes | Partiel | Consultation solde, historique court |
| Caisse & Opérations | Oui | Guichet, versement, retrait, virement, paiement carte |
| Crédits | Partiel | Remboursement crédit si autorisé |
| Paramétrage | Non | Aucun accès |
| Pilotage & Audit | Non | Aucun accès |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Guichet | Recherche client/compte, raccourcis dépôt/retrait/virement |
| Versement | Compte cible, montant, mode dépôt, reçu |
| Retrait | Compte source, solde disponible, montant, contrôle plafond |
| Virement | Compte source/destination, montant, motif, type interne/externe |
| Paiement carte | Numéro carte, compte débité, montant, référence marchand |
| Historique opérations | Filtres simples, dernières opérations, export selon droits |
| Remboursement crédit | Échéance courante, montant remboursé, mode paiement |

### Actions interdites
- Approuver sa propre transaction.
- Modifier un découvert autorisé.
- Bloquer ou clôturer un compte.
- Consulter les écrans de paramétrage.

## 2.4 Caissier

### Mission
Gérer la caisse physique, les dépôts, retraits, décaissements et remboursements.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Partiel | Consultation client |
| Comptes & Cartes | Partiel | Consultation solde |
| Caisse & Opérations | Oui | Caisse, guichet, versement, retrait, paiement carte |
| Crédits | Partiel | Décaissement, remboursement |
| Paramétrage | Non | Aucun accès |
| Pilotage & Audit | Partiel | Activité de sa caisse seulement |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Tableau caisse | Solde ouverture, entrées, sorties, solde courant, alertes |
| Versement | Formulaire dépôt, validation, génération reçu |
| Retrait | Formulaire retrait, contrôle solde/plafond |
| Décaissement crédit | Crédit approuvé, compte cible, montant, confirmation |
| Remboursement crédit | Montant attendu, paiement partiel/avance, répartition capital/intérêts |
| Historique caisse | Mouvements de sa caisse, date/heure, agent, montant |

### Actions interdites
- Valider les opérations 4-eyes en tant que second validateur si initiateur.
- Modifier les statuts client/compte.
- Voir les rapports direction globaux.

## 2.5 Agent de crédit

### Mission
Préparer, simuler et soumettre les demandes de crédit.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Oui | Consultation client et crédits du client |
| Comptes & Cartes | Partiel | Consultation comptes actifs du client |
| Caisse & Opérations | Non | Sauf historique utile au dossier |
| Crédits | Oui | Simulation, nouvelle demande, demandes, détail |
| Paramétrage | Non | Consultation indirecte des produits seulement |
| Pilotage & Audit | Non | Aucun accès |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Simulation crédit | Montant, taux, durée, méthode, produit, amortissement |
| Nouvelle demande | Stepper client/produit, montant/durée, garanties, simulation |
| Liste demandes | Filtres statut, produit, agent, montant, date |
| Détail demande | Client, produit, garanties, pièces, simulation, historique décisions |
| Crédits par client | Liste crédits actifs, terminés, rejetés |
| Consultation crédit | Résumé, échéancier, remboursements, documents |

### Actions interdites
- Décider seul d’approuver ou rejeter une demande.
- Décaisser un crédit.
- Modifier les produits de crédit.

## 2.6 Comité de crédit

### Mission
Analyser et décider les demandes de crédit.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Partiel | Fiche client en lecture |
| Comptes & Cartes | Partiel | Comptes liés au dossier |
| Caisse & Opérations | Non | Aucun accès opérationnel |
| Crédits | Oui | Demandes, détail, décision |
| Paramétrage | Non | Aucun accès |
| Pilotage & Audit | Partiel | Indicateurs crédit si autorisés |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Liste demandes crédit | Demandes en attente, filtres, urgence |
| Détail demande | Dossier client, garanties, pièces, simulation |
| Décision crédit | Approuver, rejeter, modifier montant/durée/taux, commentaire comité |
| Consultation crédit | Statut, capital restant, échéancier, documents |

### Actions interdites
- Modifier les données KYC.
- Manipuler la caisse.
- Modifier les paramètres produits/agences.

## 2.7 Superviseur d’agence

### Mission
Contrôler les opérations de l’agence, valider les actions sensibles et suivre la performance locale.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Oui | Consultation, statut client, décision KYC selon habilitation |
| Comptes & Cartes | Oui | Consultation, blocage, déblocage, clôture, découvert |
| Caisse & Opérations | Oui | Validation 4-eyes, caisse, historique |
| Crédits | Oui | Consultation, décision selon délégation |
| Paramétrage | Partiel | Produits/agences en lecture |
| Pilotage & Audit | Partiel | Tableaux agence, audit limité |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Dashboard agence | KPI clients, dépôts, crédits, caisse, alertes |
| Liste clients | Tous filtres, export, changement statut |
| Décision KYC | Dossier lecture seule, décision, risque, commentaire |
| Blocage/déblocage compte | Motif, impacts, confirmation |
| Clôture compte | Conditions, solde résiduel, double confirmation |
| Validation 4-eyes | Transactions en attente, approbation/rejet |
| Historique opérations | Filtres avancés, export CSV/PDF |
| Demandes crédit | Détail et décision si seuil autorisé |

### Actions interdites
- Modifier le paramétrage global si non admin.
- Approuver une opération qu’il a lui-même initiée.
- Accéder aux données hors périmètre agence si restriction multi-agence.

## 2.8 Compliance Officer / Conformité

### Mission
Valider les KYC, surveiller les risques, contrôler les décisions sensibles et consulter les traces.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Oui | KYC, décisions, statuts, documents |
| Comptes & Cartes | Partiel | Comptes à risque ou bloqués |
| Caisse & Opérations | Partiel | Transactions suspectes |
| Crédits | Partiel | Dossiers à risque |
| Paramétrage | Non | Aucun accès |
| Pilotage & Audit | Oui | Journal audit, alertes conformité |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Décision KYC | Valider, rejeter, demander complément, niveau risque |
| Fiche client | Documents KYC, historique décisions, activité |
| Transactions suspectes | Montants élevés, opérations bloquées, alertes |
| Journal d’audit | Filtres période, utilisateur, action, entité, résultat |
| Comptes bloqués | Motif, date, agent, justificatifs |

### Actions interdites
- Faire des opérations de caisse.
- Modifier produits, agences ou tarifs.
- Décaisser un crédit.

## 2.9 Administrateur système / Admin backoffice

### Mission
Paramétrer le système, gérer les référentiels et accéder aux fonctions d’administration.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Oui | Consultation et supervision |
| Comptes & Cartes | Oui | Supervision complète |
| Caisse & Opérations | Oui | Supervision et validation |
| Crédits | Oui | Supervision |
| Paramétrage | Oui | Produits, agences, cache |
| Pilotage & Audit | Oui | Audit, direction, agios |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Produits crédit | Créer, modifier, activer/désactiver |
| Produits épargne | Créer, modifier, activer/désactiver |
| Agences | Liste, création, modification, détail, désactivation |
| Cache tarification | Dernière mise à jour, rafraîchir maintenant |
| Journal d’audit | Consultation complète, export CSV légal |
| Dashboard direction | KPI globaux, rapports, filtres période/agence |
| Agios | Calcul frais, pénalités, prélèvements en attente |

### Actions sensibles
- Désactivation agence avec double confirmation.
- Rafraîchissement cache tarifaire.
- Modification produits impactant les calculs financiers.
- Accès global au journal d’audit.

## 2.10 Direction

### Mission
Piloter l’activité globale, suivre les KPI et exporter les rapports de gestion.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Lecture synthétique | Drill-down depuis KPI |
| Comptes & Cartes | Lecture synthétique | Soldes et encours |
| Caisse & Opérations | Lecture synthétique | Volumes et alertes |
| Crédits | Lecture synthétique | Encours, défaut, remboursement |
| Paramétrage | Non | Aucun accès opérationnel |
| Pilotage & Audit | Oui | Dashboard direction, rapports |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Tableau de bord direction | Clients actifs, total dépôts, crédits en cours, taux remboursement, revenus, solde caisses |
| Graphiques direction | Dépôts vs crédits, nouveaux clients, crédits par produit, défaut par agence, top agents |
| Filtres globaux | Période, agence |
| Export rapport | Rapport direction PDF |
| Drill-down KPI | Navigation vers détails filtrés |

### Actions interdites
- Modifier clients, comptes, crédits ou paramètres.
- Faire des opérations de caisse.
- Changer les statuts.

## 2.11 Auditeur interne / Contrôle interne

### Mission
Contrôler les opérations, vérifier les traces et produire des constats.

### Menus visibles
| Menu | Visible | Détails |
|---|---|---|
| Clients | Lecture | Consultation dossiers |
| Comptes & Cartes | Lecture | Consultation statuts et historiques |
| Caisse & Opérations | Lecture | Historique et validations |
| Crédits | Lecture | Dossiers, décisions, échéanciers |
| Paramétrage | Lecture | Référentiels utilisés |
| Pilotage & Audit | Oui | Journal d’audit, exports |

### Écrans visibles
| Écran | Ce qu’il doit voir |
|---|---|
| Journal d’audit | Filtres avancés, détails JSON diff, export CSV |
| Historique opérations | Filtres période/type/sens/montant |
| Fiche client | Données et historique, sans modification |
| Détail crédit | Décisions, documents, échéancier |
| Paramètres en lecture | Produits, agences, tarifs |

### Actions interdites
- Toute modification métier.
- Toute validation opérationnelle.
- Toute opération financière.

## 3. Matrice Synthétique des Menus

| Acteur | Clients | Comptes & Cartes | Caisse & Opérations | Crédits | Paramétrage | Pilotage & Audit |
|---|---|---|---|---|---|---|
| Non authentifié | Non | Non | Non | Non | Non | Non |
| Agent commercial | Oui | Partiel | Partiel | Partiel | Non | Non |
| Agent guichet | Partiel | Partiel | Oui | Partiel | Non | Non |
| Caissier | Partiel | Partiel | Oui | Partiel | Non | Partiel |
| Agent de crédit | Oui | Partiel | Non | Oui | Non | Non |
| Comité de crédit | Partiel | Partiel | Non | Oui | Non | Partiel |
| Superviseur | Oui | Oui | Oui | Oui | Partiel | Partiel |
| Compliance Officer | Oui | Partiel | Partiel | Partiel | Non | Oui |
| Admin | Oui | Oui | Oui | Oui | Oui | Oui |
| Direction | Lecture | Lecture | Lecture | Lecture | Non | Oui |
| Auditeur interne | Lecture | Lecture | Lecture | Lecture | Lecture | Oui |

## 4. Règles UX par Profil

| Règle | Application |
|---|---|
| Masquage menu | Un menu non autorisé ne doit pas être affiché dans la sidebar |
| Désactivation action | Une action visible mais non disponible doit être désactivée avec tooltip explicatif |
| Périmètre agence | Les agents et superviseurs voient uniquement leur agence, sauf habilitation multi-agence |
| Double validation | Les opérations sensibles doivent exiger un second validateur différent de l’initiateur |
| Audit | Les actions de création, modification, décision, validation et rejet doivent être tracées |
| Exports | Les exports doivent respecter le rôle et le périmètre de données |
| Lecture seule | Direction et audit doivent disposer de vues sans boutons de modification |

## 5. Recommandation de Configuration Technique

Chaque utilisateur devrait avoir :

| Attribut | Exemple |
|---|---|
| `role` | `AGENT_COMMERCIAL`, `CAISSIER`, `SUPERVISEUR`, `ADMIN` |
| `permissions` | `CLIENT_READ`, `KYC_DECIDE`, `ACCOUNT_BLOCK`, `CREDIT_APPROVE` |
| `agencyScope` | `AGENCE_PLATEAU` ou `ALL` |
| `cashdeskScope` | `CAISSE_01` |
| `approvalLimits` | Montant maximum validable par opération |

Le frontend peut ensuite construire la sidebar et les actions visibles à partir de ces permissions, tandis que le backend reste l’autorité finale.
