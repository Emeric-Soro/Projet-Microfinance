-- V5: Ajout de la colonne id_agence sur client et utilisateur (Système Décentralisé)
DECLARE
    v_count NUMBER := 0;
BEGIN
    -- Colonne id_agence sur client
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'CLIENT';

    IF v_count > 0 THEN
        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'CLIENT'
          AND column_name = 'ID_AGENCE';

        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (id_agence NUMBER(19,0) NULL)';
        END IF;
    END IF;

    -- Colonne id_agence sur utilisateur
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'UTILISATEUR';

    IF v_count > 0 THEN
        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'UTILISATEUR'
          AND column_name = 'ID_AGENCE';

        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (id_agence NUMBER(19,0) NULL)';
        END IF;
    END IF;
END;
/
