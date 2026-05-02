package com.microfinance.core_banking.dto.response.compte;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'une carte Visa associée à un compte")
public class CarteVisaResponseDTO {

    @Schema(description = "Numéro de carte masqué pour des raisons de sécurité", example = "**** **** **** 1234")
    private String numeroCarteMasque;

    @Schema(description = "Date d'expiration de la carte", example = "2028-12-31")
    private LocalDate dateExpiration;

    @Schema(description = "Statut de la carte", example = "ACTIF")
    private String statut;
}
