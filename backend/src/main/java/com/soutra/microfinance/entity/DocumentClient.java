package com.soutra.microfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "soutra_document_client", indexes = {
        @Index(name = "idx_doc_client", columnList = "id_client"),
        @Index(name = "idx_doc_date", columnList = "date_upload")
})
@Getter
@Setter
@NoArgsConstructor
public class DocumentClient extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doc")
    private Long idDoc;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "type_mime", nullable = false, length = 100)
    private String typeMime;

    @Column(name = "taille_octets", nullable = false)
    private Long tailleOctets;

    @Column(name = "chemin_stockage", nullable = false, length = 500)
    private String cheminStockage;

    @Column(name = "categorie", length = 50)
    private String categorie;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @Column(name = "date_upload", nullable = false)
    private LocalDateTime dateUpload;
}
