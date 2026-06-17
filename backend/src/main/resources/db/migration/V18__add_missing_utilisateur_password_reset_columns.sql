-- =============================================================================
-- V18 : Colonnes utilisateur manquantes pour les bases locales passees en validate
-- =============================================================================
-- V15 etait un no-op lorsque Hibernate ddl-auto=update creait encore ces colonnes.
-- En Docker, ddl-auto=validate exige que Flyway les cree explicitement.
-- =============================================================================

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE soutra_utilisateur ADD reset_token_hash VARCHAR2(255 CHAR)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE soutra_utilisateur ADD reset_token_expire_le TIMESTAMP';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE soutra_utilisateur ADD last_password_change TIMESTAMP';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN RAISE; END IF;
END;
/
