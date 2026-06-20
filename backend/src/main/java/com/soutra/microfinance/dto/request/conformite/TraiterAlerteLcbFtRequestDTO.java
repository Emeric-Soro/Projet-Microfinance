package com.soutra.microfinance.dto.request.conformite;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraiterAlerteLcbFtRequestDTO {

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;

    @NotBlank(message = "Le traitePar est obligatoire")
    private String traitePar;

    private String actions;
}
