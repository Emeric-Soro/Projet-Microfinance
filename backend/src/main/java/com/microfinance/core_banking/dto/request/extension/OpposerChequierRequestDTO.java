package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OpposerChequierRequestDTO {
    @NotBlank(message = "Le motif d'opposition est obligatoire")
    private String motifOpposition;
}
