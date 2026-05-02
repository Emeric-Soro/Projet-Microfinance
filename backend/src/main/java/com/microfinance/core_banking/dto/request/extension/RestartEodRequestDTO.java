package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RestartEodRequestDTO {

    @NotNull(message = "L'identifiant de l'execution est obligatoire")
    private Long executionId;
}
