package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de re-scannage d'un client")
public class RescannerClientRequestDTO {
    @NotBlank(message = "Le type piece est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de pièce à rescanner (obligatoire, max 50 caractères)", example = "CNI")
    private String typePiece;

    @Size(max = 500)
    @Schema(description = "Motif du re-scannage (optionnel, max 500 caractères)", example = "Document expiré")
    private String motifRescan;
}
