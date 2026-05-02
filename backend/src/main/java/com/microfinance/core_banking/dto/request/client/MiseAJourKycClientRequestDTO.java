package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.TypePieceIdentite;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
@Schema(description = "Requête de mise à jour des informations KYC d'un client")
public class MiseAJourKycClientRequestDTO {

    @NotBlank(message = "La profession est obligatoire")
    @Size(max = 120, message = "La profession ne doit pas depasser 120 caracteres")
    @Schema(description = "Profession (obligatoire, max 120 caractères)", example = "Ingénieur")
    private String profession;

    @Size(max = 150, message = "L'employeur ne doit pas depasser 150 caracteres")
    @Schema(description = "Employeur (optionnel, max 150 caractères)", example = "Entreprise SA")
    private String employeur;

    @NotNull(message = "Le type de piece d'identite est obligatoire")
    @Schema(description = "Type de pièce d'identité (obligatoire)", example = "CNI")
    private TypePieceIdentite typePieceIdentite;

    @NotBlank(message = "Le numero de piece d'identite est obligatoire")
    @Size(max = 80, message = "Le numero de piece d'identite ne doit pas depasser 80 caracteres")
    @Schema(description = "Numéro de pièce d'identité (obligatoire, max 80 caractères)", example = "AB123456")
    private String numeroPieceIdentite;

    @NotNull(message = "La date d'expiration de la piece d'identite est obligatoire")
    @Schema(description = "Date d'expiration de la pièce d'identité (obligatoire)", example = "2030-01-15")
    private LocalDate dateExpirationPieceIdentite;

    @NotBlank(message = "La photo d'identite est obligatoire")
    @Size(max = 255, message = "L'URL de la photo d'identite ne doit pas depasser 255 caracteres")
    @Schema(description = "URL de la photo d'identité (obligatoire, max 255 caractères)", example = "/uploads/photos/photo_001.jpg")
    private String photoIdentiteUrl;

    @NotBlank(message = "Le justificatif de domicile est obligatoire")
    @Size(max = 255, message = "L'URL du justificatif de domicile ne doit pas depasser 255 caracteres")
    @Schema(description = "URL du justificatif de domicile (obligatoire, max 255 caractères)", example = "/uploads/justificatifs/domicile_001.pdf")
    private String justificatifDomicileUrl;

    @NotBlank(message = "Le justificatif de revenus est obligatoire")
    @Size(max = 255, message = "L'URL du justificatif de revenus ne doit pas depasser 255 caracteres")
    @Schema(description = "URL du justificatif de revenus (obligatoire, max 255 caractères)", example = "/uploads/justificatifs/revenus_001.pdf")
    private String justificatifRevenusUrl;

    @NotBlank(message = "La nationalite est obligatoire")
    @Size(max = 80, message = "La nationalite ne doit pas depasser 80 caracteres")
    @Schema(description = "Pays de nationalité (obligatoire, max 80 caractères)", example = "Sénégal")
    private String paysNationalite;

    @NotBlank(message = "Le pays de residence est obligatoire")
    @Size(max = 80, message = "Le pays de residence ne doit pas depasser 80 caracteres")
    @Schema(description = "Pays de résidence (obligatoire, max 80 caractères)", example = "Sénégal")
    private String paysResidence;

    @NotNull(message = "L'indicateur PEP est obligatoire")
    @Schema(description = "Indicateur Personne Politiquement Exposée (obligatoire)", example = "false")
    private Boolean pep;

    @PastOrPresent(message = "La date de soumission KYC ne peut pas etre dans le futur")
    @Schema(description = "Date de soumission KYC (optionnel, ne peut pas être dans le futur)", example = "2026-04-01")
    private LocalDate dateSoumission;
}
