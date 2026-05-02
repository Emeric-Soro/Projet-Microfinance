package com.microfinance.core_banking.dto.response.client;

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
@Schema(description = "Informations détaillées d'un client")
public class ClientResponseDTO {

    @Schema(description = "Identifiant unique du client", example = "1")
    private Long idClient;

    @Schema(description = "Code client unique", example = "CLI-20260401-0001")
    private String codeClient;

    @Schema(description = "Nom complet du client", example = "John Doe")
    private String nomComplet;

    @Schema(description = "Adresse email du client", example = "email@example.com")
    private String email;

    @Schema(description = "Numéro de téléphone du client", example = "+221771234567")
    private String telephone;

    @Schema(description = "Statut du client", example = "ACTIF")
    private String statut;

    @Schema(description = "Date de naissance du client", example = "1990-01-15")
    private LocalDate dateNaissance;

    @Schema(description = "Adresse postale du client", example = "Dakar, Sénégal")
    private String adresse;

    @Schema(description = "Profession du client", example = "Ingénieur")
    private String profession;

    @Schema(description = "Type de pièce d'identité", example = "CNI")
    private String typePieceIdentite;

    @Schema(description = "Numéro de pièce d'identité masqué", example = "****1234")
    private String numeroPieceIdentiteMasque;

    @Schema(description = "Date d'expiration de la pièce d'identité", example = "2030-12-31")
    private LocalDate dateExpirationPieceIdentite;

    @Schema(description = "URL de la photo d'identité", example = "/uploads/photos/photo_1.jpg")
    private String photoIdentiteUrl;

    @Schema(description = "URL du justificatif de domicile", example = "/uploads/justificatifs/domicile_1.pdf")
    private String justificatifDomicileUrl;

    @Schema(description = "URL du justificatif de revenus", example = "/uploads/justificatifs/revenus_1.pdf")
    private String justificatifRevenusUrl;

    @Schema(description = "Pays de nationalité", example = "Sénégal")
    private String paysNationalite;

    @Schema(description = "Pays de résidence", example = "Sénégal")
    private String paysResidence;

    @Schema(description = "Indique si le client est une personne politiquement exposée", example = "false")
    private Boolean pep;

    @Schema(description = "Niveau de risque du client", example = "FAIBLE")
    private String niveauRisque;

    @Schema(description = "Statut KYC du client", example = "COMPLET")
    private String statutKyc;

    @Schema(description = "Indique si le dossier KYC est complet", example = "true")
    private Boolean kycComplet;
}
