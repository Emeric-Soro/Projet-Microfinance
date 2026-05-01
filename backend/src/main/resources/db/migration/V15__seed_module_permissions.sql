MERGE INTO permission_securite target
USING (
    SELECT 'ACCOUNTING_VIEW' code_permission, 'Consulter la comptabilite' libelle_permission, 'ACCOUNTING' module_code, 'Lecture classes, comptes, journaux, grand livre et balance' description_permission FROM dual
    UNION ALL
    SELECT 'ACCOUNTING_MANAGE', 'Gerer la comptabilite' libelle_permission, 'ACCOUNTING' module_code, 'Creation de classes, comptes, journaux, schemas et clotures via maker-checker' description_permission FROM dual
    UNION ALL
    SELECT 'TREASURY_VIEW', 'Consulter la tresorerie' libelle_permission, 'TREASURY' module_code, 'Lecture caisses, coffres, sessions et mouvements' description_permission FROM dual
    UNION ALL
    SELECT 'TREASURY_MANAGE', 'Gerer la tresorerie' libelle_permission, 'TREASURY' module_code, 'Creation de caisses/coffres et operations sensibles de tresorerie' description_permission FROM dual
    UNION ALL
    SELECT 'CREDIT_VIEW', 'Consulter le credit' libelle_permission, 'CREDIT' module_code, 'Lecture produits, demandes, credits, echeances, garanties et provisions' description_permission FROM dual
    UNION ALL
    SELECT 'CREDIT_MANAGE', 'Gerer le credit' libelle_permission, 'CREDIT' module_code, 'Creation produits, decisions, deblocages et traitements sensibles' description_permission FROM dual
    UNION ALL
    SELECT 'DIGITAL_VIEW', 'Consulter le digital' libelle_permission, 'DIGITAL' module_code, 'Lecture partenaires API, appareils clients et employes' description_permission FROM dual
    UNION ALL
    SELECT 'DIGITAL_MANAGE', 'Gerer le digital' libelle_permission, 'DIGITAL' module_code, 'Creation partenaires API, appareils et administration des canaux' description_permission FROM dual
    UNION ALL
    SELECT 'VALIDATION_VIEW', 'Consulter les validations' libelle_permission, 'VALIDATION' module_code, 'Lecture des actions en attente' description_permission FROM dual
    UNION ALL
    SELECT 'VALIDATION_DECIDE', 'Decider les validations' libelle_permission, 'VALIDATION' module_code, 'Approbation ou rejet des actions maker-checker' description_permission FROM dual
) source
ON (target.code_permission = source.code_permission)
WHEN NOT MATCHED THEN
    INSERT (code_permission, libelle_permission, module_code, description_permission, actif, created_at, updated_at)
    VALUES (source.code_permission, source.libelle_permission, source.module_code, source.description_permission, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_permission_securite target
USING (
    SELECT r.id_role, p.id_permission
    FROM role_utilisateur r
    JOIN permission_securite p ON 1 = 1
    WHERE UPPER(r.code_role_utilisateur) = 'ADMIN'
) source
ON (target.id_role = source.id_role AND target.id_permission = source.id_permission)
WHEN NOT MATCHED THEN
    INSERT (id_role, id_permission, created_at, updated_at)
    VALUES (source.id_role, source.id_permission, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_permission_securite target
USING (
    SELECT r.id_role, p.id_permission
    FROM role_utilisateur r
    JOIN permission_securite p ON p.code_permission IN (
        'ACCOUNTING_VIEW','ACCOUNTING_MANAGE',
        'TREASURY_VIEW','TREASURY_MANAGE',
        'CREDIT_VIEW','CREDIT_MANAGE',
        'DIGITAL_VIEW','DIGITAL_MANAGE',
        'VALIDATION_VIEW','VALIDATION_DECIDE',
        'SECURITY_PERMISSION_VIEW','SECURITY_PERMISSION_MANAGE','SECURITY_AUDIT_VIEW'
    )
    WHERE UPPER(r.code_role_utilisateur) = 'SUPERVISEUR'
) source
ON (target.id_role = source.id_role AND target.id_permission = source.id_permission)
WHEN NOT MATCHED THEN
    INSERT (id_role, id_permission, created_at, updated_at)
    VALUES (source.id_role, source.id_permission, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_permission_securite target
USING (
    SELECT r.id_role, p.id_permission
    FROM role_utilisateur r
    JOIN permission_securite p ON p.code_permission IN (
        'ACCOUNTING_VIEW',
        'TREASURY_VIEW',
        'CREDIT_VIEW',
        'DIGITAL_VIEW',
        'VALIDATION_VIEW'
    )
    WHERE UPPER(r.code_role_utilisateur) = 'GUICHETIER'
) source
ON (target.id_role = source.id_role AND target.id_permission = source.id_permission)
WHEN NOT MATCHED THEN
    INSERT (id_role, id_permission, created_at, updated_at)
    VALUES (source.id_role, source.id_permission, SYSTIMESTAMP, SYSTIMESTAMP);
