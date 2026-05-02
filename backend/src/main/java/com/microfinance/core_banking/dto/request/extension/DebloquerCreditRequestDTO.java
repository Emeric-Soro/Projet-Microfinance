package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de déblocage de crédit")
public class DebloquerCreditRequestDTO {
    @Schema(description = "Identifiant de la demande de crédit (optionnel)", example = "1")
    private Long idDemande;

    @Schema(description = "Montant accordé (optionnel)", example = "1000000.00")
    private BigDecimal montantAccorde;

    @NotBlank(message = "Le numero de compte destination est obligatoire")
    @Size(max = 30)
    @Schema(description = "Numéro de compte destination (obligatoire, max 30 caractères)", example = "SN000012345678901")
    private String numCompteDestination;

    @Schema(description = "Identifiant de l'utilisateur opérateur (optionnel)", example = "1")
    private Long idUtilisateurOperateur;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "DEBLOQUE")
    private String statut;
}
