package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.TypePieceIdentite;
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
public class CreationClientRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit etre dans le passe")
    private LocalDate dateNaissance;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    private String email;

    @NotBlank(message = "Le telephone est obligatoire")
    @Size(max = 30, message = "Le telephone ne doit pas depasser 30 caracteres")
    @Pattern(
            regexp = "^\\+?[0-9][0-9\\s-]{7,29}$",
            message = "Le telephone doit contenir uniquement des chiffres, espaces ou tirets"
    )
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres")
    private String adresse;

    @Size(max = 120, message = "La profession ne doit pas depasser 120 caracteres")
    private String profession;

    @Size(max = 150, message = "L'employeur ne doit pas depasser 150 caracteres")
    private String employeur;

    private TypePieceIdentite typePieceIdentite;

    @Size(max = 80, message = "Le numero de piece d'identite ne doit pas depasser 80 caracteres")
    private String numeroPieceIdentite;

    private LocalDate dateExpirationPieceIdentite;

    @Size(max = 255, message = "L'URL de la photo d'identite ne doit pas depasser 255 caracteres")
    private String photoIdentiteUrl;

    @Size(max = 255, message = "L'URL du justificatif de domicile ne doit pas depasser 255 caracteres")
    private String justificatifDomicileUrl;

    @Size(max = 255, message = "L'URL du justificatif de revenus ne doit pas depasser 255 caracteres")
    private String justificatifRevenusUrl;

    @Size(max = 80, message = "La nationalite ne doit pas depasser 80 caracteres")
    private String paysNationalite;

    @Size(max = 80, message = "Le pays de residence ne doit pas depasser 80 caracteres")
    private String paysResidence;

    private Boolean pep;
}
