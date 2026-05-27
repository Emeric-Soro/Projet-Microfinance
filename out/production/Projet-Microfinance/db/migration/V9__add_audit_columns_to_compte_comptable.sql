-- V9: Add audit columns to compte_comptable (missed in V7)
-- BaseAuditEntity requires created_at and updated_at on all extending entities.

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE compte_comptable ADD created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-1430) THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE compte_comptable ADD updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE NOT IN (-1430) THEN RAISE; END IF;
END;
/
