package com.soutra.microfinance.dto.response.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaisseResponseDTO {

    private Long id;
    private String codeGuichet;
    private BigDecimal fondInitial;
    private BigDecimal soldeActuel;
    private BigDecimal ecartFermeture;
    private LocalDateTime dateOuverture;
    private LocalDateTime dateFermeture;
    private String statut;
    private Long agenceId;
    private String agenceNom;
    private Long guichetierId;
    private String guichetierNom;
    private LocalDateTime createdAt;
}
