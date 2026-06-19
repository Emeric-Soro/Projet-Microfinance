package com.soutra.microfinance.dto.request.operation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Requete de depot initial lors de l'ouverture d'un compte.
 * Contrairement au depot guichet classique, ce depot ne necessite pas
 * une caisse ouverte — il peut etre realise par l'agent commercial
 * ou le guichetier juste apres la creation du compte.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepotInitialRequestDTO {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant du depot initial doit etre strictement positif")
    private BigDecimal montant;

    /** Identifiant de l'utilisateur initiateur (guichetier ou agent commercial). */
    @NotNull(message = "L'identifiant de l'initiateur est obligatoire")
    private Long idInitiateur;
}
