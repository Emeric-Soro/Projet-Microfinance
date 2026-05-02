package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de redémarrage d'une exécution EOD")
public class RestartEodRequestDTO {

    @NotNull(message = "L'identifiant de l'execution est obligatoire")
    @Schema(description = "Identifiant de l'exécution EOD à redémarrer (obligatoire)", example = "1")
    private Long executionId;
}
