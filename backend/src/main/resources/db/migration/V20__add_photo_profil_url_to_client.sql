-- V20 : Ajout de la colonne photo_profil_url sur la table soutra_client
-- Sépare la photo de profil (portrait client) de la pièce d'identité (scan CNI/Passeport)
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tab_cols
    WHERE table_name = 'SOUTRA_CLIENT'
      AND column_name = 'PHOTO_PROFIL_URL';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE soutra_client ADD (photo_profil_url VARCHAR2(255 CHAR) NULL)';
    END IF;
END;
/
