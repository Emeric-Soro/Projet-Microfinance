package com.microfinance.core_banking.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'un utilisateur (compte en ligne)")
public class CreationUtilisateurRequestDTO {

    @NotBlank(message = "Le code client est obligatoire")
    @Size(max = 50, message = "Le code client ne doit pas depasser 50 caracteres")
    @Pattern(
            regexp = "^CLI-\\d{8}-\\d{4}$",
            message = "Le code client doit respecter le format CLI-YYYYMMDD-XXXX"
    )
    @Schema(description = "Code client (obligatoire, format: CLI-YYYYMMDD-XXXX)", example = "CLI-20260401-0001")
    private String codeClient;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    @Schema(description = "Adresse email (obligatoire, max 150 caractères)", example = "jean.dupont@email.com")
    private String email;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit etre dans le passe")
    @Schema(description = "Date de naissance (obligatoire, doit être dans le passé)", example = "1990-01-15")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,100}$",
            message = "Le mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractere special"
    )
    @Schema(description = "Mot de passe en clair (obligatoire, 8-100 caractères, doit contenir majuscule, minuscule, chiffre et caractère spécial)", example = "********")
    private String motDePasseBrut;
}
