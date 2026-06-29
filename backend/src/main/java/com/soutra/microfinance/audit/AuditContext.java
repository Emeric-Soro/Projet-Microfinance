package com.soutra.microfinance.audit;

/**
 * Conteneur ThreadLocal permettant aux services métier de passer des données contextuelles
 * (id_entite, avant/après) à l'AuditLogAspect de façon transparente.
 *
 * Usage dans un service :
 * <pre>
 *   AuditContext.setIdEntite(String.valueOf(idClient));
 *   AuditContext.setDetailsAvant(objectMapper.writeValueAsString(anciennesValeurs));
 *   // ... effectuer la modification ...
 *   AuditContext.setDetailsApres(objectMapper.writeValueAsString(nouvellesValeurs));
 *   // L'aspect lit et efface le contexte automatiquement après persistance.
 * </pre>
 */
public final class AuditContext {

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private static final ThreadLocal<AuditContextData> HOLDER = new ThreadLocal<>();

    private AuditContext() {}

    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"serialization_error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    public static void setAction(String action) {
        getOrCreate().setAction(action);
    }

    public static void setIdEntite(String idEntite) {
        getOrCreate().setIdEntite(idEntite);
    }

    public static void setDetailsAvant(String json) {
        getOrCreate().setDetailsAvant(json);
    }

    public static void setDetailsApres(String json) {
        getOrCreate().setDetailsApres(json);
    }

    /** Retourne le contexte courant (peut être null si aucun service n'a renseigné de contexte). */
    public static AuditContextData get() {
        return HOLDER.get();
    }

    /** Efface le contexte du thread courant. Appelé systématiquement par l'aspect après persistance. */
    public static void clear() {
        HOLDER.remove();
    }

    private static AuditContextData getOrCreate() {
        if (HOLDER.get() == null) {
            HOLDER.set(new AuditContextData());
        }
        return HOLDER.get();
    }
}
