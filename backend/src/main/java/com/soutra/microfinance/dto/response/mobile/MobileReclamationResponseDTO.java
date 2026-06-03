package com.soutra.microfinance.dto.response.mobile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileReclamationResponseDTO {

    private Long id;
    private String reference;
    private String typeReclamation;
    private String description;
    private String statut;
    private String priorite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateTraitement;
    private String motifCloture;
}
