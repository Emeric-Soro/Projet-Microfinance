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
        EXECUTE IMMEDIATE q'[
            MERGE INTO statut_envoi cible
            USING (SELECT 'EN_ATTENTE' AS code_statut_envoi, 'En attente' AS libelle FROM dual) src
            ON (cible.code_statut_envoi = src.code_statut_envoi)
            WHEN NOT MATCHED THEN
                INSERT (code_statut_envoi, libelle, created_at, updated_at)
                VALUES (src.code_statut_envoi, src.libelle, SYSTIMESTAMP, SYSTIMESTAMP)
        ]';

        EXECUTE IMMEDIATE q'[
            MERGE INTO statut_envoi cible
            USING (SELECT 'ENVOYE' AS code_statut_envoi, 'Envoye' AS libelle FROM dual) src
            ON (cible.code_statut_envoi = src.code_statut_envoi)
            WHEN NOT MATCHED THEN
                INSERT (code_statut_envoi, libelle, created_at, updated_at)
                VALUES (src.code_statut_envoi, src.libelle, SYSTIMESTAMP, SYSTIMESTAMP)
        ]';

        EXECUTE IMMEDIATE q'[
            MERGE INTO statut_envoi cible
            USING (SELECT 'ECHEC' AS code_statut_envoi, 'Echec' AS libelle FROM dual) src
            ON (cible.code_statut_envoi = src.code_statut_envoi)
            WHEN NOT MATCHED THEN
                INSERT (code_statut_envoi, libelle, created_at, updated_at)
                VALUES (src.code_statut_envoi, src.libelle, SYSTIMESTAMP, SYSTIMESTAMP)
        ]';
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
        EXECUTE IMMEDIATE q'[
            MERGE INTO type_canal cible
            USING (SELECT 'SMS' AS code_canal, 'SMS' AS libelle FROM dual) src
            ON (cible.code_canal = src.code_canal)
            WHEN NOT MATCHED THEN
                INSERT (code_canal, libelle, created_at, updated_at)
                VALUES (src.code_canal, src.libelle, SYSTIMESTAMP, SYSTIMESTAMP)
        ]';
    END IF;
END;
/
