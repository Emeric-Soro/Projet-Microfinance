-- V21 : Enrichissement du journal d'audit pour la tracabilite des modifications
-- Ajoute : id_entite (quel enregistrement), details_avant (valeurs avant), details_apres (valeurs apres)

DECLARE v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_tab_cols
    WHERE table_name = 'SOUTRA_SYSTEM_AUDIT_LOG' AND column_name = 'ID_ENTITE';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE soutra_system_audit_log ADD (id_entite VARCHAR2(150 CHAR) NULL)';
    END IF;
END;
/

DECLARE v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_tab_cols
    WHERE table_name = 'SOUTRA_SYSTEM_AUDIT_LOG' AND column_name = 'DETAILS_AVANT';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE soutra_system_audit_log ADD (details_avant CLOB NULL)';
    END IF;
END;
/

DECLARE v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_tab_cols
    WHERE table_name = 'SOUTRA_SYSTEM_AUDIT_LOG' AND column_name = 'DETAILS_APRES';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE soutra_system_audit_log ADD (details_apres CLOB NULL)';
    END IF;
END;
/
