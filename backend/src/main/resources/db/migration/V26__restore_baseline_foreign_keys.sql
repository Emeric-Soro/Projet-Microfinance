BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE carte_visa
        ADD CONSTRAINT fk_carte_visa_compte
        FOREIGN KEY (id_compte) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE utilisateur_role
        ADD CONSTRAINT fk_utilisateur_role_user
        FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE utilisateur_role
        ADD CONSTRAINT fk_utilisateur_role_role
        FOREIGN KEY (id_role) REFERENCES role_utilisateur(id_role)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE affectation_utilisateur_agence
        ADD CONSTRAINT fk_affectation_user
        FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE demande_credit
        ADD CONSTRAINT fk_demande_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE credit
        ADD CONSTRAINT fk_credit_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE depot_a_terme
        ADD CONSTRAINT fk_depot_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE session_caisse
        ADD CONSTRAINT fk_session_user
        FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE action_en_attente
        ADD CONSTRAINT fk_action_maker
        FOREIGN KEY (id_maker) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE action_en_attente
        ADD CONSTRAINT fk_action_checker
        FOREIGN KEY (id_checker) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE alerte_conformite
        ADD CONSTRAINT fk_alerte_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE alerte_conformite
        ADD CONSTRAINT fk_alerte_transaction
        FOREIGN KEY (id_transaction) REFERENCES bank_transaction(id_transaction)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE appareil_client
        ADD CONSTRAINT fk_appareil_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE client
        ADD CONSTRAINT fk_client_agence
        FOREIGN KEY (id_agence) REFERENCES agence(id_agence)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE compte
        ADD CONSTRAINT fk_compte_agence
        FOREIGN KEY (id_agence) REFERENCES agence(id_agence)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE utilisateur
        ADD CONSTRAINT fk_user_agence_active
        FOREIGN KEY (id_agence_active) REFERENCES agence(id_agence)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction
        ADD CONSTRAINT fk_tx_agence_operation
        FOREIGN KEY (id_agence_operation) REFERENCES agence(id_agence)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction
        ADD CONSTRAINT fk_tx_session_caisse
        FOREIGN KEY (id_session_caisse) REFERENCES session_caisse(id_session_caisse)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE depot_a_terme
        ADD CONSTRAINT fk_dat_compte_support
        FOREIGN KEY (id_compte_support) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE bank_transaction
        ADD (code_operation_metier VARCHAR2(40 CHAR))';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE wallet_client
        ADD CONSTRAINT fk_wallet_client
        FOREIGN KEY (id_client) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE wallet_client
        ADD CONSTRAINT fk_wallet_compte
        FOREIGN KEY (id_compte) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE ordre_paiement_externe
        ADD CONSTRAINT fk_ordre_paiement_compte
        FOREIGN KEY (id_compte) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE chequier
        ADD CONSTRAINT fk_chequier_compte
        FOREIGN KEY (id_compte) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE remise_cheque
        ADD CONSTRAINT fk_remise_compte
        FOREIGN KEY (id_compte_remise) REFERENCES compte(id_compte)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE consultation_bic
        ADD CONSTRAINT fk_consultation_bic_client
        FOREIGN KEY (client_id) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE consentement_open_banking
        ADD CONSTRAINT fk_consentement_client
        FOREIGN KEY (client_id) REFERENCES client(id_client)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE mutation_personnel
        ADD CONSTRAINT fk_mutation_validateur
        FOREIGN KEY (id_validateur) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE operation_deplacee
        ADD CONSTRAINT fk_operation_deplacee_transaction
        FOREIGN KEY (id_transaction) REFERENCES bank_transaction(id_transaction)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE rapprochement_inter_agence
        ADD CONSTRAINT fk_rapprochement_validateur
        FOREIGN KEY (id_validateur) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE historique_mot_de_passe_utilisateur
        ADD CONSTRAINT fk_hist_mdp_user
        FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2275 THEN RAISE; END IF;
END;
/
