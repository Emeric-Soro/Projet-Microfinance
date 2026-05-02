package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerActionValidationRequestDTO {
    @NotBlank(message = "Le type action est obligatoire")
    @Size(max = 50)
    private String typeAction;

    @NotBlank(message = "La ressource est obligatoire")
    @Size(max = 50)
    private String ressource;

    @Size(max = 50)
    private String referenceRessource;

    private Map<String, Object> donnees;

    @Size(max = 500)
    private String commentaire;
}
