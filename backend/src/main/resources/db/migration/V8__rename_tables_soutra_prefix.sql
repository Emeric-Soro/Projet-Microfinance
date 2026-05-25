-- V8: Rename core banking tables to SOUTRA_ prefix for brand alignment
-- Per PRD 17 - SOUTRA Microfinance branding
-- Idempotent: handles ORA-00942 (table does not exist) and ORA-04043 (object does not exist)
-- These can occur when Hibernate ddl-auto=update already creates tables with soutra_ prefix.

-- Helper: we catch -942 (table/view does not exist) and -4043 (object does not exist)
-- to make each rename idempotent for fresh and replay scenarios.

-- Rename client table
BEGIN
    EXECUTE IMMEDIATE 'RENAME client TO soutra_client';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename compte table
BEGIN
    EXECUTE IMMEDIATE 'RENAME compte TO soutra_compte';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename credit table
BEGIN
    EXECUTE IMMEDIATE 'RENAME credit TO soutra_credit';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename bank_transaction table to soutra_transaction
BEGIN
    EXECUTE IMMEDIATE 'RENAME bank_transaction TO soutra_transaction';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename ligne_ecriture table to soutra_ligne_ecriture
BEGIN
    EXECUTE IMMEDIATE 'RENAME ligne_ecriture TO soutra_ligne_ecriture';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename echeance table
BEGIN
    EXECUTE IMMEDIATE 'RENAME echeance TO soutra_echeance';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename demande_credit table
BEGIN
    EXECUTE IMMEDIATE 'RENAME demande_credit TO soutra_demande_credit';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename produit_credit table
BEGIN
    EXECUTE IMMEDIATE 'RENAME produit_credit TO soutra_produit_credit';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename produit_epargne table
BEGIN
    EXECUTE IMMEDIATE 'RENAME produit_epargne TO soutra_produit_epargne';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename agence table
BEGIN
    EXECUTE IMMEDIATE 'RENAME agence TO soutra_agence';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename utilisateur table
BEGIN
    EXECUTE IMMEDIATE 'RENAME utilisateur TO soutra_utilisateur';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename caisse table
BEGIN
    EXECUTE IMMEDIATE 'RENAME caisse TO soutra_caisse';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename carte_visa table
BEGIN
    EXECUTE IMMEDIATE 'RENAME carte_visa TO soutra_carte_visa';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename notification table
BEGIN
    EXECUTE IMMEDIATE 'RENAME notification TO soutra_notification';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename garantie table
BEGIN
    EXECUTE IMMEDIATE 'RENAME garantie TO soutra_garantie';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename agio table
BEGIN
    EXECUTE IMMEDIATE 'RENAME agio TO soutra_agio';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename tarification_parametre table
BEGIN
    EXECUTE IMMEDIATE 'RENAME tarification_parametre TO soutra_tarification_parametre';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename system_audit_log table
BEGIN
    EXECUTE IMMEDIATE 'RENAME system_audit_log TO soutra_system_audit_log';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

-- Rename enum/lookup tables
BEGIN
    EXECUTE IMMEDIATE 'RENAME statut_client TO soutra_statut_client';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME statut_compte TO soutra_statut_compte';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME statut_credit TO soutra_statut_credit';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_compte TO soutra_type_compte';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_transaction TO soutra_type_transaction';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_agio TO soutra_type_agio';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_canal TO soutra_type_canal';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME role_utilisateur TO soutra_role_utilisateur';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME statut_envoi TO soutra_statut_envoi';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_piece_identite TO soutra_type_piece_identite';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'RENAME type_garantie TO soutra_type_garantie';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-942, -4043) THEN RAISE; END IF;
END;
/
