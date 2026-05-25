package com.soutra.microfinance.dto.comptabilite;

import java.math.BigDecimal;

// DTO de saisie d'une ligne d'ecriture comptable pour le moteur comptable.
public record LigneEcritureDTO(
        // Numero du compte du grand livre concerne.
        String numeroCompte,
        // Montant a debiter (0 si credit seul).
        BigDecimal debit,
        // Montant a crediter (0 si debit seul).
        BigDecimal credit
) {}
