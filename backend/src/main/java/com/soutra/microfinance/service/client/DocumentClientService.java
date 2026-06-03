package com.soutra.microfinance.service.client;

import com.soutra.microfinance.entity.DocumentClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentClientService {

    DocumentClient uploadDocument(Long idClient, MultipartFile fichier, String categorie, Long uploadedBy);

    Page<DocumentClient> listerDocuments(Long idClient, Pageable pageable);
}
