package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.TypePieceIdentite;
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
public class MiseAJourKycClientRequestDTO {

    @NotBlank(message = "La profession est obligatoire")
    @Size(max = 120, message = "La profession ne doit pas depasser 120 caracteres")
    private String profession;

    @Size(max = 150, message = "L'employeur ne doit pas depasser 150 caracteres")
    private String employeur;

    @NotNull(message = "Le type de piece d'identite est obligatoire")
    private TypePieceIdentite typePieceIdentite;

    @NotBlank(message = "Le numero de piece d'identite est obligatoire")
    @Size(max = 80, message = "Le numero de piece d'identite ne doit pas depasser 80 caracteres")
    private String numeroPieceIdentite;

    @NotNull(message = "La date d'expiration de la piece d'identite est obligatoire")
    private LocalDate dateExpirationPieceIdentite;

    @NotBlank(message = "La photo d'identite est obligatoire")
    @Size(max = 255, message = "L'URL de la photo d'identite ne doit pas depasser 255 caracteres")
    private String photoIdentiteUrl;

    @NotBlank(message = "Le justificatif de domicile est obligatoire")
    @Size(max = 255, message = "L'URL du justificatif de domicile ne doit pas depasser 255 caracteres")
    private String justificatifDomicileUrl;

    @NotBlank(message = "Le justificatif de revenus est obligatoire")
    @Size(max = 255, message = "L'URL du justificatif de revenus ne doit pas depasser 255 caracteres")
    private String justificatifRevenusUrl;

    @NotBlank(message = "La nationalite est obligatoire")
    @Size(max = 80, message = "La nationalite ne doit pas depasser 80 caracteres")
    private String paysNationalite;

    @NotBlank(message = "Le pays de residence est obligatoire")
    @Size(max = 80, message = "Le pays de residence ne doit pas depasser 80 caracteres")
    private String paysResidence;

    @NotNull(message = "L'indicateur PEP est obligatoire")
    private Boolean pep;

    @PastOrPresent(message = "La date de soumission KYC ne peut pas etre dans le futur")
    private LocalDate dateSoumission;
}
