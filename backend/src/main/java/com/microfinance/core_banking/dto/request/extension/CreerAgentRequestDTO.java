package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerAgentRequestDTO {
    @NotBlank(message = "Le code agent est obligatoire")
    private String codeAgent;

    @NotBlank(message = "Le nom agent est obligatoire")
    private String nomAgent;

    private String telephone;

    private String adresse;

    @NotBlank(message = "Le type agent est obligatoire")
    private String typeAgent;

    @NotNull(message = "L'id de l'agence de rattachement est obligatoire")
    private Long idAgenceRattachement;
}
