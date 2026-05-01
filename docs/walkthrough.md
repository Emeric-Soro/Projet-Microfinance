# Walkthrough — Mise à Jour Backend Microfinance

## Résumé

Transformation du Core Banking existant en système de gestion de microfinance complet. **34 fichiers** créés ou modifiés, compilation Maven réussie.

---

## Changements Effectués

### 1. Nouveau Module Crédits/Prêts (Cœur Métier)

Le module complet de gestion des crédits avec le workflow :

```
Demande → Étude → Approbation → Décaissement → Remboursement → Solde
```

**Entités créées :**
- [DemandeCredit.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/DemandeCredit.java) — Demande de prêt en instruction
- [Credit.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/Credit.java) — Prêt actif après décaissement
- [Echeance.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/Echeance.java) — Ligne du tableau d'amortissement
- [Garantie.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/Garantie.java) — Garantie associée à un crédit

**Services clés :**
- [CreditServiceImpl.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/service/credit/CreditServiceImpl.java) — Logique métier complète (soumission, approbation, décaissement, remboursement)
- [AmortissementService.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/service/credit/AmortissementService.java) — Calcul du tableau d'amortissement (3 méthodes : dégressif, constant, in fine)

**Endpoints REST :**

| Méthode | URL | Rôle |
|---------|-----|------|
| `POST` | `/api/v1/credits/demandes` | Soumettre une demande |
| `GET` | `/api/v1/credits/demandes` | Lister les demandes en attente |
| `PUT` | `/api/v1/credits/demandes/{id}/decision` | Approuver / Rejeter |
| `POST` | `/api/v1/credits/{id}/decaissement` | Décaisser le prêt |
| `POST` | `/api/v1/credits/{id}/remboursement` | Enregistrer un remboursement |
| `GET` | `/api/v1/credits/{id}/echeancier` | Tableau d'amortissement |
| `GET` | `/api/v1/credits/client/{idClient}` | Crédits d'un client |

---

### 2. Entités de Paramétrage

- [Agence.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/Agence.java) — Points de service / agences
- [ProduitCredit.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/ProduitCredit.java) — Catalogue des produits de prêt
- [ProduitEpargne.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/ProduitEpargne.java) — Catalogue des produits d'épargne
- [StatutCredit.java](file:///c:/Users/emeso/OneDrive/Bureau/Projet-Microfinance/backend/src/main/java/com/microfinance/core_banking/entity/StatutCredit.java) — Table de référence des statuts de crédit

---

### 3. Enrichissement des Entités Existantes

**Client.java** — Ajout des champs KYC microfinance :
- `numeroPieceIdentite`, `typePieceIdentite`, `profession`, `revenuMensuel`, `secteurActivite`
- Nouvelles relations vers `DemandeCredit` et `Credit`

**Compte.java** — Ajout de :
- Relation `ManyToOne` vers `Agence` (agence de gestion)
- Relation `ManyToOne` vers `ProduitEpargne` (type de produit d'épargne)

**Utilisateur.java** — Ajout de :
- `nomUtilisateur`, `prenomUtilisateur` (pour les employés internes)
- Relation `ManyToOne` vers `Agence` (rattachement)
- `client` rendu **nullable** (un guichetier n'est pas un client)

---

### 4. Sécurité RBAC

Nouveaux rôles ajoutés dans SecurityConfig :
- `AGENT_CREDIT` → Consultation et soumission de demandes
- `CHEF_AGENCE` → Approbation/rejet + décaissement
- `DIRECTEUR` → Accès complet au module crédits

---

### 5. Enums et Types

- `TypeGarantie` : CAUTION_SOLIDAIRE, NANTISSEMENT, HYPOTHEQUE, GAGE, DEPOT_A_TERME
- `MethodeCalculInteret` : DEGRESSIF, CONSTANT, IN_FINE
- `StatutDemande` : EN_ATTENTE, EN_ETUDE, APPROUVEE, REJETEE

---

## Validation

- ✅ `mvnw compile` — **BUILD SUCCESS** (0 erreurs)

---

## Données de Référence Nécessaires

Pour que le module crédits fonctionne, il faut insérer en base les données de paramétrage :

```sql
-- Statuts de crédit
INSERT INTO statut_credit (code_statut, libelle, created_at, updated_at) VALUES
('APPROUVE', 'Approuvé', NOW(), NOW()),
('DECAISSE', 'Décaissé', NOW(), NOW()),
('EN_COURS', 'En cours de remboursement', NOW(), NOW()),
('EN_RETARD', 'En retard de paiement', NOW(), NOW()),
('SOLDE', 'Soldé', NOW(), NOW()),
('DOUTEUX', 'Créance douteuse', NOW(), NOW()),
('CONTENTIEUX', 'En contentieux', NOW(), NOW()),
('PASSE_EN_PERTE', 'Passé en perte', NOW(), NOW());

-- Rôles microfinance
INSERT INTO role_utilisateur (code_role_utilisateur, intitule_role, created_at, updated_at) VALUES
('AGENT_CREDIT', 'Agent de Crédit', NOW(), NOW()),
('CHEF_AGENCE', 'Chef d''Agence', NOW(), NOW()),
('DIRECTEUR', 'Directeur Général', NOW(), NOW());

-- Exemple de produit de crédit
INSERT INTO produit_credit (code_produit, libelle, taux_interet_annuel, duree_min_mois, duree_max_mois, montant_min, montant_max, methode_calcul, frais_dossier_pourcentage, penalite_retard_pourcentage, est_actif, created_at, updated_at) VALUES
('MC-COMMERCE', 'Micro-crédit Commerce', 18.0000, 3, 24, 50000.00, 5000000.00, 'DEGRESSIF', 2.0000, 1.0000, true, NOW(), NOW());
```
