package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.TypePieceIdentite;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
@Schema(description = "Requête de création d'un nouveau client")
public class CreationClientRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    @Schema(description = "Nom du client (obligatoire, max 100 caractères)", example = "Dupont")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    @Schema(description = "Prénom du client (obligatoire, max 100 caractères)", example = "Jean")
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit etre dans le passe")
    @Schema(description = "Date de naissance (obligatoire, doit être dans le passé)", example = "1990-01-15")
    private LocalDate dateNaissance;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    @Schema(description = "Adresse email (obligatoire, max 150 caractères)", example = "jean.dupont@email.com")
    private String email;

    @NotBlank(message = "Le telephone est obligatoire")
    @Size(max = 30, message = "Le telephone ne doit pas depasser 30 caracteres")
    @Pattern(
            regexp = "^\\+?[0-9][0-9\\s-]{7,29}$",
            message = "Le telephone doit contenir uniquement des chiffres, espaces ou tirets"
    )
    @Schema(description = "Numéro de téléphone (obligatoire, format: chiffres, espaces ou tirets)", example = "+221771234567")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres")
    @Schema(description = "Adresse physique (obligatoire, max 255 caractères)", example = "123 Rue Principale, Dakar")
    private String adresse;

    @Size(max = 120, message = "La profession ne doit pas depasser 120 caracteres")
    @Schema(description = "Profession (optionnel, max 120 caractères)", example = "Ingénieur")
    private String profession;

    @Size(max = 150, message = "L'employeur ne doit pas depasser 150 caracteres")
    @Schema(description = "Employeur (optionnel, max 150 caractères)", example = "Entreprise SA")
    private String employeur;

    @Schema(description = "Type de pièce d'identité (optionnel)", example = "CNI")
    private TypePieceIdentite typePieceIdentite;

    @Size(max = 80, message = "Le numero de piece d'identite ne doit pas depasser 80 caracteres")
    @Schema(description = "Numéro de pièce d'identité (optionnel, max 80 caractères)", example = "AB123456")
    private String numeroPieceIdentite;

    @Schema(description = "Date d'expiration de la pièce d'identité (optionnel)", example = "2030-01-15")
    private LocalDate dateExpirationPieceIdentite;

    @Size(max = 255, message = "L'URL de la photo d'identite ne doit pas depasser 255 caracteres")
    @Schema(description = "URL de la photo d'identité (optionnel, max 255 caractères)", example = "/uploads/photos/photo_001.jpg")
    private String photoIdentiteUrl;

    @Size(max = 255, message = "L'URL du justificatif de domicile ne doit pas depasser 255 caracteres")
    @Schema(description = "URL du justificatif de domicile (optionnel, max 255 caractères)", example = "/uploads/justificatifs/domicile_001.pdf")
    private String justificatifDomicileUrl;

    @Size(max = 255, message = "L'URL du justificatif de revenus ne doit pas depasser 255 caracteres")
    @Schema(description = "URL du justificatif de revenus (optionnel, max 255 caractères)", example = "/uploads/justificatifs/revenus_001.pdf")
    private String justificatifRevenusUrl;

    @Size(max = 80, message = "La nationalite ne doit pas depasser 80 caracteres")
    @Schema(description = "Pays de nationalité (optionnel, max 80 caractères)", example = "Sénégal")
    private String paysNationalite;

    @Size(max = 80, message = "Le pays de residence ne doit pas depasser 80 caracteres")
    @Schema(description = "Pays de résidence (optionnel, max 80 caractères)", example = "Sénégal")
    private String paysResidence;

    @Schema(description = "Indicateur Personne Politiquement Exposée (PPE) (optionnel)", example = "false")
    private Boolean pep;
}
