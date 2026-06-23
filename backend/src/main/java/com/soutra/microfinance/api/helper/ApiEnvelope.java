package com.soutra.microfinance.api.helper;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Enveloppe standard pour toutes les reponses des endpoints mobiles.
 *
 * Le client Flutter (comportement code dans les *DataSourceImpl sous
 * `lib/data/**`) attend que chaque reponse non-paginee soit encapsulee
 * dans un objet avec une cle `data` :
 * <pre>
 * {
 *   "status": "success",
 *   "timestamp": "2026-06-20T17:00:00",
 *   "data": [ ... ]
 * }
 * </pre>
 *
 * Pour les reponses paginees, le champ `data` contient directement la
 * structure Spring {@code Page} (avec {@code content}, {@code totalElements},
 * etc.) et le champ optionnel {@code meta} peut transporter des
 * informations complementaires (filtres actifs, totaux agregees, etc.).
 *
 * Pour les reponses scalaires (ex : solde d'un compte), le champ `data`
 * est un objet cle/valeur :
 * <pre>
 * { "status": "success", "data": { "solde": 169525 } }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiEnvelope<T> {

    /** Statut logique de la reponse. Valeur courante : "success". */
    private final String status;

    /** Charge utile reelle de la reponse (liste, objet, scalaire sous forme de Map). */
    private final T data;

    /** Metadonnees optionnelles (pagination, compteurs, etc.). */
    private final Map<String, Object> meta;

    /** Horodatage serveur de la reponse (utile au client pour le cache/affichage). */
    private final LocalDateTime timestamp;

    private ApiEnvelope(String status, T data, Map<String, Object> meta) {
        this.status = status;
        this.data = data;
        this.meta = meta;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Construit une reponse sans metadonnees.
     *
     * @param data payload expose au client sous la cle {@code data}
     * @return enveloppe prete a serialiser
     */
    public static <T> ApiEnvelope<T> success(T data) {
        return new ApiEnvelope<>("success", data, null);
    }

    /**
     * Construit une reponse avec metadonnees (pagination, compteurs, etc.).
     *
     * @param data payload principal
     * @param meta metadonnees exposees sous la cle {@code meta}
     */
    public static <T> ApiEnvelope<T> success(T data, Map<String, Object> meta) {
        return new ApiEnvelope<>("success", data, meta);
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
