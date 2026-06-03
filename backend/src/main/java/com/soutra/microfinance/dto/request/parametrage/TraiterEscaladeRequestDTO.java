package com.soutra.microfinance.dto.request.parametrage;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraiterEscaladeRequestDTO {

    @NotBlank(message = "L'action est obligatoire")
    private String action;

    private String commentaire;
}
