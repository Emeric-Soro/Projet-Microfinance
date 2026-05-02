BEGIN
    EXECUTE IMMEDIATE '
        ALTER TABLE carte_visa ADD (
            type_carte VARCHAR2(20 CHAR) DEFAULT ''DEBIT'',
            plafond_mensuel NUMBER(19,2),
            solde_prepaye NUMBER(19,2) DEFAULT 0,
            pin_hash VARCHAR2(255 CHAR),
            tentative_pin NUMBER(3,0) DEFAULT 0,
            bloque NUMBER(1,0) DEFAULT 0,
            token_carte VARCHAR2(255 CHAR),
            date_derniere_utilisation TIMESTAMP
        )';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 AND SQLCODE != -1430 THEN RAISE; END IF;
END;
/
