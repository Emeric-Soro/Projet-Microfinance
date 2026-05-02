package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConsulterBicRequestDTO {
    @NotBlank(message = "Le code BIC est obligatoire")
    @Size(max = 20)
    private String codeBic;

    @Size(max = 50)
    private String codeBanque;
}
