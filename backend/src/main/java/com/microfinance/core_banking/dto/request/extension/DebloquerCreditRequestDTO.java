package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DebloquerCreditRequestDTO {
    private Long idDemande;

    private BigDecimal montantAccorde;

    @NotBlank(message = "Le numero de compte destination est obligatoire")
    @Size(max = 30)
    private String numCompteDestination;

    private Long idUtilisateurOperateur;

    @Size(max = 20)
    private String statut;
}
