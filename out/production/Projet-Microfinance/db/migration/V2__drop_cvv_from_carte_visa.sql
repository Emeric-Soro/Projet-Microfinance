DECLARE
    v_table_exists NUMBER := 0;
    v_column_exists NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_table_exists
    FROM user_tables
    WHERE table_name = 'CARTE_VISA';

    IF v_table_exists > 0 THEN
        SELECT COUNT(*)
        INTO v_column_exists
        FROM user_tab_cols
        WHERE table_name = 'CARTE_VISA'
          AND column_name = 'CVV';

        IF v_column_exists > 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE carte_visa DROP COLUMN cvv';
        END IF;
    END IF;
END;
/
