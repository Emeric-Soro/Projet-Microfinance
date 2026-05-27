# Interfaces front (Core Banking)

Ce document liste les ecrans a prevoir dans le front, regroupes par categorie, avec leur usage et les profils autorises.
Sources: `instruction-Core-banking.md` + controleurs REST du projet.

## Legende des profils
- ADMIN
- DIRECTEUR
- CHEF_AGENCE
- SUPERVISEUR
- GUICHETIER
- CLIENT
- PUBLIC (non authentifie)

## Authentification et acces
- Connexion (login)
  - But: authentifier un utilisateur et obtenir un JWT.
  - Profils: PUBLIC.
- Verification OTP
  - But: valider le second facteur et finaliser la session.
  - Profils: PUBLIC.
- Creation compte web
  - But: creer un acces web/mobile pour un client existant.
  - Profils: PUBLIC.
- Deconnexion
  - But: revoquer le JWT en cours.
  - Profils: tous les utilisateurs authentifies.
- Gestion des roles
  - But: attribuer un role metier a un utilisateur.
  - Profils: ADMIN.
- Activation / desactivation utilisateur
  - But: activer ou couper un acces numerique.
  - Profils: ADMIN.

## Clients et KYC
- Creation client
  - But: enregistrer un nouveau client.
  - Profils: PUBLIC (config actuel) ; a confirmer si usage back-office.
- Liste des clients
  - But: consulter la liste paginee des clients.
  - Profils: ADMIN, GUICHETIER.
- Detail client
  - But: afficher la fiche client.
  - Profils: ADMIN, GUICHETIER.
- Mise a jour statut client
  - But: activer, bloquer, cloturer un client selon regles metier.
  - Profils: ADMIN, GUICHETIER.
- Dossier KYC (saisie / mise a jour)
  - But: saisir pieces d identite, justificatifs, conformite.
  - Profils: ADMIN, GUICHETIER.
- Decision KYC
  - But: valider / rejeter / demander revision du KYC avec niveau de risque.
  - Profils: ADMIN, SUPERVISEUR.

## Comptes
- Ouverture de compte
  - But: ouvrir un compte avec depot initial si KYC valide.
  - Profils: ADMIN, GUICHETIER.
- Consulter solde
  - But: lire le solde courant.
  - Profils: ADMIN, GUICHETIER, CLIENT (si compte lui appartient).
- Changement decouvert
  - But: ajuster le plafond de decouvert.
  - Profils: ADMIN, GUICHETIER.
- Cloture de compte
  - But: cloturer un compte selon conditions metier.
  - Profils: ADMIN, GUICHETIER.
- Blocage / deblocage de compte
  - But: changer le statut de disponibilite du compte.
  - Profils: ADMIN, GUICHETIER.

## Cartes Visa
- Commande de carte
  - But: generer une nouvelle carte rattachee a un compte.
  - Profils: ADMIN, GUICHETIER.
- Opposition carte
  - But: desactiver une carte (perte, fraude).
  - Profils: ADMIN, GUICHETIER.

## Operations et caisse
- Depot
  - But: initier un depot (4-eyes si seuil depasse).
  - Profils: ADMIN, GUICHETIER.
- Retrait
  - But: initier un retrait (4-eyes si seuil depasse).
  - Profils: ADMIN, GUICHETIER.
- Virement
  - But: initier un virement entre comptes (workflow 4-eyes).
  - Profils: ADMIN, GUICHETIER.
- Paiement carte
  - But: debiter un compte via carte (plafond, statut, expiration).
  - Profils: ADMIN, GUICHETIER.
- Validation transaction (approbation)
  - But: approuver une transaction en attente.
  - Profils: ADMIN, SUPERVISEUR.
- Validation transaction (rejet)
  - But: rejeter une transaction en attente.
  - Profils: ADMIN, SUPERVISEUR.
- Historique d un compte
  - But: consulter le releve / lignes d ecriture.
  - Profils: ADMIN, GUICHETIER, CLIENT (si compte lui appartient).
- Ouverture caisse
  - But: ouvrir la caisse du guichetier.
  - Profils: ADMIN, GUICHETIER.
- Fermeture caisse
  - But: fermer la caisse et constater les ecarts.
  - Profils: ADMIN, GUICHETIER.
- Etat caisse
  - But: consulter le solde caisse en cours.
  - Profils: ADMIN, GUICHETIER.

## Credits
- Soumission demande de credit
  - But: saisir une demande de credit client.
  - Profils: ADMIN, CHEF_AGENCE, GUICHETIER.
- Liste des demandes en attente
  - But: afficher les demandes a traiter.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE.
- Detail demande de credit
  - But: consulter une demande specifique.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Decision demande de credit
  - But: approuver / rejeter une demande.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE.
- Consultation credit
  - But: afficher la fiche credit.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Decaissement credit
  - But: verser le montant sur le compte cible.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE.
- Remboursement credit
  - But: enregistrer un paiement de credit.
  - Profils: ADMIN, GUICHETIER.
- Echeancier / tableau amortissement
  - But: visualiser les echeances et le plan de remboursement.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Credits par client
  - But: lister les credits d un client.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Simulation de credit
  - But: simuler un tableau amortissement.
  - Profils: PUBLIC.

## Tarification et agios
- Calcul frais tenue mensuels
  - But: lancer le batch de frais de tenue de compte.
  - Profils: ADMIN.
- Calcul penalite decouvert
  - But: calculer la penalite sur un compte en decouvert.
  - Profils: ADMIN.
- Prelevement agios en attente
  - But: executer le prelevement des agios non debites.
  - Profils: ADMIN.
- Rafraichir cache tarification
  - But: invalider le cache des parametres de tarification.
  - Profils: ADMIN.

## Parametrage
- Produits credit (creation)
  - But: creer un produit de credit.
  - Profils: ADMIN, DIRECTEUR.
- Produits credit (liste)
  - But: lister les produits actifs.
  - Profils: authentifies (tous roles).
- Produits epargne (creation)
  - But: creer un produit d epargne.
  - Profils: ADMIN, DIRECTEUR.
- Produits epargne (liste)
  - But: lister les produits actifs.
  - Profils: authentifies (tous roles).
- Agences (creation)
  - But: creer une agence.
  - Profils: ADMIN.
- Agences (modification)
  - But: mettre a jour une agence.
  - Profils: ADMIN.
- Agences (detail)
  - But: consulter une agence.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Agences (liste)
  - But: lister les agences actives.
  - Profils: ADMIN, SUPERVISEUR, CHEF_AGENCE, GUICHETIER.
- Agences (desactivation)
  - But: desactiver une agence.
  - Profils: ADMIN.

## Notifications
- Historique notifications client
  - But: lister SMS/Emails envoyes a un client.
  - Profils: CLIENT.

## Statistiques / tableau de bord
- KPIs direction
  - But: afficher indicateurs agreges (clients actifs, depots, credits, PAR).
  - Profils: ADMIN, DIRECTEUR, CHEF_AGENCE.

## Audit et conformite
- Journal d audit
  - But: consulter les actions sensibles tracees par le systeme.
  - Profils: ADMIN, SUPERVISEUR.

## Notes de coherence metier (doc fonctionnelle)
- 4-eyes: transactions sensibles necessitent approbation superviseur.
- KYC valide requis avant ouverture de compte.
- Client actif + compte actif requis avant credit.
- Caisse ouverte requise pour operations en especes.

## Priorite ecrans backoffice
1) Connexion backoffice (login + OTP)
2) Creation compte web client (si fait par backoffice)
3) Liste clients
4) Detail client
5) Saisie / mise a jour KYC client
6) Decision KYC
7) Changement statut client
8) Ouverture de compte
9) Consultation solde compte
10) Blocage compte
11) Deblocage compte
12) Cloture compte
13) Changement decouvert autorise
14) Commande carte Visa
15) Opposition carte Visa
16) Ouverture caisse
17) Etat caisse
18) Depot
19) Retrait
20) Virement
21) Paiement carte
22) Validation transaction (approbation 4-eyes)
23) Validation transaction (rejet 4-eyes)
24) Historique operations d’un compte
25) Soumission demande de credit
26) Liste demandes de credit en attente
27) Detail demande de credit
28) Decision demande de credit
29) Consultation credit
30) Decaissement credit
31) Remboursement credit
32) Echeancier / tableau amortissement
33) Credits par client
34) Simulation credit
35) Creation produit de credit
36) Liste produits de credit
37) Creation produit d’epargne
38) Liste produits d’epargne
39) Creation agence
40) Modification agence
41) Detail agence
42) Liste agences
43) Desactivation agence
44) Calcul frais tenue mensuels
45) Calcul penalite decouvert
46) Prelevement agios en attente
47) Rafraichir cache tarification
48) KPIs / tableau de bord direction
49) Journal d’audit

## Roadmap sprints (backoffice)
Sprint 1 - Socle securite + cycle client
- Connexion backoffice (login + OTP)
- Creation compte web client (si fait par backoffice)
- Liste clients
- Detail client
- Saisie / mise a jour KYC client
- Decision KYC
- Changement statut client

Sprint 2 - Comptes et cartes
- Ouverture de compte
- Consultation solde compte
- Blocage compte
- Deblocage compte
- Cloture compte
- Changement decouvert autorise
- Commande carte Visa
- Opposition carte Visa

Sprint 3 - Operations et caisse
- Ouverture caisse
- Etat caisse
- Depot
- Retrait
- Virement
- Paiement carte
- Validation transaction (approbation 4-eyes)
- Validation transaction (rejet 4-eyes)
- Historique operations d’un compte

Sprint 4 - Credits
- Soumission demande de credit
- Liste demandes de credit en attente
- Detail demande de credit
- Decision demande de credit
- Consultation credit
- Decaissement credit
- Remboursement credit
- Echeancier / tableau amortissement
- Credits par client
- Simulation credit

Sprint 5 - Parametrage et tarification
- Creation produit de credit
- Liste produits de credit
- Creation produit d’epargne
- Liste produits d’epargne
- Creation agence
- Modification agence
- Detail agence
- Liste agences
- Desactivation agence
- Rafraichir cache tarification

Sprint 6 - Agios + pilotage + audit
- Calcul frais tenue mensuels
- Calcul penalite decouvert
- Prelevement agios en attente
- KPIs / tableau de bord direction
- Journal d’audit

## Roadmap sprints (front office)
Sprint 1 - Acces client + consultation
- Connexion client (login + OTP)
- Deconnexion
- Tableau de bord client
- Liste des comptes client
- Detail compte + solde

Sprint 2 - Suivi et historique
- Historique des operations (releve)
- Notifications client (SMS/Email)

Sprint 3 - Credit (optionnel selon regles metier)
- Demande de credit
- Suivi demande / credits en cours

