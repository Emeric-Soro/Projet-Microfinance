package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerMutationRequestDTO {
    @NotNull(message = "L'id de l'employe est obligatoire")
    private Long idEmploye;

    private Long idAgenceSource;

    @NotNull(message = "L'id de l'agence destination est obligatoire")
    private Long idAgenceDestination;

    private LocalDate dateMutation;

    @Size(max = 500)
    private String motif;

    @Size(max = 20)
    private String statut;
}
