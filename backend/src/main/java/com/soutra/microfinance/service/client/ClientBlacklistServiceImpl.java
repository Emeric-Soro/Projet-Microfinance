package com.soutra.microfinance.service.client;

import com.soutra.microfinance.constant.AppConstants;
import com.soutra.microfinance.dto.request.client.AddBlacklistRequestDTO;
import com.soutra.microfinance.dto.request.client.RemoveBlacklistRequestDTO;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.ClientBlacklist;
import com.soutra.microfinance.entity.ClientBlacklistHistory;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.repository.client.ClientBlacklistHistoryRepository;
import com.soutra.microfinance.repository.client.ClientBlacklistRepository;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.StatutClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientBlacklistServiceImpl implements ClientBlacklistService {

    private final ClientRepository clientRepository;
    private final StatutClientRepository statutClientRepository;
    private final ClientBlacklistRepository clientBlacklistRepository;
    private final ClientBlacklistHistoryRepository clientBlacklistHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ClientBlacklist> listerBlacklist(Pageable pageable) {
        return clientBlacklistRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientBlacklistHistory> obtenirHistorique(Pageable pageable) {
        return clientBlacklistHistoryRepository.findAllByOrderByDateActionDesc(pageable);
    }

    @Override
    @Transactional
    public ClientBlacklist ajouterABlacklist(Long idClient, AddBlacklistRequestDTO requestDTO, String operateur) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        if (clientBlacklistRepository.existsByClient_IdClient(idClient)) {
            throw new IllegalStateException("Ce client est deja dans la blacklist");
        }

        // Mettre a jour le statut du client a BLOQUE
        StatutClient statutBloque = statutClientRepository.findByLibelleStatutIgnoreCase(AppConstants.STATUT_CLIENT_BLOQUE)
                .orElseThrow(() -> new IllegalStateException("Statut BLOQUE introuvable"));
        client.setStatutClient(statutBloque);
        clientRepository.save(client);

        // Enregistrer dans la blacklist
        ClientBlacklist bl = new ClientBlacklist();
        bl.setClient(client);
        bl.setMotif(requestDTO.getMotif());
        bl.setDetails(requestDTO.getDetails());
        bl.setDateBlacklist(LocalDateTime.now());
        bl.setBlacklistePar(operateur);
        ClientBlacklist blSaved = clientBlacklistRepository.save(bl);

        // Enregistrer l'historique
        ClientBlacklistHistory hist = new ClientBlacklistHistory();
        hist.setIdClient(idClient);
        hist.setAction("AJOUT");
        hist.setClientNom(client.getNom());
        hist.setClientPrenom(client.getPrenom());
        hist.setNumeroClient(client.getCodeClient());
        hist.setMotif(requestDTO.getMotif());
        hist.setDetails(requestDTO.getDetails());
        hist.setDateAction(LocalDateTime.now());
        hist.setOperateur(operateur);
        clientBlacklistHistoryRepository.save(hist);

        return blSaved;
    }

    @Override
    @Transactional
    public void retirerDeBlacklist(Long idClient, RemoveBlacklistRequestDTO requestDTO, String operateur) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        ClientBlacklist bl = clientBlacklistRepository.findByClient_IdClient(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Ce client n'est pas dans la blacklist"));

        // Restaurer le statut du client a ACTIF
        StatutClient statutActif = statutClientRepository.findByLibelleStatutIgnoreCase(AppConstants.STATUT_CLIENT_ACTIF)
                .orElseThrow(() -> new IllegalStateException("Statut ACTIF introuvable"));
        client.setStatutClient(statutActif);
        clientRepository.save(client);

        // Supprimer de la blacklist
        clientBlacklistRepository.delete(bl);

        // Enregistrer l'historique
        ClientBlacklistHistory hist = new ClientBlacklistHistory();
        hist.setIdClient(idClient);
        hist.setAction("RETRAIT");
        hist.setClientNom(client.getNom());
        hist.setClientPrenom(client.getPrenom());
        hist.setNumeroClient(client.getCodeClient());
        hist.setMotif(bl.getMotif());
        hist.setDetails(requestDTO != null && requestDTO.getMotif() != null ? requestDTO.getMotif() : "Retrait manuel de la blacklist");
        hist.setDateAction(LocalDateTime.now());
        hist.setOperateur(operateur);
        clientBlacklistHistoryRepository.save(hist);
    }
}
