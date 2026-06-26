package com.soutra.microfinance.dto.request.client;

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
public class CreationCollaborateurRequestDTO {

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

    @Size(max = 80, message = "Le numero de piece d'identite ne doit pas depasser 80 caracteres")
    private String numeroPieceIdentite;

    @NotBlank(message = "Le code agence est obligatoire")
    private String codeAgence;

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 100, message = "Le login ne doit pas depasser 100 caracteres")
    private String login;

    @NotBlank(message = "Le role est obligatoire")
    private String role;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,100}$",
            message = "Le mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractere special"
    )
    private String motDePasseBrut;

    private Boolean secondFacteurActive;
}
