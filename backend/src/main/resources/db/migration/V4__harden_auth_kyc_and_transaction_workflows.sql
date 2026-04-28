DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'UTILISATEUR';

    IF v_count > 0 THEN
        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'ACTIF';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (actif NUMBER(1,0) DEFAULT 1 NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'COMPTE_EXPIRE_LE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (compte_expire_le TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'COMPTE_VERROUILLE_JUSQU_AU';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (compte_verrouille_jusqu_au TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'NOMBRE_ECHECS_CONNEXION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (nombre_echecs_connexion NUMBER(10,0) DEFAULT 0 NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'DERNIER_ECHEC_CONNEXION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (dernier_echec_connexion TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'DERNIERE_CONNEXION_REUSSIE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (derniere_connexion_reussie TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'MOT_DE_PASSE_MODIFIE_LE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (mot_de_passe_modifie_le TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'IDENTIFIANTS_EXPIRENT_LE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (identifiants_expirent_le TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'SECOND_FACTEUR_ACTIVE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (second_facteur_active NUMBER(1,0) DEFAULT 1 NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'OTP_CHALLENGE_ID';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (otp_challenge_id VARCHAR2(80 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'OTP_HASH';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (otp_hash VARCHAR2(255 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'OTP_EXPIRE_LE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (otp_expire_le TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'UTILISATEUR' AND column_name = 'OTP_TENTATIVES_RESTANTES';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur ADD (otp_tentatives_restantes NUMBER(10,0) DEFAULT 0 NOT NULL)';
        END IF;

        EXECUTE IMMEDIATE q'[
            UPDATE utilisateur
            SET actif = NVL(actif, 1),
                nombre_echecs_connexion = NVL(nombre_echecs_connexion, 0),
                mot_de_passe_modifie_le = NVL(mot_de_passe_modifie_le, CAST(SYSTIMESTAMP AS TIMESTAMP)),
                identifiants_expirent_le = NVL(identifiants_expirent_le, CAST(SYSTIMESTAMP + NUMTODSINTERVAL(90, 'DAY') AS TIMESTAMP)),
                second_facteur_active = NVL(second_facteur_active, 1),
                otp_tentatives_restantes = NVL(otp_tentatives_restantes, 0)
        ]';

        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'UTILISATEUR'
          AND column_name = 'MOT_DE_PASSE_MODIFIE_LE'
          AND nullable = 'Y';

        IF v_count > 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur MODIFY (mot_de_passe_modifie_le NOT NULL)';
        END IF;

        SELECT COUNT(*)
        INTO v_count
        FROM user_tab_cols
        WHERE table_name = 'UTILISATEUR'
          AND column_name = 'IDENTIFIANTS_EXPIRENT_LE'
          AND nullable = 'Y';

        IF v_count > 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE utilisateur MODIFY (identifiants_expirent_le NOT NULL)';
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
    WHERE table_name = 'CLIENT';

    IF v_count > 0 THEN
        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'TYPE_PIECE_IDENTITE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (type_piece_identite VARCHAR2(30 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'NUMERO_PIECE_IDENTITE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (numero_piece_identite VARCHAR2(80 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'DATE_EXPIRATION_PIECE_IDENTITE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (date_expiration_piece_identite DATE NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'PHOTO_IDENTITE_URL';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (photo_identite_url VARCHAR2(255 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'JUSTIFICATIF_DOMICILE_URL';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (justificatif_domicile_url VARCHAR2(255 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'JUSTIFICATIF_REVENUS_URL';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (justificatif_revenus_url VARCHAR2(255 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'PROFESSION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (profession VARCHAR2(120 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'EMPLOYEUR';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (employeur VARCHAR2(150 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'PAYS_NATIONALITE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (pays_nationalite VARCHAR2(80 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'PAYS_RESIDENCE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (pays_residence VARCHAR2(80 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'PEP';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (pep NUMBER(1,0) DEFAULT 0 NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'NIVEAU_RISQUE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (niveau_risque VARCHAR2(20 CHAR) DEFAULT ''FAIBLE'' NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'STATUT_KYC';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (statut_kyc VARCHAR2(30 CHAR) DEFAULT ''BROUILLON'' NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'DATE_SOUMISSION_KYC';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (date_soumission_kyc DATE NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'DATE_VALIDATION_KYC';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (date_validation_kyc DATE NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'COMMENTAIRE_KYC';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (commentaire_kyc VARCHAR2(500 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CLIENT' AND column_name = 'VALIDATEUR_KYC';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD (validateur_kyc VARCHAR2(120 CHAR) NULL)';
        END IF;

        EXECUTE IMMEDIATE q'[
            SELECT COUNT(*)
            FROM (
                SELECT numero_piece_identite
                FROM client
                WHERE numero_piece_identite IS NOT NULL
                GROUP BY numero_piece_identite
                HAVING COUNT(*) > 1
            )
        ]'
        INTO v_count;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Impossible d''ajouter la contrainte KYC: des numeros de piece dupliques existent deja.');
        END IF;

        SELECT COUNT(*)
        INTO v_count
        FROM user_cons_columns ucc
        JOIN user_constraints uc ON uc.constraint_name = ucc.constraint_name
        WHERE uc.table_name = 'CLIENT'
          AND uc.constraint_type = 'U'
          AND ucc.column_name = 'NUMERO_PIECE_IDENTITE';

        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE client ADD CONSTRAINT uk_client_num_piece_ident UNIQUE (numero_piece_identite)';
        END IF;

        EXECUTE IMMEDIATE q'[
            UPDATE client
            SET pep = NVL(pep, 0),
                niveau_risque = NVL(niveau_risque, 'FAIBLE'),
                statut_kyc = NVL(statut_kyc, 'BROUILLON')
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
    WHERE table_name = 'BANK_TRANSACTION';

    IF v_count > 0 THEN
        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'STATUT_OPERATION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (statut_operation VARCHAR2(30 CHAR) DEFAULT ''EXECUTEE'' NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'VALIDATION_SUPERVISEUR_REQUISE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (validation_superviseur_requise NUMBER(1,0) DEFAULT 0 NOT NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'DATE_VALIDATION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (date_validation TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'DATE_EXECUTION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (date_execution TIMESTAMP NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'MOTIF_REJET';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (motif_rejet VARCHAR2(500 CHAR) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'ID_USER_VALIDATION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (id_user_validation NUMBER(19,0) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'ID_COMPTE_SOURCE';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (id_compte_source NUMBER(19,0) NULL)';
        END IF;

        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'BANK_TRANSACTION' AND column_name = 'ID_COMPTE_DESTINATION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction ADD (id_compte_destination NUMBER(19,0) NULL)';
        END IF;

        EXECUTE IMMEDIATE q'[
            UPDATE bank_transaction
            SET statut_operation = NVL(statut_operation, 'EXECUTEE'),
                validation_superviseur_requise = NVL(validation_superviseur_requise, 0),
                date_execution = NVL(date_execution, date_heure_transaction)
        ]';
    END IF;
END;
/
