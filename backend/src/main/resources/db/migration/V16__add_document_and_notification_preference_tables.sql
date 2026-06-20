-- =============================================================================
-- V16 : Tables documents client + preferences notification + colonnes lu/lue_le
-- =============================================================================
-- NO-OP : Les tables soutra_document_client, soutra_notification_preference,
-- et les colonnes lu/lue_le sur soutra_notification sont gerees par Hibernate
-- (ddl-auto=update) via les entites JPA correspondantes.
-- Les foreign keys referencent des tables creees par Hibernate, pas par Flyway.
-- =============================================================================
SELECT 1 FROM DUAL;
