package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RescannerClientRequestDTO {
    @NotBlank(message = "Le type piece est obligatoire")
    @Size(max = 50)
    private String typePiece;

    @Size(max = 500)
    private String motifRescan;
}
