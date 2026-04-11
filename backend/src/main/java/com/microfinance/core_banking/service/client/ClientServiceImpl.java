package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final StatutClientRepository statutClientRepository;

    public ClientServiceImpl(ClientRepository clientRepository, StatutClientRepository statutClientRepository) {
        this.clientRepository = clientRepository;
        this.statutClientRepository = statutClientRepository;
    }

    @Override
    @Transactional
    public Client creerClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas etre null");
        }
        if (client.getCodeClient() == null || client.getCodeClient().isBlank()) {
            throw new IllegalArgumentException("Le code client est obligatoire");
        }
        if (clientRepository.existsByCodeClient(client.getCodeClient())) {
            throw new IllegalArgumentException("Code client deja utilise");
        }
        if (client.getEmail() != null && clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email deja utilise");
        }
        if (client.getTelephone() != null && clientRepository.existsByTelephone(client.getTelephone())) {
            throw new IllegalArgumentException("Telephone deja utilise");
        }

        // Récupération stricte : Si "NOUVEAU" n'est pas dans la base, l'application bloque (Sécurité !)
        StatutClient statutParDefaut = statutClientRepository.findByLibelleStatutIgnoreCase("NOUVEAU")
                .orElseThrow(() -> new IllegalStateException("Erreur critique : Le statut 'NOUVEAU' n'est pas paramétré en base."));

        client.setStatutClient(statutParDefaut);

        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client modifierStatutClient(Long idClient, String nouveauStatut) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (nouveauStatut == null || nouveauStatut.isBlank()) {
            throw new IllegalArgumentException("Le nouveau statut est obligatoire");
        }

        // Récupération stricte : on refuse d'inventer un statut qui n'existe pas
        StatutClient statut = statutClientRepository.findByLibelleStatutIgnoreCase(nouveauStatut)
                .orElseThrow(() -> new IllegalArgumentException("Le statut '" + nouveauStatut + "' n'existe pas dans le système."));

        client.setStatutClient(statut);
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client obtenirDetailsClient(Long idClient) {
        return clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));
    }
}