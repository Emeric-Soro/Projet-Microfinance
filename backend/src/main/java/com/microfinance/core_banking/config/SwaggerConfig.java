package com.microfinance.core_banking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MicroFin Core Banking API",
                version = "1.0.0",
                description = "API REST sécurisée du système bancaire central MicroFin. " +
                        "Cette API couvre la gestion complète des opérations bancaires : clients, " +
                        "comptes, transactions, crédits, épargne, comptabilité, trésorerie, conformité " +
                        "et administration technique. Toutes les opérations financières critiques " +
                        "suivent le principe de double validation Maker-Checker et la comptabilité " +
                        "en partie double.",
                contact = @Contact(
                        name = "Support Technique MicroFin",
                        email = "support@microfin.ci",
                        url = "https://microfin.ci"
                ),
                license = @License(
                        name = "Propriétaire - MicroFin",
                        url = "https://microfin.ci/license"
                )
        ),
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Tag(name = "Core Banking", description = "API centrale du système bancaire MicroFin")
public class SwaggerConfig {
}