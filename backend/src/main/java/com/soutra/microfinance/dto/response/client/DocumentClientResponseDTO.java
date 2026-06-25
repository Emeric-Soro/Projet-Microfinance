package com.soutra.microfinance.dto.response.client;

import com.soutra.microfinance.entity.DocumentClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentClientResponseDTO {

    private Long idDoc;
    private Long idClient;
    private String nomFichier;
    private String typeMime;
    private Long tailleOctets;
    private String categorie;
    private LocalDateTime dateUpload;
    private Long uploadedBy;
    private Long id;
    private String nom;
    private String typeDocument;
    private String chemin;

    public static DocumentClientResponseDTO fromEntity(DocumentClient doc) {
        if (doc == null) {
            return null;
        }
        return DocumentClientResponseDTO.builder()
                .idDoc(doc.getIdDoc())
                .id(doc.getIdDoc())
                .idClient(doc.getIdClient())
                .nomFichier(doc.getNomFichier())
                .nom(doc.getNomFichier())
                .typeMime(doc.getTypeMime())
                .tailleOctets(doc.getTailleOctets())
                .categorie(doc.getCategorie())
                .typeDocument(doc.getCategorie())
                .dateUpload(doc.getDateUpload())
                .uploadedBy(doc.getUploadedBy())
                .chemin(doc.getCheminStockage())
                .build();
    }
}
