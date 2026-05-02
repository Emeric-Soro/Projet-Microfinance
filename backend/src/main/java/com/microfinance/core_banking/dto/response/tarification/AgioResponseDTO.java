package com.microfinance.core_banking.dto.response.tarification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations sur les frais et agios appliqués")
public class AgioResponseDTO {

    @Schema(description = "Type de frais appliqué", example = "FRAIS_TENUE_COMPTE")
    private String typeFrais;

    @Schema(description = "Montant des frais en XOF", example = "1500.00")
    private BigDecimal montant;

    @Schema(description = "Date de calcul des frais", example = "2026-04-01")
    private LocalDate dateCalcul;

    @Schema(description = "Indique si les frais ont été prélevés", example = "true")
    private Boolean estPreleve;
}
