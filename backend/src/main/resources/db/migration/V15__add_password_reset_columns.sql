-- =============================================================================
-- V15 : Ajout des colonnes pour la reinitialisation de mot de passe web
-- =============================================================================
-- NO-OP : Les colonnes reset_token_hash, reset_token_expire_le et
-- last_password_change sont gerees par Hibernate (ddl-auto=update)
-- via l'entite Utilisateur.java. La table soutra_utilisateur n'existe
-- pas au moment de l'execution Flyway (creee apres par Hibernate).
-- =============================================================================
SELECT 1 FROM DUAL;
