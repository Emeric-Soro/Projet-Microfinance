-- =============================================================================
-- V17 : Indexes manquants sur les tables créées post-V8
--       (annotations @Index des entités JPA non reflétées dans V0_001)
-- =============================================================================
-- Entité Beneficiaire : @Index(name = "ix_soutra_beneficiaire_client",
--                               columnList = "id_client")
-- Entité DocumentClient : @Index(name = "idx_doc_client",
--                                columnList = "id_client"),
--                         @Index(name = "idx_doc_date",
--                                columnList = "date_upload")
-- =============================================================================

-- 1. Index ix_soutra_beneficiaire_client
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE INDEX ix_soutra_beneficiaire_client
            ON soutra_beneficiaire (id_client)
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

-- 2. Index idx_doc_client
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE INDEX idx_doc_client
            ON soutra_document_client (id_client)
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

-- 3. Index idx_doc_date
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE INDEX idx_doc_date
            ON soutra_document_client (date_upload)
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN RAISE; END IF;
END;
/
