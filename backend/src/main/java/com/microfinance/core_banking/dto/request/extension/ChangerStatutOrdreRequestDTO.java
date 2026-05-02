package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangerStatutOrdreRequestDTO {
    @NotBlank(message = "Le nouveau statut est obligatoire")
    @Size(max = 20)
    private String nouveauStatut;

    @Size(max = 500)
    private String commentaire;

    @NotNull(message = "L'id validateur est obligatoire")
    private Long idValidateur;
}
