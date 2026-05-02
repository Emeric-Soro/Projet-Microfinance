MERGE INTO permission_securite target
USING (
    SELECT 'CREATE_CARTE' code_permission, 'Emettre des cartes bancaires' libelle_permission, 'MONETIQUE' module_code, 'Emission de nouvelles cartes bancaires pour les clients' description_permission FROM dual
    UNION ALL
    SELECT 'UPDATE_CARTE', 'Gerer les cartes bancaires (PIN, blocage, déblocage)', 'MONETIQUE', 'Gestion du PIN, blocage et déblocage des cartes bancaires' FROM dual
    UNION ALL
    SELECT 'PAYMENT_CARTE', 'Effectuer des paiements par carte', 'MONETIQUE', 'Traitement des opérations de paiement par carte' FROM dual
    UNION ALL
    SELECT 'VIEW_CLIENT', 'Consulter les informations clients', 'CLIENT', 'Consultation des informations et dossiers clients' FROM dual
    UNION ALL
    SELECT 'ACCOUNT_VIEW', 'Consulter les comptes', 'ACCOUNT', 'Consultation des comptes clients et de leurs soldes' FROM dual
    UNION ALL
    SELECT 'ACCOUNT_MANAGE', 'Gerer les comptes', 'ACCOUNT', 'Création, modification et clôture des comptes clients' FROM dual
    UNION ALL
    SELECT 'TRANSACTION_CREATE', 'Creer des transactions', 'TRANSACTION', 'Saisie et création d''opérations de transaction' FROM dual
    UNION ALL
    SELECT 'TRANSACTION_VALIDATE', 'Valider des transactions superviseur', 'TRANSACTION', 'Validation superviseur des opérations de transaction' FROM dual
    UNION ALL
    SELECT 'USER_VIEW', 'Consulter les utilisateurs', 'USER', 'Consultation des profils utilisateurs du système' FROM dual
    UNION ALL
    SELECT 'USER_MANAGE', 'Gerer les utilisateurs', 'USER', 'Création, modification et désactivation des utilisateurs' FROM dual
    UNION ALL
    SELECT 'ROLE_MANAGE', 'Gerer les rôles', 'SECURITY', 'Création, modification et affectation des rôles et profils' FROM dual
    UNION ALL
    SELECT 'PARAMETER_MANAGE', 'Gerer les paramètres', 'PARAMETER', 'Gestion des paramètres généraux et configurations du système' FROM dual
    UNION ALL
    SELECT 'CASH_VIEW', 'Consulter les caisses', 'TREASURY', 'Consultation des caisses, soldes et mouvements de caisse' FROM dual
    UNION ALL
    SELECT 'CASH_MANAGE', 'Gerer les caisses', 'TREASURY', 'Gestion des caisses, ouvertures, clôtures et opérations de caisse' FROM dual
    UNION ALL
    SELECT 'AUDIT_VIEW', 'Consulter les traces d''audit', 'SECURITY', 'Consultation des journaux de traces d''audit et d''activité' FROM dual
) source
ON (target.code_permission = source.code_permission)
WHEN NOT MATCHED THEN
    INSERT (code_permission, libelle_permission, module_code, description_permission, actif, created_at, updated_at)
    VALUES (source.code_permission, source.libelle_permission, source.module_code, source.description_permission, 1, SYSTIMESTAMP, SYSTIMESTAMP);
