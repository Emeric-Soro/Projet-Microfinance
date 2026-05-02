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
