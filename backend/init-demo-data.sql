WHENEVER SQLERROR EXIT FAILURE;

INSERT INTO client (id_client, code_client, nom, prenom, date_naissance, adresse, telephone, email, statut_client, type_piece_identite, numero_piece_identite, pep, niveau_risque, statut_kyc, date_creation)
VALUES (999, 'CLI-DEMO-0001', 'Demo', 'Admin', TO_DATE('1990-01-01', 'YYYY-MM-DD'), '123 Rue Demo', '700000001', 'demo.admin@microfin.local', 'ACTIF', 'CNI', 'CNI-DEMO-0001', 0, 'FAIBLE', 'VALIDE', SYSTIMESTAMP);

INSERT INTO utilisateur (id_user, nom_utilisateur, mot_de_passe_hash, email, id_client, actif, nombre_echecs_connexion, second_facteur_active, otp_tentatives_restantes, mot_de_passe_modifie_le, identifiants_expirent_le)
VALUES (999, 'demo.admin', '$2a$10$DEMOHASHWILLNOTWORKDIRECTLY', 'demo.admin@microfin.local', 999, 1, 0, 0, 0, SYSTIMESTAMP, SYSTIMESTAMP + NUMTODSINTERVAL(90, 'DAY'));

COMMIT;
