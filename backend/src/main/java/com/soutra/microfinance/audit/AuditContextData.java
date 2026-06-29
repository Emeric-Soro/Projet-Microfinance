package com.soutra.microfinance.audit;

import lombok.Getter;
import lombok.Setter;

/**
 * Données contextuelles d'audit passées via ThreadLocal.
 * Permet aux services métier de fournir id_entite, details_avant et details_apres
 * à l'AuditLogAspect sans modifier la signature des méthodes annotées @AuditLog.
 */
@Getter
@Setter
public class AuditContextData {

    /** Identifiant de l'entité modifiée (idClient, idCredit, numCompte…). */
    private String idEntite;

    /** Représentation JSON des valeurs AVANT modification. */
    private String detailsAvant;

    /** Représentation JSON des valeurs APRÈS modification. */
    private String detailsApres;

    /** Nom de l'action d'audit surchargé dynamiquement. */
    private String action;
}
