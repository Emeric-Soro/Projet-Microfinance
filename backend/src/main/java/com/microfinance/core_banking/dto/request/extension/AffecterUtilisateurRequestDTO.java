package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AffecterUtilisateurRequestDTO {
    @NotNull(message = "L'id de l'utilisateur est obligatoire")
    private Long idUtilisateur;

    @NotNull(message = "L'id de l'agence est obligatoire")
    private Long idAgence;

    @Size(max = 50)
    private String roleOperatoire;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private Boolean actif;
}
