package com.soutra.microfinance.dto.request.mobile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobilePinRequestDTO {

    @NotBlank(message = "Le code PIN est obligatoire")
    @Size(min = 4, max = 6, message = "Le code PIN doit contenir entre 4 et 6 caracteres")
    private String codePin;
}
