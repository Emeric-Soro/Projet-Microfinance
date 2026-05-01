-- ============================================================
-- DONNEES DE REFERENCE MICROFINANCE
-- Ce fichier est execute explicitement apres l'initialisation Hibernate
-- pour conserver les MERGE idempotents sans bloquer Flyway.
-- ============================================================

-- ============================================================
-- 1. STATUTS CLIENT (si pas encore inseres)
-- ============================================================
MERGE INTO statut_client sc
USING (SELECT 'NOUVEAU' AS libelle_statut FROM dual) src
ON (UPPER(sc.libelle_statut) = src.libelle_statut)
WHEN NOT MATCHED THEN INSERT (libelle_statut, date_statut, created_at, updated_at)
VALUES ('NOUVEAU', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_client sc
USING (SELECT 'ACTIF' AS libelle_statut FROM dual) src
ON (UPPER(sc.libelle_statut) = src.libelle_statut)
WHEN NOT MATCHED THEN INSERT (libelle_statut, date_statut, created_at, updated_at)
VALUES ('ACTIF', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_client sc
USING (SELECT 'BLOQUE' AS libelle_statut FROM dual) src
ON (UPPER(sc.libelle_statut) = src.libelle_statut)
WHEN NOT MATCHED THEN INSERT (libelle_statut, date_statut, created_at, updated_at)
VALUES ('BLOQUE', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_client sc
USING (SELECT 'FERME' AS libelle_statut FROM dual) src
ON (UPPER(sc.libelle_statut) = src.libelle_statut)
WHEN NOT MATCHED THEN INSERT (libelle_statut, date_statut, created_at, updated_at)
VALUES ('FERME', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 2. TYPES DE COMPTE
-- ============================================================
MERGE INTO type_compte tc
USING (SELECT 'COURANT' AS libelle FROM dual) src
ON (UPPER(tc.libelle) = src.libelle)
WHEN NOT MATCHED THEN INSERT (libelle, created_at, updated_at)
VALUES ('COURANT', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_compte tc
USING (SELECT 'EPARGNE' AS libelle FROM dual) src
ON (UPPER(tc.libelle) = src.libelle)
WHEN NOT MATCHED THEN INSERT (libelle, created_at, updated_at)
VALUES ('EPARGNE', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_compte tc
USING (SELECT 'DAT' AS libelle FROM dual) src
ON (UPPER(tc.libelle) = src.libelle)
WHEN NOT MATCHED THEN INSERT (libelle, created_at, updated_at)
VALUES ('DAT', SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 3. TYPES DE TRANSACTION
-- ============================================================
MERGE INTO type_transaction tt
USING (SELECT 'DEPOT' AS code_type_transaction FROM dual) src
ON (tt.code_type_transaction = src.code_type_transaction)
WHEN NOT MATCHED THEN INSERT (code_type_transaction, libelle, created_at, updated_at)
VALUES ('DEPOT', 'Depot en especes', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_transaction tt
USING (SELECT 'RETRAIT' AS code_type_transaction FROM dual) src
ON (tt.code_type_transaction = src.code_type_transaction)
WHEN NOT MATCHED THEN INSERT (code_type_transaction, libelle, created_at, updated_at)
VALUES ('RETRAIT', 'Retrait en especes', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_transaction tt
USING (SELECT 'VIREMENT' AS code_type_transaction FROM dual) src
ON (tt.code_type_transaction = src.code_type_transaction)
WHEN NOT MATCHED THEN INSERT (code_type_transaction, libelle, created_at, updated_at)
VALUES ('VIREMENT', 'Virement entre comptes', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_transaction tt
USING (SELECT 'DECAISSEMENT_CREDIT' AS code_type_transaction FROM dual) src
ON (tt.code_type_transaction = src.code_type_transaction)
WHEN NOT MATCHED THEN INSERT (code_type_transaction, libelle, created_at, updated_at)
VALUES ('DECAISSEMENT_CREDIT', 'Decaissement de credit', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_transaction tt
USING (SELECT 'REMBOURSEMENT_CREDIT' AS code_type_transaction FROM dual) src
ON (tt.code_type_transaction = src.code_type_transaction)
WHEN NOT MATCHED THEN INSERT (code_type_transaction, libelle, created_at, updated_at)
VALUES ('REMBOURSEMENT_CREDIT', 'Remboursement d echeance', SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 4. ROLES UTILISATEUR (existants + nouveaux microfinance)
-- ============================================================
MERGE INTO role_utilisateur ru
USING (SELECT 'ADMIN' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('ADMIN', 'Administrateur Systeme', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'GUICHETIER' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('GUICHETIER', 'Caissier / Guichetier', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'CLIENT' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('CLIENT', 'Client E-Banking', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'AGENT_CREDIT' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('AGENT_CREDIT', 'Agent de Credit', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'CHEF_AGENCE' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('CHEF_AGENCE', 'Chef d Agence', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'DIRECTEUR' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('DIRECTEUR', 'Directeur General', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_utilisateur ru
USING (SELECT 'COMPTABLE' AS code_role_utilisateur FROM dual) src
ON (ru.code_role_utilisateur = src.code_role_utilisateur)
WHEN NOT MATCHED THEN INSERT (code_role_utilisateur, intitule_role, created_at, updated_at)
VALUES ('COMPTABLE', 'Comptable', SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 5. STATUTS DE CREDIT (NOUVEAU - Module Credits)
-- ============================================================
MERGE INTO statut_credit sc
USING (SELECT 'APPROUVE' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('APPROUVE', 'Approuve', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'DECAISSE' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('DECAISSE', 'Decaisse', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'EN_COURS' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('EN_COURS', 'En cours de remboursement', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'EN_RETARD' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('EN_RETARD', 'En retard de paiement', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'SOLDE' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('SOLDE', 'Solde integralement', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'DOUTEUX' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('DOUTEUX', 'Creance douteuse', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'CONTENTIEUX' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('CONTENTIEUX', 'En contentieux', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_credit sc
USING (SELECT 'PASSE_EN_PERTE' AS code_statut FROM dual) src
ON (sc.code_statut = src.code_statut)
WHEN NOT MATCHED THEN INSERT (code_statut, libelle, created_at, updated_at)
VALUES ('PASSE_EN_PERTE', 'Passe en perte', SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 6. AGENCE PAR DEFAUT
-- ============================================================
MERGE INTO agence ag
USING (SELECT 'AG-001' AS code_agence FROM dual) src
ON (ag.code_agence = src.code_agence)
WHEN NOT MATCHED THEN INSERT (code_agence, nom, adresse, telephone, est_active, created_at, updated_at)
VALUES ('AG-001', 'Agence Principale', 'Abidjan, Plateau', '+225 27 20 00 00 00', 1, SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 7. PRODUITS DE CREDIT (Catalogue initial)
-- ============================================================
MERGE INTO produit_credit pc
USING (SELECT 'MC-COMMERCE' AS code_produit FROM dual) src
ON (pc.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, duree_min_mois, duree_max_mois, montant_min, montant_max, methode_calcul, frais_dossier_pourcentage, penalite_retard_pourcentage, est_actif, created_at, updated_at)
VALUES ('MC-COMMERCE', 'Micro-credit Commerce', 18.0000, 3, 24, 50000.00, 5000000.00, 'DEGRESSIF', 2.0000, 1.0000, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO produit_credit pc
USING (SELECT 'MC-AGRICULTURE' AS code_produit FROM dual) src
ON (pc.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, duree_min_mois, duree_max_mois, montant_min, montant_max, methode_calcul, frais_dossier_pourcentage, penalite_retard_pourcentage, est_actif, created_at, updated_at)
VALUES ('MC-AGRICULTURE', 'Micro-credit Agriculture', 15.0000, 6, 36, 100000.00, 10000000.00, 'CONSTANT', 1.5000, 0.5000, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO produit_credit pc
USING (SELECT 'PRET-SALARIE' AS code_produit FROM dual) src
ON (pc.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, duree_min_mois, duree_max_mois, montant_min, montant_max, methode_calcul, frais_dossier_pourcentage, penalite_retard_pourcentage, est_actif, created_at, updated_at)
VALUES ('PRET-SALARIE', 'Pret Salarie', 12.0000, 1, 48, 100000.00, 20000000.00, 'DEGRESSIF', 1.0000, 1.5000, 1, SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 8. PRODUITS D'EPARGNE (Catalogue initial)
-- ============================================================
MERGE INTO produit_epargne pe
USING (SELECT 'EP-VUE' AS code_produit FROM dual) src
ON (pe.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, montant_min_ouverture, penalite_retrait_anticipe, duree_min_jours, est_actif, created_at, updated_at)
VALUES ('EP-VUE', 'Epargne a Vue', 3.5000, 5000.00, 0, 0, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO produit_epargne pe
USING (SELECT 'DAT-6M' AS code_produit FROM dual) src
ON (pe.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, montant_min_ouverture, penalite_retrait_anticipe, duree_min_jours, est_actif, created_at, updated_at)
VALUES ('DAT-6M', 'Depot a Terme 6 Mois', 5.5000, 100000.00, 2.0000, 180, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO produit_epargne pe
USING (SELECT 'DAT-12M' AS code_produit FROM dual) src
ON (pe.code_produit = src.code_produit)
WHEN NOT MATCHED THEN INSERT (code_produit, libelle, taux_interet_annuel, montant_min_ouverture, penalite_retrait_anticipe, duree_min_jours, est_actif, created_at, updated_at)
VALUES ('DAT-12M', 'Depot a Terme 12 Mois', 7.0000, 100000.00, 3.0000, 365, 1, SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 9. CANAUX DE NOTIFICATION
-- ============================================================
MERGE INTO type_canal tc
USING (SELECT 'SMS' AS code_canal FROM dual) src
ON (tc.code_canal = src.code_canal)
WHEN NOT MATCHED THEN INSERT (code_canal, libelle, created_at, updated_at)
VALUES ('SMS', 'SMS', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO type_canal tc
USING (SELECT 'EMAIL' AS code_canal FROM dual) src
ON (tc.code_canal = src.code_canal)
WHEN NOT MATCHED THEN INSERT (code_canal, libelle, created_at, updated_at)
VALUES ('EMAIL', 'Email', SYSTIMESTAMP, SYSTIMESTAMP);

-- ============================================================
-- 10. STATUTS D'ENVOI NOTIFICATION
-- ============================================================
MERGE INTO statut_envoi se
USING (SELECT 'ENVOYE' AS code_statut_envoi FROM dual) src
ON (se.code_statut_envoi = src.code_statut_envoi)
WHEN NOT MATCHED THEN INSERT (code_statut_envoi, libelle, created_at, updated_at)
VALUES ('ENVOYE', 'Envoye', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_envoi se
USING (SELECT 'EN_ATTENTE' AS code_statut_envoi FROM dual) src
ON (se.code_statut_envoi = src.code_statut_envoi)
WHEN NOT MATCHED THEN INSERT (code_statut_envoi, libelle, created_at, updated_at)
VALUES ('EN_ATTENTE', 'En attente', SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO statut_envoi se
USING (SELECT 'ECHEC' AS code_statut_envoi FROM dual) src
ON (se.code_statut_envoi = src.code_statut_envoi)
WHEN NOT MATCHED THEN INSERT (code_statut_envoi, libelle, created_at, updated_at)
VALUES ('ECHEC', 'Echec', SYSTIMESTAMP, SYSTIMESTAMP);
