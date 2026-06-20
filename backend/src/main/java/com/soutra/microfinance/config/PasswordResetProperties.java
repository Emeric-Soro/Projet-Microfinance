package com.soutra.microfinance.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuration de la reinitialisation de mot de passe.
 * - tokenValidity : duree de validite d'un token avant expiration
 * - frontendBaseUrl : URL du front pour construire le lien de reinitialisation
 */
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.password-reset")
public class PasswordResetProperties {

    @NotNull
    private Duration tokenValidity = Duration.ofMinutes(30);

    @NotBlank
    private String frontendBaseUrl = "http://localhost:5173";

    @NotBlank
    private String resetPath = "/auth/reset";
}
