DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'NOTIFICATION';

    IF v_count > 0 THEN
        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'NOTIFICATION'
          AND column_name = 'ERREUR_ENVOI';

        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE notification ADD (erreur_envoi VARCHAR2(500 CHAR))';
        END IF;

        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'NOTIFICATION'
          AND column_name = 'DATE_ENVOI'
          AND nullable = 'N';

        IF v_count > 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE notification MODIFY (date_envoi NULL)';
        END IF;
    END IF;
END;
/

DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'STATUT_ENVOI';

    IF v_count > 0 THEN
        INSERT INTO statut_envoi (code_statut_envoi, libelle, created_at, updated_at)
        SELECT 'EN_ATTENTE', 'En attente', SYSTIMESTAMP, SYSTIMESTAMP
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM statut_envoi
            WHERE code_statut_envoi = 'EN_ATTENTE'
        );

        INSERT INTO statut_envoi (code_statut_envoi, libelle, created_at, updated_at)
        SELECT 'ENVOYE', 'Envoye', SYSTIMESTAMP, SYSTIMESTAMP
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM statut_envoi
            WHERE code_statut_envoi = 'ENVOYE'
        );

        INSERT INTO statut_envoi (code_statut_envoi, libelle, created_at, updated_at)
        SELECT 'ECHEC', 'Echec', SYSTIMESTAMP, SYSTIMESTAMP
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM statut_envoi
            WHERE code_statut_envoi = 'ECHEC'
        );
    END IF;
END;
/

DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'TYPE_CANAL';

    IF v_count > 0 THEN
        INSERT INTO type_canal (code_canal, libelle, created_at, updated_at)
        SELECT 'SMS', 'SMS', SYSTIMESTAMP, SYSTIMESTAMP
        FROM dual
        WHERE NOT EXISTS (
            SELECT 1
            FROM type_canal
            WHERE code_canal = 'SMS'
        );
    END IF;
END;
/
