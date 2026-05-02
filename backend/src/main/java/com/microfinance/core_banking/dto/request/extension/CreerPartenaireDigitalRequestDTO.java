package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un partenaire digital")
public class CreerPartenaireDigitalRequestDTO {
    @NotBlank(message = "Le code partenaire est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du partenaire (obligatoire, max 20 caractères)", example = "PART-001")
    private String codePartenaire;

    @NotBlank(message = "Le nom partenaire est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom du partenaire (obligatoire, max 100 caractères)", example = "Partenaire Digital SA")
    private String nomPartenaire;

    @NotBlank(message = "Le type service est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de service (obligatoire, max 50 caractères)", example = "API_PAIEMENT")
    private String typeService;

    @Size(max = 50)
    @Schema(description = "Contact (optionnel, max 50 caractères)", example = "+221771234567")
    private String contact;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
