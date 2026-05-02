package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenererBulletinPaieRequestDTO {
    @NotNull(message = "L'id employe est obligatoire")
    private Long idEmploye;

    @NotBlank(message = "La periode paie est obligatoire")
    private String periodePaie;

    private String dateEmission;
}
