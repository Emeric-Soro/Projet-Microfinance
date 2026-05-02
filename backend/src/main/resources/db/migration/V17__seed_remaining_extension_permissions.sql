MERGE INTO permission_securite target
USING (
    SELECT 'ORGANIZATION_VIEW' code_permission, 'Consulter l''organisation' libelle_permission, 'ORGANIZATION' module_code, 'Lecture des regions, agences, guichets et reporting reseau' description_permission FROM dual
    UNION ALL
    SELECT 'ORGANIZATION_MANAGE', 'Gerer l''organisation' libelle_permission, 'ORGANIZATION' module_code, 'Creation et validation des entites multi-agences sensibles' description_permission FROM dual
    UNION ALL
    SELECT 'SAVINGS_VIEW', 'Consulter l''epargne' libelle_permission, 'SAVINGS' module_code, 'Lecture des produits d''epargne et depots a terme' description_permission FROM dual
    UNION ALL
    SELECT 'SAVINGS_MANAGE', 'Gerer l''epargne' libelle_permission, 'SAVINGS' module_code, 'Creation des produits et souscription d''operations d''epargne sensibles' description_permission FROM dual
    UNION ALL
    SELECT 'PAYMENTS_VIEW', 'Consulter les paiements externes' libelle_permission, 'PAYMENTS' module_code, 'Lecture mobile money, lots de compensation et ordres externes' description_permission FROM dual
    UNION ALL
    SELECT 'PAYMENTS_MANAGE', 'Gerer les paiements externes' libelle_permission, 'PAYMENTS' module_code, 'Administration et validation des operations externes sensibles' description_permission FROM dual
    UNION ALL
    SELECT 'RISK_VIEW', 'Consulter les risques' libelle_permission, 'RISK' module_code, 'Lecture des risques, incidents, stress tests et tableaux de liquidite' description_permission FROM dual
    UNION ALL
    SELECT 'RISK_MANAGE', 'Gerer les risques' libelle_permission, 'RISK' module_code, 'Creation et execution des traitements risques sensibles' description_permission FROM dual
    UNION ALL
    SELECT 'FEATURE_VIEW', 'Consulter l''inventaire fonctionnel' libelle_permission, 'FEATURE' module_code, 'Lecture de l''inventaire backend et de couverture' description_permission FROM dual
) source
ON (target.code_permission = source.code_permission)
WHEN NOT MATCHED THEN
    INSERT (code_permission, libelle_permission, module_code, description_permission, actif, created_at, updated_at)
    VALUES (source.code_permission, source.libelle_permission, source.module_code, source.description_permission, 1, SYSTIMESTAMP, SYSTIMESTAMP);
