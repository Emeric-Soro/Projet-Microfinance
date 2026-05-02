package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un scénario de stress test")
public class CreerStressTestRequestDTO {
    @NotBlank(message = "Le code scenario est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du scénario (obligatoire, max 20 caractères)", example = "SCEN-001")
    private String codeScenario;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du scénario (obligatoire, max 100 caractères)", example = "Scénario crise économique")
    private String libelle;

    @Size(max = 1000)
    @Schema(description = "Description (optionnel, max 1000 caractères)", example = "Simulation d'une crise économique sévère")
    private String description;

    @Schema(description = "Paramètres du scénario (optionnel)")
    private Map<String, Object> parametres;

    @Schema(description = "Date d'exécution (optionnel)", example = "2026-04-01")
    private String dateExecution;
}
