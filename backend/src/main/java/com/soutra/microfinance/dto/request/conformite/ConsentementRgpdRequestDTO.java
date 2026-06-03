package com.soutra.microfinance.dto.request.conformite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsentementRgpdRequestDTO {

    private Long idClient;

    @NotBlank(message = "La finalité est obligatoire")
    private String finalite;

    @NotNull(message = "Le champ consenti est obligatoire")
    private Boolean consenti;

    private String adresseIp;
}
