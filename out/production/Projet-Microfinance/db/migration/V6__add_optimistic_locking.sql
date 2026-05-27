DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_tables WHERE table_name = 'COMPTE';
    IF v_count > 0 THEN
        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'COMPTE' AND column_name = 'VERSION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE COMPTE ADD version NUMBER(10,0) DEFAULT 0 NOT NULL';
        END IF;
    END IF;
END;
/

DECLARE
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*) INTO v_count FROM user_tables WHERE table_name = 'CREDIT';
    IF v_count > 0 THEN
        SELECT COUNT(*) INTO v_count FROM user_tab_cols WHERE table_name = 'CREDIT' AND column_name = 'VERSION';
        IF v_count = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE CREDIT ADD version NUMBER(10,0) DEFAULT 0 NOT NULL';
        END IF;
    END IF;
END;
/
