package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametreSystemeRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String code;

    @NotBlank
    @Size(max = 500)
    private String valeur;
}
