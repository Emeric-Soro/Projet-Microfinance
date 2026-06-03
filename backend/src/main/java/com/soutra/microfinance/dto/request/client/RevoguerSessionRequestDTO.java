package com.soutra.microfinance.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevoguerSessionRequestDTO {

    @NotBlank(message = "L'identifiant de session est obligatoire")
    private String sessionId;
}
