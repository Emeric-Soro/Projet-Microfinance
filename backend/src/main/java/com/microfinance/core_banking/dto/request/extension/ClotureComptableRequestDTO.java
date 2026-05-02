package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClotureComptableRequestDTO {
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Size(max = 20)
    private String typeCloture;

    @Size(max = 20)
    private String statut;

    @Size(max = 1000)
    private String commentaire;
}
