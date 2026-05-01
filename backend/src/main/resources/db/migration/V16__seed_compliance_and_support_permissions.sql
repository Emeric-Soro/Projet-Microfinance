MERGE INTO permission_securite target
USING (
    SELECT 'COMPLIANCE_VIEW' code_permission, 'Consulter la conformite' libelle_permission, 'COMPLIANCE' module_code, 'Lecture alertes, rapports et rescans' description_permission FROM dual
    UNION ALL
    SELECT 'COMPLIANCE_MANAGE', 'Gerer la conformite' libelle_permission, 'COMPLIANCE' module_code, 'Creation alertes, rapports et operations sensibles de conformite' description_permission FROM dual
    UNION ALL
    SELECT 'SUPPORT_VIEW', 'Consulter les modules support' libelle_permission, 'SUPPORT' module_code, 'Lecture budgets, fournisseurs, commandes, paie et immobilisations' description_permission FROM dual
    UNION ALL
    SELECT 'SUPPORT_MANAGE', 'Gerer les modules support' libelle_permission, 'SUPPORT' module_code, 'Creation et gestion sensible des modules support' description_permission FROM dual
) source
ON (target.code_permission = source.code_permission)
WHEN NOT MATCHED THEN
    INSERT (code_permission, libelle_permission, module_code, description_permission, actif, created_at, updated_at)
    VALUES (source.code_permission, source.libelle_permission, source.module_code, source.description_permission, 1, SYSTIMESTAMP, SYSTIMESTAMP);

MERGE INTO role_permission_securite target
USING (
    SELECT r.id_role, p.id_permission
    FROM role_utilisateur r
    JOIN permission_securite p ON p.code_permission IN (
        'COMPLIANCE_VIEW','COMPLIANCE_MANAGE','SUPPORT_VIEW','SUPPORT_MANAGE'
    )
    WHERE UPPER(r.code_role_utilisateur) IN ('ADMIN','SUPERVISEUR')
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
        'COMPLIANCE_VIEW','SUPPORT_VIEW'
    )
    WHERE UPPER(r.code_role_utilisateur) = 'GUICHETIER'
) source
ON (target.id_role = source.id_role AND target.id_permission = source.id_permission)
WHEN NOT MATCHED THEN
    INSERT (id_role, id_permission, created_at, updated_at)
    VALUES (source.id_role, source.id_permission, SYSTIMESTAMP, SYSTIMESTAMP);
