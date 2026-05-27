||
||
||

> <img src="./4i3ymcw0.png"
> style="width:1.18755in;height:1.37792in" />**DOSSIER** **TECHNIQUE**
> **COMPLET**
>
> **CoreBanking**

Système d’Information Bancaire Application de Microfinance

**Analyse** **Fonctionnelle** **&** **Architecture** **Technique**

> Conformité BCEAO / UEMOA / SYSCOHADA
>
> Spring Boot 3.x \| Oracle Database \| JWT / RBAC
>
> Auteur **Equipe** **Soutra**
>
> Référence DT-SOUTRA-2026-v2.0
>
> Pages 58
>
> **Table** **des** **Matières**
>
> **Table** **des** **matières**

**1** **Système** **d’Information** **Bancaire** **(SIB).** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **7**

> 1.1 Présentation générale . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 7
>
> 1.2 Architecture N-Tiers (5 couches) . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 7
>
> 1.2.1 Couche 1 — Entités (Entities). . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 7
>
> 1.2.2 Couche 2 — Référentiels (Repositories) . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 8
>
> 1.2.3 Couche 3 — Services (Cœur métier). . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 8
>
> 1.2.4 Couche 4 — DTOs & Mappers . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 8
>
> 1.2.5 Couche 5 — Contrôleurs (API REST) . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . 8
>
> 1.3 Sécurité & Authentification. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 9
>
> 1.3.1 Authentification JWT. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 9
>
> 1.3.2 Contrôle d’accès par rôles (RBAC). . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 9
>
> 1.3.3 Principe des 4 Yeux . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 10
>
> 1.4 Traçabilité & Audit. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 10
>
> 1.4.1 Audit Log avec AOP . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 10
>
> 1.4.2 Verrouillage optimiste (@Version) . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 10
>
> 1.5 Configuration & Paramétrage . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 10
>
> 1.5.1 Paramètres dynamiques . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 11
>
> 1.5.2 Gestion des erreurs. . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 11
>
> 1.6 Interfaces & Communication. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 11
>
> 1.6.1 API REST. . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 11
>
> 1.7 Décisions d’architecture . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 11

**2** **Gestion** **des** **Clients** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **13**

> 2.1 Présentation générale . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 13
>
> **1**/58
>
> **SOUTRA** \| Dossier Technique TABLE DES MATIÈRES
>
> 2.2 Cycle de vie du client . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 13
>
> 2.2.1 Étape 1 — Création (NOUVEAU) . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . 14
>
> 2.2.2 Étape 2 — Dossier KYC. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 14
>
> 2.2.3 Étape 3 — Décision KYC . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 14
>
> 2.2.4 Étape 4 — Gestion du statut. . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.3 Évaluation du risque client . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.3.1 Facteurs de risque . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.3.2 Niveaux de risque et conséquences . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.4 Gestion des utilisateurs (espace client) . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.4.1 Authentification en deux étapes. . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 15
>
> 2.4.2 Expiration des mots de passe. . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 16
>
> 2.5 Règles métier & contraintes . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 16
>
> 2.6 Traçabilité . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 16

**3** **Gestion** **des** **Comptes** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **17**

> 3.1 Présentation générale . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 17
>
> 3.2 Cycle de vie d’un compte . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 17
>
> 3.2.1 Ouverture de compte . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 17
>
> 3.2.2 Gestion du découvert autorisé . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 18
>
> 3.2.3 Blocage / Déblocage de compte . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 18
>
> 3.2.4 Clôture de compte . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 18
>
> 3.3 Types de comptes. . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 18
>
> 3.4 Gestion des cartes Visa. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 19
>
> 3.4.1 Commande de carte . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 19
>
> 3.4.2 Opposition sur carte. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 19
>
> 3.5 Règles métier . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 19

**4** **Gestion** **des** **Opérations.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**20**

> 4.1 Présentation générale . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 20
>
> 4.2 Types d’opérations . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 20
>
> 4.2.1 Dépôt. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 20
>
> 4.2.2 Retrait . . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 21
>
> Microfinance Core Banking **2**/58
>
> **SOUTRA** \| Dossier Technique TABLE DES MATIÈRES
>
> 4.2.3 Virement. . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 21
>
> 4.2.4 Paiement carte . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 21
>
> 4.3 Circuit de validation — Principe des 4 Yeux . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 21
>
> 4.4 Stratégie de frais (Fee Calculator). . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 21
>
> 4.5 Écritures comptables (Partie double) . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 22
>
> 4.6 Gestion de la caisse. . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 22
>
> 4.6.1 Cycle quotidien . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 22
>
> 4.7 Règles métier . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 23

**5** **Gestion** **Financière.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **24**

> 5.1 Présentation générale . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 24
>
> 5.2 Gestion des crédits . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 24
>
> 5.2.1 Cycle de vie d’un crédit. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 24
>
> 5.2.2 Méthodes d’amortissement . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 25
>
> 5.2.3 Produits de crédit . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 25
>
> 5.2.4 Garanties . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 26
>
> 5.3 Gestion de la tarification. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 26
>
> 5.3.1 Paramètres dynamiques . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 26
>
> 5.4 Gestion des agios . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 26
>
> 5.4.1 Types d’agios . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 26
>
> 5.4.2 Traitement batch. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 27
>
> 5.5 Simulation de crédit . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 27
>
> 5.6 Règles métier . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 27

**6** **Workflow** **Global** **—** **Parcours** **Complet** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **28**

> 6.1 Parcours client complet. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 28
>
> 6.2 Matrice des habilitations. . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 29
>
> 6.3 Règles d’enchaînement entre modules. . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 29
>
> 6.4 Ordre d’implémentation recommandé . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 29

**7** **Outils** **Décisionnels** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **30**

> Microfinance Core Banking **3**/58
>
> **SOUTRA** \| Dossier Technique TABLE DES MATIÈRES
>
> 7.1 Vue d’ensemble. . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 30
>
> 7.2 Niveau 1 — Outils stratégiques . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 30
>
> 7.3 Niveau 2 — Outils tactiques. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 30
>
> 7.4 Niveau 3 — Outils opérationnels . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 31
>
> 7.5 Niveau 4 — Outils techniques. . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 31
>
> 7.6 Normes et référentiels applicables . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 31
>
> 7.7 Cycle de décision par phase. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 32

**8** **Arbres** **de** **Décision** **Micro.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **33**

> 8.1 AD-01 — Ouverture de Compte . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 33
>
> 8.2 AD-02 — Validation KYC Client . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 33
>
> 8.3 AD-03 — Traitement Transaction. . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 33
>
> 8.4 AD-04 — Décision Crédit. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 34
>
> 8.5 AD-05 — Gestion Caisse . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 34
>
> 8.6 AD-06 — Gestion des Échéances . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 34
>
> 8.7 AD-07 — Authentification et Sécurité . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 35
>
> 8.8 AD-08 — Calcul d’Agios et Intérêts . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 35
>
> 8.9 Synthèse — Matrice des arbres de décision. . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 35

**9** **Description** **des** **Besoins** **Métier** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **36**

> 9.1 Acteurs du système. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 36
>
> 9.2 Besoins fonctionnels par module. . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 36
>
> 9.3 Besoins non-fonctionnels. . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 37
>
> 9.4 Contraintes réglementaires . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 37
>
> 9.5 Règles de gestion transverses (20 règles). . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 37
>
> 9.6 Priorisation MoSCoW. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 38

**10** **Modélisation** **des** **Données** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **39**

> 10.1 Dictionnaire des entités. . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 39
>
> 10.2 Schéma relationnel — Tables principales. . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 39
>
> Microfinance Core Banking **4**/58
>
> **SOUTRA** \| Dossier Technique TABLE DES MATIÈRES
>
> 10.2.1 TABLE : CLIENTS. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 39
>
> 10.2.2 TABLE : COMPTES . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 40
>
> 10.2.3 TABLE : TRANSACTIONS. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 40
>
> 10.3 Clés étrangères (extraits) . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 40
>
> 10.4 Index et optimisation . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 40
>
> 10.5 Séquences et génération d’identifiants. . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 41
>
> 10.6 Politique de chiffrement . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 41
>
> 10.7 Migration Flyway . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 41

**11** **Organisation** **des** **Modules** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **42**

> 11.1 Vue N-Tiers globale . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 42
>
> 11.2 Les 12 modules métier . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 42
>
> 11.3 Détail par module — Composants . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 43
>
> 11.3.1 Module Client (core-client). . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 43
>
> 11.3.2 Module Transaction (core-operation) . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . 43
>
> 11.3.3 Module Crédit (core-credit) . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 43
>
> 11.4 Matrice des dépendances inter-modules . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . 44
>
> 11.5 Design Patterns utilisés. . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 44

**12** **Règles** **Comptables** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **45**

> 12.1 Principe de la partie double . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 45
>
> 12.2 Schémas d’écritures par type d’opération . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 45
>
> 12.3 Plan comptable simplifié (SYSCOHADA EMF). . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . 46
>
> 12.4 Contrôles d’intégrité comptable . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 46
>
> 12.5 Provisionnement des créances. . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 46

**13** **Sécurité** **et** **Procédures.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**47**

> 13.1 Authentification JWT. . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 47
>
> 13.2 RBAC — Contrôle d’accès basé sur les rôles. . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . 47
>
> 13.3 Politique de verrouillage . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 47
>
> 13.4 Expiration mot de passe . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 48
>
> Microfinance Core Banking **5**/58
>
> **SOUTRA** \| Dossier Technique TABLE DES MATIÈRES
>
> 13.5 OTP 2FA . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 48
>
> 13.6 Circuit 4 yeux (Four-Eyes Principle) . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 48
>
> 13.7 Piste d’audit (AOP) . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 49

**14** **Sommaire** **des** **16** **Dossiers** **d’Analyse** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **50**

> 14.1 Index des dossiers. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 50
>
> 14.2 Matrice de couverture par domaine. . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 50
>
> 14.3 Statistiques documentaires . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 51

**15** **Tests** **et** **Validation** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **52**

> 15.1 Pyramide de tests. . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 52
>
> 15.2 Plan de tests par module . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 52
>
> 15.3 Scénarios critiques . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 53
>
> 15.4 Outils et frameworks. . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 53
>
> 15.5 Critères d’acceptation. . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 53

**16** **Déploiement** **et** **Maintenance.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **54**

> 16.1 Pipeline CI/CD. . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 54
>
> 16.2 Architecture Docker . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 54
>
> 16.3 Profils Spring Boot. . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 54
>
> 16.4 Monitoring et observabilité . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 54
>
> 16.5 Stratégie de sauvegarde . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 55
>
> 16.6 Procédures de maintenance. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . 55
>
> 16.7 Procédure de mise en production . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . 55

**Annexes** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.**
**.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **.** **57**

> Annexe A — Glossaire . . . . . . . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 57
>
> Annexe B — Stack technologique. . . . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . . . 57
>
> Annexe C — Références documentaires . . . . . . . . . . . . . . . . .
> . . . . . . . . . . . . . . . . . . . . . . . . . 58
>
> Microfinance Core Banking **6**/58

**SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)

> **1** **Système** **d’Information** **Bancaire** **(SIB)**
>
> ò **Sous-système** **transverse**
>
> Le SIB constitue le **socle** **technique** sur lequel reposent
> l’ensemble des modules fonctionnels. Il n’implémente aucune règle
> métier spécifique, mais fournit les mécanismes de sécurité,
> persistance, communication et audit que les modules métiers utilisent.
>
> **Dépendances** **:** Tous les modules fonctionnels reposent sur ce
> socle.

**1.1** **Présentation** **générale**

Le Système d’Information Bancaire (SIB) constitue le socle technique de
l’application de microfi-nance. Dans une institution de microfinance
(IMF), le SIB doit concilier des contraintes souvent contradictoires :

> •  **Accessibilité** — L’application doit être utilisable par des
> agents de guichet dans des agences rurales avec une connexion internet
> potentiellement instable, tout en restant disponible pour les clients
> sur mobile.
>
> • Ł **Sécurité** — Les transactions financières manipulent de l’argent
> réel. Chaque opération doit être authentifiée, autorisée, tracée et
> irréfutable.
>
> • Û **Auditabilité** — Les régulateurs (BCEAO, BEAC) imposent des
> exigences strictes de reporting. Chaque action doit pouvoir être
> reconstituée *a* *posteriori*.

**1.2** **Architecture** **N-Tiers** **(5** **couches)**

Le système suit une architecture en **5** **couches** (N-Tiers), un
choix délibéré répondant aux besoins de la microfinance :

**1.2.1** **Couche** **1** **—** **Entités** **(Entities)**

> Ô **Modèle** **de** **domaine**
>
> Chaque entité correspond à un concept bancaire réel (Client, Compte,
> Transaction, Credit...) et est mappée directement à une table Oracle.
> Toutes héritent de BaseAuditEntity qui injecte automatiquement :
>
> • dateCreation / dateModification — Timestamps automatiques
>
> • creePar / modifiePar — Traçabilité utilisateur
>
> Microfinance Core Banking **7**/58

**SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)

> Ce mécanisme garantit que **toute** **entité** dispose d’une
> traçabilité complète sans effort de développement supplémentaire.

**1.2.2** **Couche** **2** **—** **Référentiels** **(Repositories)**

Interfaces Spring Data JPA étendant JpaRepository. Aucune implémentation
manuelle — le framework génère les requêtes à partir des noms de
méthodes.

> u **Règle** **de** **Gestion** **—** **Pagination** **native**
>
> La pagination est native (crucial pour les listes de transactions
> pouvant atteindre des centaines de milliers de lignes). Les
> Optional\<T\> en retour forcent le traitement des cas d’absence.

**1.2.3** **Couche** **3** **—** **Services** **(Cœur** **métier)**

> Ł **Règle** **fondamentale**
>
> Les services sont le **cœur** **du** **métier**. Aucune couche
> au-dessus (Contrôleur) ne doit contenir de logique métier. Les
> services sont les seuls à porter l’annotation @Transactional. Exemples
> de logique bancaire :
>
> • Vérifier que le KYC du client est valide avant d’ouvrir un compte
>
> • Vérifier que le solde est sufisant avant d’autoriser un retrait
>
> • Calculer les agios selon la méthode d’amortissement
>
> • Appliquer les frais de tenue de compte mensuels

**1.2.4** **Couche** **4** **—** **DTOs** **&** **Mappers**

Découplage entre le monde externe (API JSON) et le domaine interne
(Entités) :

> • Les entités contiennent des données sensibles (KYC, crédit). Les
> DTOs contrôlent **exactement** ce qui est exposé.
>
> • Les formats d’entrée API diffèrent de la structure base. Les mappers
> MapStruct (compilés) détectent les erreurs à la compilation.

**1.2.5** **Couche** **5** **—** **Contrôleurs** **(API** **REST)**

Point d’entrée HTTP. Règle stricte : **aucune** **logique** **métier**.
Le contrôleur reçoit la requête (DTO), appelle le service, retourne la
réponse (DTO).

> Microfinance Core Banking **8**/58

**SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)

> **Couche** **5** **—** **Contrôleurs** **(API** **REST)**
>
> **Couche** **4** **—** **DTOs** **&** **Mappers**
>
> **Couche** **3** **—** **Services** **(Cœur** **métier)**
>
> **Couche** **2** **—** **Repositories** **(JPA)**
>
> **Couche** **1** **—** **Entités** **(Domaine)**

@RestController — Points d’entrée HTTP

MapStruct — Transformation sécurisée

@Transactional — Logique bancaire

JpaRepository — Accès données Oracle

@Entity + BaseAuditEntity

> **Figure** **1** **–** Architecture N-Tiers en 5 couches du système
> Soutra

**1.3** **Sécurité** **&** **Authentification**

**1.3.1** **Authentification** **JWT**

L’application utilise **JSON** **Web** **Tokens** **(JWT)** pour
l’authentification sans état :

||
||
||
||
||
||
||

**1.3.2** **Contrôle** **d’accès** **par** **rôles** **(RBAC)**

||
||
||
||
||
||
||
||
||

> . **Exigence** **réglementaire** **UEMOA/CEMAC**
>
> Cette séparation stricte des rôles n’est pas un détail technique —
> c’est une **exigence** **régle-mentaire** dans la plupart des
> juridictions de la zone UEMOA/CEMAC.
>
> Microfinance Core Banking **9**/58

**SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)

**1.3.3** **Principe** **des** **4** **Yeux**

> u **Règle** **de** **Gestion** **—** **Circuit** **de** **validation**
> **obligatoire**
>
> **Aucune** **opération** **sensible** ne peut être réalisée par une
> seule personne :
>
> **1.** Un guichetier initie une transaction (dépôt, retrait, virement)
>
> **2.** Si le montant *\>* 500000 FCFA : transaction à **EN**
> **ATTENTE**
>
> **3.** Un superviseur (ou admin) doit explicitement approuver ou
> rejeter
>
> **4.** Le système **interdit** **l’auto-approbation** : un superviseur
> ne peut pas approuver une transaction qu’il a lui-même initiée
>
> *Seuil* *configurable* *via* TransactionWorkflowProperties*.* *La*
> *valeur* *de* *500000* *FCFA* *(*≈ *762* *€)* *correspond* *aux*
> *seuils* *typiques* *des* *IMF* *de* *l’UEMOA.*

**1.4** **Traçabilité** **&** **Audit**

**1.4.1** **Audit** **Log** **avec** **AOP**

Le système utilise la **programmation** **orientée** **aspect**
**(AOP)** pour journaliser automatiquement toutes les opérations :

> • **Qui** a fait quoi (utilisateur connecté)
>
> • Sur **quelle** **entité** (type, identifiant)
>
> • **Quelle** **action** (création, modification, suppression)
>
> • **Quand** (timestamp précis)
>
> • **Ancienne/nouvelle** **valeur** (pour les modifications)

**1.4.2** **Verrouillage** **optimiste** **(@Version)**

> Ô **Concurrence** **sécurisée**
>
> Toutes les entités critiques (Compte, Transaction, Credit) utilisent
> un champ @Version. Si deux guichetiers traitent simultanément le même
> compte, la deuxième transaction échoue avec une exception, évitant la
> corruption de données.
>
> Une approche pessimiste (verrouillage en base) serait trop coûteuse
> pour un système traitant des centaines d’opérations simultanément.

**1.5** **Configuration** **&** **Paramétrage**

> Microfinance Core Banking **10**/58
>
> **SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)
>
> **1.5.1** **Paramètres** **dynamiques**
>
> Le système utilise une approche **clé-valeur** (TarificationParametre)
> pour les paramètres modi-fiables sans redéploiement :

||
||
||
||
||
||
||

Ces paramètres sont mis en cache avec **Caffeine** pour éviter les
allers-retours en base. Invalidation via POST /cache/refresh.

> **1.5.2** **Gestion** **des** **erreurs**
>
> Centralisation via GlobalExceptionHandler :
>
> • Format uniforme ErrorResponseDTO (code, message, timestamp, détails)
>
> • Les erreurs de validation jakarta.validation sont interceptées
>
> • En production, **jamais** **de** **stack** **trace** exposée
> (anti-fuite d’informations)
>
> **1.6** **Interfaces** **&** **Communication**
>
> **1.6.1** **API** **REST**

||
||
||
||
||
||
||
||
||
||
||
||

> **1.7** **Décisions** **d’architecture**
>
> Microfinance Core Banking **11**/58

**SOUTRA** \| Dossier Technique 1 SYSTÈME D’INFORMATION BANCAIRE (SIB)

||
||
||
||
||
||
||

> Microfinance Core Banking **12**/58

**SOUTRA** \| Dossier Technique 2 GESTION DES CLIENTS

> **2** **Gestion** **des** **Clients**
>
> ò **Module** **Client** **&** **Utilisateur** Sous-système : Client &
> Utilisateur.
>
> **Entités** **principales** **:** Client, Utilisateur,
> RoleUtilisateur, StatutClient, StatutKycClient, Ni-veauRisqueClient,
> TypePieceIdentite.
>
> **Dépendances** **:** SIB (Module 1), Notification.

**2.1** **Présentation** **générale**

Le module de Gestion des Clients est le **point** **d’entrée** de toutes
les relations entre l’IMF et ses clients. Il gère l’ensemble du cycle de
vie : enregistrement initial, vérification d’identité (KYC), évaluation
du risque et gestion des accès numériques.

Spécificités de la microfinance :

> • Documents d’identité non standardisés (cartes nationales,
> passeports, certificats) — d’où la diversité des TypePieceIdentite
>
> • KYC progressif : début de relation avec dossier partiel ( **EN**
> **ATTENTE** BROUILLON)
>
> • Notation de risque simplifiée : pas de scoring bancaire
> traditionnel, évaluation sur critères observables (PEP, historique,
> comportement)

**2.2** **Cycle** **de** **vie** **du** **client**

> **BLOQUÉ**
>
> Déblocage **NOUVEAU** KYC validé **A** Nouveau KYC Auto/manuel
> **SUSPENDU**
>
> Régularisation
>
> **INACTIF**
>
> **Figure** **2** **–** Diagramme d’états du cycle de vie client
>
> Microfinance Core Banking **13**/58

**SOUTRA** \| Dossier Technique 2 GESTION DES CLIENTS

**2.2.1** **Étape** **1** **—** **Création** **(NOUVEAU)**

Informations minimales requises :

> • Nom, prénom, date de naissance
>
> • Type et numéro de pièce d’identité
>
> • Adresse et coordonnées (téléphone, email)
>
> • Agence de rattachement
>
> u **Règle** **de** **Gestion** **—** **R1** **—** **Unicité**
> **pièce** **d’identité**
>
> L’unicité du numéro de pièce d’identité est vérifiée pour éviter les
> doublons de clients (fraude à l’identité). Deux clients ne peuvent
> avoir le même numéro pour un même type de pièce.

**2.2.2** **Étape** **2** **—** **Dossier** **KYC**

> Ô **Processus** **réglementaire** **UEMOA/CEMAC**
>
> Le KYC (*Know* *Your* *Customer*) est un processus réglementaire
> **obligatoire** pour toute institution financière de la zone
> UEMOA/CEMAC.
>
> **État** **BROUILLON** **:**
>
> • Dossier en cours de constitution
>
> • Modifiable par le guichetier
>
> • Aucune opération bancaire possible
>
> **État** **EN_ATTENTE** **:**
>
> • Dossier complet, soumis pour validation
>
> • Non modifiable par le guichetier
>
> • Un superviseur doit prendre la décision
>
> **Contenu** **du** **dossier** **KYC** **:** Copie pièce d’identité,
> justificatif de domicile, photo, déclaration PEP, évaluation du
> risque, score de risque.

**2.2.3** **Étape** **3** **—** **Décision** **KYC**

> Ł **Séparation** **des** **tâches** **—** **Instruction** **BCEAO**
> **No01/2019** • Le **guichetier** constitue le dossier (ne peut pas
> valider)
>
> • Le **superviseur** prend la décision (ne peut pas être le même)
>
> Décision
>
> Décision

**VALIDÉ** : Client passe ACTIF, peut ouvrir des comptes.

**REJETÉ** : Client reste BLOQUÉ, motif obligatoire, re-soumission
possible.

> Prévient la création de “faux clients” par des employés malveillants
> et garantit la vérification indépendante.
>
> Microfinance Core Banking **14**/58

**SOUTRA** \| Dossier Technique 2 GESTION DES CLIENTS

**2.2.4** **Étape** **4** **—** **Gestion** **du** **statut**

||
||
||
||
||
||
||

**2.3** **Évaluation** **du** **risque** **client**

**2.3.1** **Facteurs** **de** **risque**

> • **Statut** **PEP** : Personnes politiquement exposées — risque de
> blanchiment plus élevé
>
> • **Score** **calculé** : Combinaison profession, zone géographique,
> type d’activité, historique
>
> • **Comportement** **passé** : Retards de remboursement, incidents de
> paiement, découverts non autorisés

**2.3.2** **Niveaux** **de** **risque** **et** **conséquences**

||
||
||
||
||
||

Le niveau de risque influence directement : le plafond de découvert, le
montant maximum de crédit, la fréquence des revues KYC et le taux
d’intérêt applicable.

**2.4** **Gestion** **des** **utilisateurs** **(espace** **client)**

**2.4.1** **Authentification** **en** **deux** **étapes**

> Login (identifiant + MDP)

Vérification BCrypt

Valide Génération OTP (SMS)

Saisie Valide OTP

Émission JWT

> Échec ×5 → Verrouillage
>
> **Figure** **3** **–** Flux d’authentification avec OTP
>
> Microfinance Core Banking **15**/58
>
> **SOUTRA** \| Dossier Technique 2 GESTION DES CLIENTS
>
> **2.4.2** **Expiration** **des** **mots** **de** **passe**
>
> u **Règle** **de** **Gestion** **—** **Conformité** **BCEAO**
>
> Les mots de passe expirent après **90** **jours**. À l’expiration,
> l’utilisateur est invité à changer son mot de passe lors de la
> prochaine connexion. Conforme aux recommandations BCEAO en matière de
> sécurité des SI bancaires.
>
> **2.5** **Règles** **métier** **&** **contraintes**

||
||
||
||
||
||
||
||

> **2.6** **Traçabilité**

Toutes les actions sont tracées via le système d’audit central :

> • Création d’un client → horodatée + utilisateur créateur
>
> • Modification d’un dossier KYC → horodatée
>
> • Décision KYC (valide/rejet) → horodatée + décideur + motif
>
> • Changement de statut client → horodaté + auteur
>
> • Tentative de connexion échouée → horodatée + compteur
>
> • Changement de rôle utilisateur → horodaté + administrateur
>
> Microfinance Core Banking **16**/58

**SOUTRA** \| Dossier Technique 3 GESTION DES COMPTES

> **3** **Gestion** **des** **Comptes**
>
> ò **Module** **Comptes** **bancaires** **&** **Cartes** **Visa**
>
> **Entités** **principales** **:** Compte, TypeCompte, StatutCompte,
> CarteVisa. **Dépendances** **:** Gestion des Clients (Module 2), SIB
> (Module 1).

**3.1** **Présentation** **générale**

Le module gère le cycle de vie complet des comptes bancaires. Un compte
n’est pas un simple conteneur de solde : c’est l’instrument central
permettant l’épargne, les transferts, le décaissement des crédits, la
gestion du découvert et l’association de moyens de paiement.

**3.2** **Cycle** **de** **vie** **d’un** **compte**

**3.2.1** **Ouverture** **de** **compte**

> u **Règle** **de** **Gestion** **—** **Conditions** **préalables**
> **obligatoires** **1.** Le client doit être en statut **ACTIF** (KYC
> validé)
>
> **2.** Un dépôt initial positif est requis (montant minimum
> configurable)
>
> **3.** Le type de compte doit exister dans la base de référence
>
> **4.** Le guichetier doit être habilité à ouvrir des comptes

**Génération** **du** **numéro** **de** **compte.** Numéro généré
automatiquement selon une formule garantis-sant l’**unicité**
**absolue**, essentielle pour l’identification des transactions et les
virements.

**Dépôt** **initial.** Traité de manière **transactionnelle** : si la
création du compte réussit mais que l’enregistrement de la transaction
échoue, l’ensemble est annulé. Pas de compte sans dépôt initial associé.

> Microfinance Core Banking **17**/58

**SOUTRA** \| Dossier Technique 3 GESTION DES COMPTES

**3.2.2** **Gestion** **du** **découvert** **autorisé**

> Ô **Calcul** **du** **solde** **disponible**
>
> Solde disponible = Solde comptable+Découvert autorisé
>
> **Exemple** **:** Solde = 10000 FCFA, Découvert autorisé = 50000 FCFA
> → Retrait maximum = 60000 FCFA.
>
> Modification du découvert :
>
> • À la hausse : nécessite validation superviseur si *\>* seuil
>
> • À la baisse : guichetier autorisé + notification client
>
> • Chaque modification tracée (qui, quand, ancienne/nouvelle valeur)

**3.2.3** **Blocage** **/** **Déblocage** **de** **compte**

||
||
||
||
||
||
||

**3.2.4** **Clôture** **de** **compte**

> Ł **Prérequis** **mandatoires** **de** **clôture**
>
> **1.** **Solde** **nul** **obligatoirement** — Le client doit avoir
> vidé son compte
>
> **2.** **Aucune** **opération** **en** **attente** — Les transactions
> **EN** **ATTENTE** bloquent la clôture
>
> **3.** **Aucun** **crédit** **en** **cours** — Un client avec crédit
> actif ne peut clôturer son compte principal
>
> **Conséquences** **:** Statut → **CLÔTURÉ** , cartes Visa désactivées,
> aucune opération future, historique conservé pour audit.

**3.3** **Types** **de** **comptes**

||
||
||
||
||
||

> Microfinance Core Banking **18**/58

**SOUTRA** \| Dossier Technique 3 GESTION DES COMPTES

**3.4** **Gestion** **des** **cartes** **Visa**

**3.4.1** **Commande** **de** **carte**

||
||
||
||
||
||
||

**3.4.2** **Opposition** **sur** **carte**

Procédure d’urgence : la carte est **immédiatement** **bloquée**, toute
tentative de paiement refusée. L’opposition est **irréversible** — une
nouvelle carte doit être commandée. Notification envoyée au client.

**3.5** **Règles** **métier**

||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **19**/58

**SOUTRA** \| Dossier Technique 4 GESTION DES OPÉRATIONS

> **4** **Gestion** **des** **Opérations**
>
> ò **Cœur** **opérationnel** **—** **Transactions** **&** **Caisse**
>
> **Entités** **principales** **:** Transaction, LigneEcriture,
> TypeTransaction, StatutOperation, Caisse, TypeCanal.
>
> **Dépendances** **:** Comptes (Module 3), Clients (Module 2), SIB
> (Module 1).

**4.1** **Présentation** **générale**

Ce module est le **cœur** **battant** de l’application. C’est ici que
l’argent “bouge” réellement. Enjeux spécifiques :

> • **Volumes** **élevés** **de** **petites** **transactions** :
> milliers d’opérations quotidiennes par agence
>
> • **Circuit** **de** **validation** : montants sensibles transitent
> par un circuit d’approbation multi-niveaux
>
> • **Traces** **comptables** : chaque mouvement génère une écriture
> comptable rapprochable
>
> • **Gestion** **de** **la** **trésorerie** : la caisse du guichetier
> doit être équilibrée chaque jour

**4.2** **Types** **d’opérations**

||
||
||
||
||
||
||

**4.2.1** **Dépôt**

Opération de crédit augmentant le solde du compte. Toujours autorisé
sauf compte bloqué ou clôturé. Frais : 0% (stratégie dédiée).

> Microfinance Core Banking **20**/58
>
> **SOUTRA** \| Dossier Technique 4 GESTION DES OPÉRATIONS
>
> **4.2.2** **Retrait**

Opération de débit. Vérification : solde+découvert ≥ montant+frais.
Plafond guichet : 1000000 FCFA (configurable). Au-delà : transaction
rejetée avant même la validation superviseur.

> **4.2.3** **Virement**
>
> Transfert entre deux comptes. Si montant ≥ 500000 FCFA : approbation
> superviseur requise. Le bénéficiaire reçoit une notification
> SMS/email.
>
> **4.2.4** **Paiement** **carte**
>
> Débit via carte Visa. Vérifications : carte active, non expirée,
> plafond quotidien non dépassé, solde sufisant.
>
> **4.3** **Circuit** **de** **validation** **—** **Principe** **des**
> **4** **Yeux**
>
> **REJETÉ** → **Annulation**
>
> Guichetier initie la transaction

**Montant** ≥ Oui **500K** **FCFA?**

Transaction EN_ATTENTE

Superviseur examine

> Non
>
> **Exécution** **immédiate**
>
> **VALIDÉ**

**APPROUVÉ** → **Exécution**

> **Figure** **4** **–** Circuit de validation des transactions —
> Principe des 4 Yeux
>
> Ł **Sécurité** **anti-fraude**
>
> • Le superviseur **ne** **peut** **pas** approuver sa propre
> transaction (auto-approbation interdite)
>
> • Un motif est obligatoire en cas de rejet
>
> • L’approbation est horodatée et tracée dans le système d’audit
>
> **4.4** **Stratégie** **de** **frais** **(Fee** **Calculator)**
>
> Le module utilise le **design** **pattern** **Strategy** pour le
> calcul des frais :
>
> Microfinance Core Banking **21**/58

**SOUTRA** \| Dossier Technique 4 GESTION DES OPÉRATIONS

> Ô **Principe** **OCP** **—** **Open/Closed**
>
> Chaque type d’opération a sa propre logique de frais. Cette approche
> permet :
>
> • Modifier les frais par type sans impacter les autres
>
> • Ajouter de nouveaux types sans modifier le code existant
>
> • Configurer les taux via les paramètres de tarification (sans
> redéploiement)
>
> Le calcul des frais est fait **avant** **l’exécution**, permettant :
> vérification du solde sufisant, afichage au client avant confirmation,
> enregistrement dans la même transaction.

**4.5** **Écritures** **comptables** **(Partie** **double)**

> u **Règle** **de** **Gestion** **—** **Principe** **fondamental**
>
> Chaque transaction génère au minimum **deux** **écritures** (partie
> double) :

||
||
||
||
||
||

> Structure d’une écriture : Sens (CREDIT/DEBIT), Montant, Compte
> bancaire, Compte comptable, Date de valeur, Transaction de référence.

**4.6** **Gestion** **de** **la** **caisse**

**4.6.1** **Cycle** **quotidien**

> **Ouverture** Solde initial déclaré 1 caisse/guichetier

**Opérations** Dépôts → solde ↑ Retraits → solde ↓

**Fermeture** Solde réel déclaré

> Écart calculé
>
> **Écart** = **0** Validation superviseur
>
> **Figure** **5** **–** Cycle quotidien de gestion de la caisse
>
> . **Gestion** **des** **écarts**
>
> Si l’écart entre solde réel et théorique est non nul :
>
> • L’écart est enregistré avec les détails
>
> • Un superviseur doit valider la fermeture avec écart
>
> • L’écart est imputé au guichetier ou à l’agence selon la politique de
> l’institution
>
> Microfinance Core Banking **22**/58

**SOUTRA** \| Dossier Technique 4 GESTION DES OPÉRATIONS

**4.7** **Règles** **métier**

||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **23**/58
>
> **SOUTRA** \| Dossier Technique 5 GESTION FINANCIÈRE
>
> **5** **Gestion** **Financière**
>
> ò **Crédits,** **Tarification** **&** **Agios**
>
> **Entités** **principales** **:** Credit, DemandeCredit, Echeance,
> Garantie, Agio, ProduitCredit, Pro-duitEpargne, TarificationParametre,
> TypeAgio, MethodeCalculInteret, StatutCredit, StatutDe-mande,
> TypeGarantie.
>
> **Dépendances** **:** Comptes (Module 3), Clients (Module 2),
> Opérations (Module 4), SIB (Module 1).
>
> **5.1** **Présentation** **générale**
>
> Le module de Gestion Financière est le **système** **nerveux** qui
> régit la rentabilité de l’IMF. Il gère trois domaines interdépendants
> :
>
> **1.** **Les** **Crédits** : Activité principale — prêter de l’argent
> et gérer le remboursement
>
> **2.** **La** **Tarification** : Paramètres définissant les frais et
> taux appliqués
>
> **3.** **Les** **Agios** : Frais et pénalités périodiques générant des
> revenus récurrents
>
> **5.2** **Gestion** **des** **crédits**
>
> **5.2.1** **Cycle** **de** **vie** **d’un** **crédit**
>
> **DEMANDE**
>
> **SOUMISE**

**EN** **INSTRUCTION**

**APPROUVÉE** **DÉCAISSÉ** **EN** **COURS** **SOLDÉ**

> Régularisation
>
> **REJETÉE** **EN** **RETARD**
>
> **Figure** **6** **–** Cycle de vie complet d’un crédit

**Étape** **1** **—** **Demande** **de** **crédit.** Le client soumet
une demande avec : montant souhaité, durée (en mois), produit de crédit
choisi, garanties proposées. Référence générée au format DEM-XXXXX.

> u **Règle** **de** **Gestion** **—** **Validation** **de** **la**
> **demande**
>
> • Montant compris dans les bornes du produit (montant_min,
> montant_max)
>
> • Durée comprise dans les bornes du produit (duree_min, duree_max)
>
> • Client en statut **ACTIF**
>
> Microfinance Core Banking **24**/58
>
> **SOUTRA** \| Dossier Technique 5 GESTION FINANCIÈRE
>
> • Pas de crédit en cours non soldé (selon politique de l’institution)
>
> **Étape** **2** **—** **Instruction** **&** **Décision.** Le
> responsable crédit examine la demande : solvabilité, garanties,
> historique, scoring interne.
>
> Ł **Séparation** **des** **tâches**
>
> L’approbation doit être faite par une personne **différente** de celle
> qui a instruit la demande.

**Étape** **3** **—** **Décaissement.** Génération du crédit
(CRD-XXXXX), création du plan d’amortissement, virement sur le compte
client, activation des échéances.

> **Étape** **4** **—** **Remboursement.**

||
||
||
||
||
||

> **Étape** **5** **—** **Solde.** Crédit soldé lorsque toutes les
> échéances sont payées, capital restant dû nul, intérêts et pénalités
> réglés. Garanties levées, client éligible à un nouveau crédit.
>
> **5.2.2** **Méthodes** **d’amortissement**

||
||
||
||
||
||

> . **Précision** **financière**
>
> **Tous** **les** **calculs** sont faits avec BigDecimal pour garantir
> la précision mathématique. Les erreurs d’arrondi avec double/float
> pourraient entraîner des pertes financières.
>
> **5.2.3** **Produits** **de** **crédit**
>
> Paramétrables sans développement : montant min/max, durée min/max,
> TAEG, méthode de calcul, frais de dossier, types de garanties
> acceptés, pénalités de retard, frais de remboursement anticipé.
>
> Microfinance Core Banking **25**/58

**SOUTRA** \| Dossier Technique 5 GESTION FINANCIÈRE

**5.2.4** **Garanties**

||
||
||
||
||
||
||
||

Chaque garantie est évaluée, activée au décaissement et levée au solde
du crédit.

**5.3** **Gestion** **de** **la** **tarification**

**5.3.1** **Paramètres** **dynamiques**

||
||
||
||
||
||
||
||

> Ô **Cache** **Caffeine** **—** **Performance**
>
> Les paramètres sont chargés en cache local **Caffeine** pour éviter un
> appel base à chaque transaction. Invalidation via POST
> /api/tarification/parametres/cache/refresh. Pas de point unique de
> défaillance.

**5.4** **Gestion** **des** **agios**

**5.4.1** **Types** **d’agios**

||
||
||
||
||

> Microfinance Core Banking **26**/58

**SOUTRA** \| Dossier Technique 5 GESTION FINANCIÈRE

**5.4.2** **Traitement** **batch**

> u **Règle** **de** **Gestion** **—** **Exécution** **sécurisée**
> **des** **agios**
>
> **1.** **Calcul** : Parcours des comptes par pages (pagination pour
> éviter la surcharge mémoire)
>
> **2.** **Validation** : Chaque agio enregistré avec son détail
>
> **3.** **Prélèvement** : Transactions de débit via un utilisateur
> système dédié
>
> **Sécurité** : Seul un administrateur peut déclencher le batch. En cas
> de solde insufisant, l’agio est enregistré mais **non** **prélevé** —
> il sera présenté ultérieurement.

**5.5** **Simulation** **de** **crédit**

Fonctionnalité accessible **sans** **authentification** (permitAll) : le
client saisit montant, durée et produit, le système calcule le plan
d’amortissement théorique : mensualités, coût total, TAEG, tableau
d’amortissement complet. Conforme aux exigences de **transparence**
**BCEAO**.

**5.6** **Règles** **métier**

||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **27**/58

**SOUTRA** \| Dossier Technique 6 WORKFLOW GLOBAL — PARCOURS COMPLET

> **6** **Workflow** **Global** **—** **Parcours** **Complet**
>
> ò **Document** **de** **synthèse**
>
> Enchaînement des 5 modules fonctionnels — du premier contact en agence
> jusqu’à la clôture. **Destiné** **à** **:** Équipes frontend, chefs de
> projet, analystes fonctionnels.

**6.1** **Parcours** **client** **complet**

> **Étape** **0** Accueil Client
>
> Module 2

**Étape** **1** Constitution KYC

> Module 2

**Étape** **2** Validation KYC

> Module 2

**Étape** **3** Ouverture Compte

> Module 3

**Étape** **4** Opérations

> Module 4
>
> **Étape** **5** Crédit
>
> Module 5

**Étape** **6** Agios (Batch)

> Module 5

**Étape** **7** Évolution Statut

> Modules 2/3

**Étape** **8** Clôture

> Module 3
>
> **Figure** **7** **–** Parcours complet d’un client dans le système

||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **28**/58
>
> **SOUTRA** \| Dossier Technique 6 WORKFLOW GLOBAL — PARCOURS COMPLET
>
> **6.2** **Matrice** **des** **habilitations**

||
||
||
||
||
||
||
||
||
||
||
||
||

\* Sauf si le superviseur est l’initiateur (auto-approbation interdite).

> **6.3** **Règles** **d’enchaînement** **entre** **modules**
>
> u **Règle** **de** **Gestion** **—** **Dépendances**
> **fonctionnelles**
>
> **1.** **CLIENT** → **COMPTE** : KYC VALIDE requis avant ouverture de
> compte
>
> **2.** **COMPTE** → **TRANSACTION** : Compte ACTIF requis pour
> opérations
>
> **3.** **CLIENT** **+** **COMPTE** → **CRÉDIT** : Client ACTIF + au
> moins 1 compte actif
>
> **4.** **CRÉDIT** → **DÉCAISSEMENT** → **REMBOURSEMENT** : Ordre
> séquentiel strict
>
> **5.** **CAISSE** **OUVERTE** → **OPÉRATIONS** **LIQUIDES** : Caisse
> requise pour espèces
>
> **6.** **AUCUN** **CRÉDIT** **EN** **COURS** → **CLÔTURE** : Crédit
> actif bloque la clôture
>
> **7.** **SOLDE** **NUL** → **CLÔTURE** : Solde non nul bloque la
> clôture
>
> **6.4** **Ordre** **d’implémentation** **recommandé**

||
||
||
||
||
||
||
||

> Microfinance Core Banking **29**/58

**SOUTRA** \| Dossier Technique 7 OUTILS DÉCISIONNELS

> **7** **Outils** **Décisionnels**
>
> ò **Gouvernance** **&** **Conception** **de** **Projet**
>
> Architecture des outils décisionnels utilisés tout au long du cycle de
> conception du Core Banking System.
>
> **Niveaux** **:** Stratégique (N1), Tactique (N2), Opérationnel (N3),
> Technique (N4).

**7.1** **Vue** **d’ensemble**

> **N1** **—** **Stratégique** Comité de Direction (Sponsor)

**N2** **—** **Tactique** Maîtrise d’Ouvrage (MOA)

**N3** **—** **Opérationnel** Équipe Conception (MOE)

**N4** **—** **Technique** Développeurs (DEV/OPS)

> **Figure** **8** **–** Niveaux décisionnels du projet

**7.2** **Niveau** **1** **—** **Outils** **stratégiques**

||
||
||
||
||
||
||

**7.3** **Niveau** **2** **—** **Outils** **tactiques**

||
||
||
||
||
||
||
||

> Microfinance Core Banking **30**/58

**SOUTRA** \| Dossier Technique 7 OUTILS DÉCISIONNELS

**7.4** **Niveau** **3** **—** **Outils** **opérationnels**

||
||
||
||
||
||
||
||
||

**7.5** **Niveau** **4** **—** **Outils** **techniques**

||
||
||
||
||
||
||
||

**7.6** **Normes** **et** **référentiels** **applicables**

> Microfinance Core Banking **31**/58

**SOUTRA** \| Dossier Technique 7 OUTILS DÉCISIONNELS

||
||
||
||
||
||
||
||
||
||

**7.7** **Cycle** **de** **décision** **par** **phase**

||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **32**/58

**SOUTRA** \| Dossier Technique 8 ARBRES DE DÉCISION MICRO

> **8** **Arbres** **de** **Décision** **Micro**
>
> ò **Logique** **décisionnelle** **fine** **du** **code**
> **applicatif**
>
> Chaque nœud correspond à une condition programmatique et chaque
> branche à un chemin d’exécution.
>
> **10** **arbres** **couvrant**
> **:**Comptes,KYC,Transactions,Crédits,Caisse,Échéances,Authentification,
> Agios, Cartes, Déblocage.

**8.1** **AD-01** **—** **Ouverture** **de** **Compte**

> Client existe?
>
> NON

OUI KYC = VALIDE?

> NON

OUI Type compte fourni? OUI

> NON

Dépôt initial ≥ 0? OUI

> NON

Numéro généré?

> Échec

OK **COMPTE** **CRÉÉ** **(ACTIF)**

> Client introuvable KYC non validé Type obligatoire Montant invalide
> Erreur génération
>
> **Figure** **9** **–** AD-01 — Arbre d’ouverture de compte

**8.2** **AD-02** **—** **Validation** **KYC** **Client**

Conditions séquentielles : Client existe → Statut KYC = EN_ATTENTE →
Pièce d’identité fournie → Pièce valide (non expirée) → Numéro pièce
unique → Âge ≥ 18 → Évaluation PEP → Score de risque calculé.

||
||
||
||
||
||

**8.3** **AD-03** **—** **Traitement** **Transaction**

> Ł **Arbre** **le** **plus** **complexe** **—** **12+** **conditions**
>
> Flux principal : Type valide → Montant *\>* 0 → Compte source existe →
> Compte actif → \[Branchement par type\].
>
> **Dépôt** **:** Créditer compte + Frais 0%.
>
> **Retrait** **:** Vérifier plafond (1M FCFA) → Vérifier solde
> disponible → Vérifier seuil 4-yeux.
>
> Microfinance Core Banking **33**/58

**SOUTRA** \| Dossier Technique 8 ARBRES DE DÉCISION MICRO

> **Virement** **:** Vérifier solde → Compte destinataire actif →
> Débiter/Créditer → Notification. **Paiement** **carte** **:** Carte
> active + non expirée + plafond quotidien.
>
> Supervision 4-yeux : si montant ≥ 500K FCFA et superviseur =
> initiateur.

**8.4** **AD-04** **—** **Décision** **Crédit**

Demande → Vérification KYC → Produit existe et actif → Montant dans
bornes → Durée dans bornes → Garanties sufisantes → Score risque →
Approbation selon seuils.

||
||
||
||
||
||

**8.5** **AD-05** **—** **Gestion** **Caisse**

**Ouverture** **:** Utilisateur existe → Pas de caisse déjà ouverte →
Solde initial ≥ 0 → CAISSE OUVERTE.

**Fermeture** **:** Calcul solde théorique = initial + dépôts −
retraits. Écart = réel − théorique.

> • Écart = 0 → Caisse en équilibre
>
> • \|Écart\| ≤ seuil (5000 FCFA) → Toléré, signalé
>
> • \|Écart\| *\>* seuil → Escalade superviseur

**8.6** **AD-06** **—** **Gestion** **des** **Échéances**

> . **Seuils** **temporels** **de** **défaillance**

||
||
||
||
||
||
||

> Microfinance Core Banking **34**/58
>
> **SOUTRA** \| Dossier Technique 8 ARBRES DE DÉCISION MICRO
>
> **8.7** **AD-07** **—** **Authentification** **et** **Sécurité**
>
> Utilisateur existe → Compte ACTIF → Non verrouillé (ou délai 30min
> écoulé) → Mot de passe BCrypt valide → Mot de passe non expiré (*\<*
> 90j) → OTP 2FA si requis → JWT généré.
>
> u **Règle** **de** **Gestion** **—** **Verrouillage**
> **anti-brute-force**
>
> Après 5 échecs : compte verrouillé pendant 30 minutes. Délai
> auto-libéré. 2FA obligatoire pour Admin et Chef d’agence.
>
> **8.8** **AD-08** **—** **Calcul** **d’Agios** **et** **Intérêts**

Trois sous-calculs batch mensuels :

> a\) **Frais** **de** **tenue** : min(solde×taux*,*plafond) sur comptes
> actifs
>
> b\) **Agios** **sur** **découvert** : \|solde\|×taux× jours (autorisé
> ou pénalisé)
>
> c\) **Intérêts** **créditeurs** : solde×taux× jours (comptes épargne)
>
> **8.9** **Synthèse** **—** **Matrice** **des** **arbres** **de**
> **décision**

||
||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **35**/58

**SOUTRA** \| Dossier Technique 9 DESCRIPTION DES BESOINS MÉTIER

> **9** **Description** **des** **Besoins** **Métier**
>
> ò **Catalogue** **des** **exigences**
>
> Référentiel des besoins fonctionnels, non-fonctionnels, règles de
> gestion et contraintes régle-mentaires.
>
> **Méthode** **:** MoSCoW \| **Normes** **:** BCEAO, SYSCOHADA, OHADA,
> PCI DSS, RGPD.

**9.1** **Acteurs** **du** **système**

||
||
||
||
||
||
||
||

**9.2** **Besoins** **fonctionnels** **par** **module**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **36**/58

**SOUTRA** \| Dossier Technique 9 DESCRIPTION DES BESOINS MÉTIER

**9.3** **Besoins** **non-fonctionnels**

||
||
||
||
||
||
||
||
||
||
||
||

**9.4** **Contraintes** **réglementaires**

> u **Règle** **de** **Gestion** **—** **Normes** **applicables**

||
||
||
||
||
||
||
||
||
||
||

**9.5** **Règles** **de** **gestion** **transverses** **(20**
**règles)**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **37**/58

**SOUTRA** \| Dossier Technique 9 DESCRIPTION DES BESOINS MÉTIER

**9.6** **Priorisation** **MoSCoW**

||
||
||
||
||
||
||

> Microfinance Core Banking **38**/58

**SOUTRA** \| Dossier Technique 10 MODÉLISATION DES DONNÉES

> **10** **Modélisation** **des** **Données**
>
> ò **Modèle** **de** **données** **Oracle** **21c+**
>
> MCD, dictionnaire des entités, colonnes, contraintes, index et
> politique de chiffrement. **SGBD** **:** Oracle 21c+ \| **ORM** **:**
> Spring Data JPA \| **Migration** **:** Flyway.

**10.1** **Dictionnaire** **des** **entités**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

**10.2** **Schéma** **relationnel** **—** **Tables** **principales**

**10.2.1** **TABLE** **:** **CLIENTS**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **39**/58

**SOUTRA** \| Dossier Technique 10 MODÉLISATION DES DONNÉES

**10.2.2** **TABLE** **:** **COMPTES**

||
||
||
||
||
||
||
||
||
||
||

**10.2.3** **TABLE** **:** **TRANSACTIONS**

||
||
||
||
||
||
||
||
||
||
||
||
||

E

**10.3** **Clés** **étrangères** **(extraits)**

||
||
||
||
||
||
||
||
||
||

**10.4** **Index** **et** **optimisation**

||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **40**/58

**SOUTRA** \| Dossier Technique 10 MODÉLISATION DES DONNÉES

**10.5** **Séquences** **et** **génération** **d’identifiants**

||
||
||
||
||
||
||
||
||

**10.6** **Politique** **de** **chiffrement**

> Ł **Sécurité** **des** **données** **au** **repos** **et** **en**
> **transit**

||
||
||
||
||
||
||
||
||

**10.7** **Migration** **Flyway**

||
||
||
||
||
||
||
||
||

> u **Règle** **de** **Gestion** **—** **Règles** **Flyway**
>
> • Les migrations sont **immutables** (ne jamais modifier une migration
> existante)
>
> • Toute modification = nouvelle migration V{n+1}
>
> • Les checksums Flyway garantissent l’intégrité des scripts
>
> • Rollback via nouvelle migration compensatoire
>
> Microfinance Core Banking **41**/58

**SOUTRA** \| Dossier Technique 11 ORGANISATION DES MODULES

> **11** **Organisation** **des** **Modules**
>
> ò **Architecture** **N-Tiers** **en** **12** **modules**
>
> Découpage en couches et packages du backend Spring Boot 3.x.
> **Pattern** **:** Controller → DTO → Service → Repository → Entity.

**11.1** **Vue** **N-Tiers** **globale**

> **CONTROLLER** — @RestController, Swagger/OpenAPI
>
> **DTO** — Request/Response, MapStruct Mappers
>
> **SERVICE** — @Service, @Transactional, Strategy

**INFRASTRUCTURE** Security JWT, AOP Audit Caffeine Cache, Flyway
@Scheduled, ExceptionHandler

> **REPOSITORY** — Spring Data JPA, @Query, Specification
>
> **ENTITY** — @Entity, @Version, @CreatedDate
>
> **Oracle** **Database** **21c+**
>
> **Figure** **10** **–** Architecture N-Tiers en 5 couches +
> infrastructure transverse

**11.2** **Les** **12** **modules** **métier**

> Microfinance Core Banking **42**/58

**SOUTRA** \| Dossier Technique 11 ORGANISATION DES MODULES

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

**11.3** **Détail** **par** **module** **—** **Composants**

**11.3.1** **Module** **Client** **(core-client)**

||
||
||
||
||
||
||
||
||
||

**Endpoints** **principaux** **:**

> • POST /api/clients — Créer client
>
> • PUT /api/clients/{id}/kyc/valider — Valider KYC
>
> • PUT /api/clients/{id}/bloquer — Bloquer client
>
> • POST /api/auth/login — Login JWT
>
> • POST /api/auth/otp/verifier — Vérifier OTP

**11.3.2** **Module** **Transaction** **(core-operation)**

||
||
||
||
||
||
||
||
||
||
||

**11.3.3** **Module** **Crédit** **(core-credit)**

> Microfinance Core Banking **43**/58

**SOUTRA** \| Dossier Technique 11 ORGANISATION DES MODULES

||
||
||
||
||
||
||
||
||
||

**11.4** **Matrice** **des** **dépendances** **inter-modules**

||
||
||
||
||
||
||
||
||
||
||

**11.5** **Design** **Patterns** **utilisés**

||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **44**/58

**SOUTRA** \| Dossier Technique 12 RÈGLES COMPTABLES

> **12** **Règles** **Comptables**
>
> ò **Comptabilité** **bancaire** **—** **Partie** **double**
> **SYSCOHADA**
>
> Application des règles comptables SYSCOHADA aux opérations bancaires
> du Core Banking. **Principe** **fondamental** **:** Pour chaque
> opération, DÉBIT = CRÉDIT.

**12.1** **Principe** **de** **la** **partie** **double**

> Ł **Règle** **absolue**
>
> Toute opération financière génère **au** **minimum** **2** **lignes**
> **d’écriture** comptable :
>
> • Une ligne au **DÉBIT** d’un compte
>
> • Une ligne au **CRÉDIT** d’un autre compte • Équilibre garanti :
> PDÉBIT = PCRÉDIT
>
> Vérification par contrainte applicative CA-06.

**12.2** **Schémas** **d’écritures** **par** **type** **d’opération**

||
||
||
||
||
||
||
||
||
||
||
||
||

**Légende** **:** M = Montant, F = Frais, C = Capital, I = Intérêts, A =
Agio, P = Pénalité

> Microfinance Core Banking **45**/58

**SOUTRA** \| Dossier Technique 12 RÈGLES COMPTABLES

**12.3** **Plan** **comptable** **simplifié** **(SYSCOHADA** **EMF)**

||
||
||
||
||
||
||
||
||
||

**12.4** **Contrôles** **d’intégrité** **comptable**

> u **Règle** **de** **Gestion** **—** **Vérifications** **appliquées**
>
> **1.** **Équilibre** **transactionnel** : Vérifié à chaque écriture
> (CA-06)
>
> **2.** **Cohérence** **solde** : Solde comptable = solde client (P
> mouvements)
>
> **3.** **Rapprochement** **caisse** : Solde théorique vs solde réel
> quotidien **4.** **Balance** **mensuelle** : P Débits = P Crédits par
> période
>
> **5.** **Grand-livre** : Historique complet des mouvements par compte
>
> **6.** **Audit** **trail** : Toute écriture tracée (utilisateur, date,
> IP)

**12.5** **Provisionnement** **des** **créances**

||
||
||
||
||
||
||
||

> Microfinance Core Banking **46**/58

**SOUTRA** \| Dossier Technique 13 SÉCURITÉ ET PROCÉDURES

> **13** **Sécurité** **et** **Procédures**
>
> ò **Architecture** **de** **sécurité** **complète**
>
> JWT, RBAC, OTP 2FA, verrouillage anti-brute-force, audit AOP,
> chiffrement, et circuit 4 yeux. **Stack** **:** Spring Security 6.x,
> BCrypt, HMAC-SHA256, TLS 1.3.

**13.1** **Authentification** **JWT**

> Client envoie login + password

BCrypt vérifie mot de passe

OTP 2FA (si requis)

Génération Access + Refresh

Accès autorisé (Bearer Token)

> **Figure** **11** **–** Flux d’authentification JWT + OTP

||
||
||
||
||
||
||

**13.2** **RBAC** **—** **Contrôle** **d’accès** **basé** **sur**
**les** **rôles**

||
||
||
||
||
||
||

**13.3** **Politique** **de** **verrouillage**

> Microfinance Core Banking **47**/58

**SOUTRA** \| Dossier Technique 13 SÉCURITÉ ET PROCÉDURES

> . **Anti-brute-force**
>
> • **Seuil** **:** 5 tentatives échouées consécutives
>
> • **Action** **:** Compte verrouillé 30 minutes (auto-libération)
>
> • **Journalisation** **:** Chaque tentative enregistrée dans
> SystemAuditLog
>
> • **Compteur** **:** Réinitialisé après login réussi

**13.4** **Expiration** **mot** **de** **passe**

> u **Règle** **de** **Gestion** **—** **Politique** **de** **rotation**
> • Expiration : **90** **jours** après dernière modification
>
> • Force changement au login si expiré
>
> • Historique des 5 derniers mots de passe (anti-réutilisation)
>
> • Complexité minimale : 8 caractères, majuscule, minuscule, chiffre,
> spécial

**13.5** **OTP** **2FA**

||
||
||
||
||
||
||
||

**13.6** **Circuit** **4** **yeux** **(Four-Eyes** **Principle)**

> Ł **Séparation** **des** **devoirs**
>
> • **Seuil** **d’activation** **:** Transaction ≥ 500000 FCFA
>
> • **Règle** **absolue** **:** Superviseur = Initiateur (RG-013)
>
> • **Statut** **intermédiaire** **:** EN_ATTENTE_APPROBATION
>
> • **Timeout** **:** 24h sans approbation → expiration automatique
>
> • **Audit** **:** Initiateur + approbateur + timestamps enregistrés
>
> Microfinance Core Banking **48**/58

**SOUTRA** \| Dossier Technique 13 SÉCURITÉ ET PROCÉDURES

**13.7** **Piste** **d’audit** **(AOP)**

||
||
||
||
||
||
||
||
||
||

> Ô **Implémentation** **technique**
>
> L’audit est implémenté via **AOP** **(Aspect-Oriented**
> **Programming)** avec un aspect @Around interceptant toutes les
> méthodes de service annotées @Auditable. Aucun code d’audit n’est
> mélangé à la logique métier.
>
> Microfinance Core Banking **49**/58

**SOUTRA** \| Dossier Technique 14 SOMMAIRE DES 16 DOSSIERS D’ANALYSE

> **14** **Sommaire** **des** **16** **Dossiers** **d’Analyse**
>
> ò **Cartographie** **documentaire**
>
> Vue d’ensemble des 16 dossiers constituant l’analyse fonctionnelle et
> technique complète du Core Banking System Soutra.

**14.1** **Index** **des** **dossiers**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

**14.2** **Matrice** **de** **couverture** **par** **domaine**

||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **50**/58

**SOUTRA** \| Dossier Technique 14 SOMMAIRE DES 16 DOSSIERS D’ANALYSE

**14.3** **Statistiques** **documentaires**

||
||
||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **51**/58

**SOUTRA** \| Dossier Technique 15 TESTS ET VALIDATION

> **15** **Tests** **et** **Validation**
>
> ò **Stratégie** **de** **tests** **multi-niveaux**
>
> Plan de tests couvrant unitaires, intégration, API, performance et
> sécurité.
>
> **Objectif** **:** Couverture *\>* 80% \| **Stack** **:** JUnit 5,
> Mockito, TestContainers, RestAssured, JMeter.

**15.1** **Pyramide** **de** **tests**

> **E2E** **/** **UAT**
>
> **Performance** **&** **Sécurité**
>
> **Tests** **API** **(RestAssured)**
>
> **Tests** **Intégration** **(TestContainers)**
>
> **Tests** **Unitaires** **(JUnit** **5** **+** **Mockito)**
>
> **Figure** **12** **–** Pyramide de tests — du plus rapide au plus
> réaliste

**15.2** **Plan** **de** **tests** **par** **module**

||
||
||
||
||
||
||
||
||
||
||

> Microfinance Core Banking **52**/58

**SOUTRA** \| Dossier Technique 15 TESTS ET VALIDATION

**15.3** **Scénarios** **critiques**

> Ł **10** **scénarios** **incontournables**
>
> **1.** Retrait avec solde insufisant → rejet avec code erreur
>
> **2.** Transaction 600k FCFA → statut EN_ATTENTE + notification
> superviseur
>
> **3.** Auto-approbation (init = superv) → rejet immédiat
>
> **4.** 5 échecs login → verrouillage 30min + log audit
>
> **5.** Ouverture compte sans KYC VALIDE → rejet
>
> **6.** Clôture compte avec solde = 0 → rejet
>
> **7.** Crédit hors bornes produit → rejet validation
>
> **8.** Accès concurrent même compte → OptimisticLockException gérée
>
> **9.** Batch agios sur comptes à découvert → prélèvement correct
>
> **10.** Carte Visa opposée → paiement refusé

**15.4** **Outils** **et** **frameworks**

||
||
||
||
||
||
||
||
||
||
||

**15.5** **Critères** **d’acceptation**

||
||
||
||
||
||
||
||

> Microfinance Core Banking **53**/58

**SOUTRA** \| Dossier Technique 16 DÉPLOIEMENT ET MAINTENANCE

> **16** **Déploiement** **et** **Maintenance**
>
> ò **Stratégie** **DevOps** **et** **exploitation**
>
> Pipeline CI/CD, conteneurisation Docker, monitoring, backup et
> procédures de maintenance. **Stack** **:** Docker, Kubernetes,
> Jenkins/GitLab CI, Prometheus, Grafana, ELK.

**16.1** **Pipeline** **CI/CD**

> **BUILD** **Maven/Gradle**

**TESTS** **JUnit** **+** **Mockito**

**QUALITY** **SonarQube**

**PACKAGE** **Docker** **Image**

**SECURITY** **OWASP** **ZAP**

**DEPLOY** **K8s** **/** **Staging**

> **Figure** **13** **–** Pipeline CI/CD en 6 étapes

**16.2** **Architecture** **Docker**

||
||
||
||
||
||
||
||
||

**16.3** **Profils** **Spring** **Boot**

||
||
||
||
||
||

**16.4** **Monitoring** **et** **observabilité**

> Microfinance Core Banking **54**/58

**SOUTRA** \| Dossier Technique 16 DÉPLOIEMENT ET MAINTENANCE

||
||
||
||
||
||
||
||
||
||

**16.5** **Stratégie** **de** **sauvegarde**

> u **Règle** **de** **Gestion** **—** **Politique** **de** **backup**

||
||
||
||
||
||
||

**16.6** **Procédures** **de** **maintenance**

||
||
||
||
||
||
||
||
||
||

**16.7** **Procédure** **de** **mise** **en** **production**

> . **Checklist** **MEP**
>
> **1.** Tous les tests CI/CD passent (unitaires + intégration + API)
>
> **2.** SonarQube : 0 blocker, 0 critical, couverture ≥ 80%
>
> **3.** OWASP ZAP : 0 vulnérabilité haute/critique
>
> **4.** Backup complet de la base avant migration
>
> Microfinance Core Banking **55**/58

**SOUTRA** \| Dossier Technique 16 DÉPLOIEMENT ET MAINTENANCE

> **5.** Flyway : migrations testées sur staging
>
> **6.** Tag Git + release notes documentées
>
> **7.** Déploiement rolling update (zéro downtime)
>
> **8.** Smoke tests post-déploiement (endpoints critiques)
>
> **9.** Monitoring 30min post-MEP (erreurs, latence, logs)
>
> Microfinance Core Banking **56**/58

**SOUTRA** \| Dossier Technique 16 DÉPLOIEMENT ET MAINTENANCE

**Annexes**

> ò **Documents** **complémentaires**
>
> Références techniques, glossaire, et ressources additionnelles du
> projet Soutra Core Banking.

**Annexe** **A** **—** **Glossaire**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

**Annexe** **B** **—** **Stack** **technologique**

> Microfinance Core Banking **57**/58
>
> **SOUTRA** \| Dossier Technique 16 DÉPLOIEMENT ET MAINTENANCE

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Annexe** **C** **—** **Références** **documentaires**
>
> **1.** **BCEAO** — Instruction no008-12-2016 relative aux règles
> prudentielles applicables aux EMF
>
> **2.** **SYSCOHADA** — Plan comptable révisé applicable aux
> établissements de microfinance
>
> **3.** **OHADA** — Acte Uniforme portant organisation des sûretés
> (révisé 2010)
>
> **4.** **PCI** **DSS** — Payment Card Industry Data Security Standard
> v4.0
>
> **5.** **RGPD** — Règlement Général sur la Protection des Données (UE
> 2016/679)
>
> **6.** **ISO** **27001** **:2022** — Management de la sécurité de
> l’information
>
> **7.** **UML** **2.5** — Unified Modeling Language Specification (OMG)
>
> **8.** **BPMN** **2.0** — Business Process Model and Notation (OMG)
>
> **9.** **Spring** **Boot** — Documentation oficielle v3.x (spring.io)

**10.** **Oracle** **Database** — Documentation 21c (docs.oracle.com)

> **Fin** **du** **Dossier** **Technique**
>
> Soutra Core Banking System — Version 2.0 — Mai 2026
>
> Document confidentiel — Reproduction interdite sans autorisation
>
> Microfinance Core Banking **58**/58
