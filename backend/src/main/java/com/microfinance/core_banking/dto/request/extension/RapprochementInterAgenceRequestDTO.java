package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RapprochementInterAgenceRequestDTO {
    @NotNull(message = "L'id de l'agence source est obligatoire")
    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    private Long idAgenceDestination;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    private BigDecimal montantDebit;

    private BigDecimal montantCredit;

    private BigDecimal ecart;

    @Size(max = 20)
    private String statut;

    @Size(max = 500)
    private String commentaire;

    private Long idValidateur;
}
