package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerCompteLiaisonRequestDTO {
    @NotNull(message = "L'id de l'agence source est obligatoire")
    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    private Long idAgenceDestination;

    @NotNull(message = "L'id du compte comptable est obligatoire")
    private Long idCompteComptable;

    @Size(max = 255)
    private String libelle;

    private Boolean actif;
}
