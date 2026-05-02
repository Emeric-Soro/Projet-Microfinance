package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SessionCaisseResponseDTO {
    private Long idSessionCaisse;
    private Long idCaisse;
    private Long idUtilisateur;
    private LocalDateTime dateOuverture;
    private LocalDateTime dateFermeture;
    private BigDecimal soldeOuverture;
    private BigDecimal soldeTheoriqueFermeture;
    private BigDecimal soldePhysiqueFermeture;
    private BigDecimal ecart;
    private String statut;
}
