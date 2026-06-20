package com.soutra.microfinance.service.client;

import com.soutra.microfinance.entity.DocumentClient;
import com.soutra.microfinance.repository.client.DocumentClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentClientServiceImpl implements DocumentClientService {

    private final DocumentClientRepository documentClientRepository;

    @Value("${app.file.storage-path:./uploads/documents}")
    private String storagePath;

    private static final long MAX_TAILLE_OCTETS = 5 * 1024 * 1024; // 5 Mo
    private static final Set<String> TYPES_MIME_AUTORISES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf"
    );

    @Override
    @Transactional
    public DocumentClient uploadDocument(Long idClient, MultipartFile fichier, String categorie, Long uploadedBy) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est obligatoire");
        }
        if (fichier.getSize() > MAX_TAILLE_OCTETS) {
            throw new IllegalArgumentException("Le fichier depasse la taille maximale de 5 Mo");
        }
        String typeMime = fichier.getContentType();
        if (typeMime == null || !TYPES_MIME_AUTORISES.contains(typeMime.toLowerCase())) {
            throw new IllegalArgumentException("Type de fichier non autorise. Seuls les images (JPEG, PNG, GIF, WebP) et les PDF sont acceptes.");
        }

        try {
            Path dossierStockage = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(dossierStockage);

            String nomUnique = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
            Path cheminFichier = dossierStockage.resolve(nomUnique);
            Files.copy(fichier.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

            DocumentClient doc = new DocumentClient();
            doc.setIdClient(idClient);
            doc.setNomFichier(nomUnique);
            doc.setTypeMime(typeMime.toLowerCase());
            doc.setTailleOctets(fichier.getSize());
            doc.setCheminStockage(cheminFichier.toString());
            doc.setCategorie(categorie);
            doc.setUploadedBy(uploadedBy);
            doc.setDateUpload(LocalDateTime.now());

            return documentClientRepository.save(doc);
        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de l'enregistrement du fichier : " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentClient> listerDocuments(Long idClient, Pageable pageable) {
        return documentClientRepository.findByIdClientOrderByDateUploadDesc(idClient, pageable);
    }
}
