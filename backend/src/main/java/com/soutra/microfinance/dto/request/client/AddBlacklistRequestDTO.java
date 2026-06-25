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
public class AddBlacklistRequestDTO {

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @NotBlank(message = "Les details du motif sont obligatoires")
    private String details;
}
