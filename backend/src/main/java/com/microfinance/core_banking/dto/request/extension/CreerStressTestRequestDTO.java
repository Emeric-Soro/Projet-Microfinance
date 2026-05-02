package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerStressTestRequestDTO {
    @NotBlank(message = "Le code scenario est obligatoire")
    @Size(max = 20)
    private String codeScenario;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @Size(max = 1000)
    private String description;

    private Map<String, Object> parametres;

    private String dateExecution;
}
