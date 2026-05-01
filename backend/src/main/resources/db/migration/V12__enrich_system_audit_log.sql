BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE system_audit_log ADD (
        correlation_id VARCHAR2(100 CHAR),
        user_agent VARCHAR2(500 CHAR),
        role_names VARCHAR2(500 CHAR),
        agency_code VARCHAR2(50 CHAR),
        module_name VARCHAR2(100 CHAR),
        entity_name VARCHAR2(100 CHAR),
        entity_id VARCHAR2(120 CHAR),
        request_method VARCHAR2(20 CHAR),
        request_path VARCHAR2(255 CHAR),
        business_date DATE,
        reason VARCHAR2(500 CHAR),
        before_value CLOB,
        after_value CLOB
    )';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_system_audit_log_correlation_id ON system_audit_log(correlation_id)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_system_audit_log_module_name ON system_audit_log(module_name)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN RAISE; END IF;
END;
/
