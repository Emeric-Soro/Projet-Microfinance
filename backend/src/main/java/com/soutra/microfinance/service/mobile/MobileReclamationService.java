package com.soutra.microfinance.service.mobile;

import com.soutra.microfinance.dto.request.mobile.MobileReclamationRequestDTO;
import com.soutra.microfinance.entity.conformite.Reclamation;
import com.soutra.microfinance.repository.conformite.ReclamationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MobileReclamationService {

    private final ReclamationRepository reclamationRepository;

    public MobileReclamationService(ReclamationRepository reclamationRepository) {
        this.reclamationRepository = reclamationRepository;
    }

    @Transactional(readOnly = true)
    public List<Reclamation> listerReclamations(Long idClient) {
        return reclamationRepository.findByIdClient(idClient);
    }

    @Transactional(readOnly = true)
    public Reclamation consulterReclamation(Long idReclamation, Long idClient) {
        Reclamation reclamation = reclamationRepository.findById(idReclamation)
                .orElseThrow(() -> new IllegalArgumentException("Reclamation introuvable"));
        if (!reclamation.getIdClient().equals(idClient)) {
            throw new IllegalArgumentException("Reclamation introuvable");
        }
        return reclamation;
    }

    @Transactional
    public Reclamation creerReclamation(Long idClient, String login, MobileReclamationRequestDTO requestDTO) {
        Reclamation reclamation = new Reclamation();
        reclamation.setReference("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reclamation.setIdClient(idClient);
        reclamation.setTypeReclamation(requestDTO.getTypeReclamation());
        reclamation.setDescription(requestDTO.getDescription());
        reclamation.setStatut("NOUVEAU");
        reclamation.setPriorite("NORMALE");
        reclamation.setDateCreation(LocalDateTime.now());
        reclamation.setCreePar(login);
        return reclamationRepository.save(reclamation);
    }
}
