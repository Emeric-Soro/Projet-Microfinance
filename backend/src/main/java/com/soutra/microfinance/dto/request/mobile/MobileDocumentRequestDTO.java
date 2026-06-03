package com.soutra.microfinance.dto.request.mobile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileDocumentRequestDTO {

    @NotBlank(message = "Le type de document est obligatoire")
    private String typeDocument;

    @NotBlank(message = "Le contenu en base64 est obligatoire")
    private String contenuBase64;

    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String nomFichier;
}
