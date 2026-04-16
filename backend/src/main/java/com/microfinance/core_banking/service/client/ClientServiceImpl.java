package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

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
        if (client.getEmail() != null && clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email deja utilise");
        }
        if (client.getTelephone() != null && clientRepository.existsByTelephone(client.getTelephone())) {
            throw new IllegalArgumentException("Telephone deja utilise");
        }

        // Le code client est gere par le backend pour eviter de l'imposer au formulaire d'inscription.
        client.setCodeClient(genererCodeClientUnique());
        // Date d'inscription renseignee automatiquement a la creation.
        client.setDateInscription(LocalDate.now());

        // Récupération stricte : Si "NOUVEAU" n'est pas dans la base, l'application bloque (Sécurité !)
        StatutClient statutParDefaut = statutClientRepository.findByLibelleStatutIgnoreCase("NOUVEAU")
                .orElseThrow(() -> new IllegalStateException("Erreur critique : Le statut 'NOUVEAU' n'est pas paramétré en base."));

        client.setStatutClient(statutParDefaut);

        return clientRepository.save(client);
    }

    // Genere un identifiant metier lisible de type CLI-YYYYMMDD-XXXX et verifie l'unicite.
    private String genererCodeClientUnique() {
        String prefixeDate = LocalDate.now().toString().replace("-", "");
        for (int tentative = 0; tentative < 20; tentative++) {
            // Genere un suffixe sur 4 chiffres pour limiter les collisions.
            int suffixe = ThreadLocalRandom.current().nextInt(1000, 10000);
            String code = "CLI-" + prefixeDate + "-" + suffixe;
            if (!clientRepository.existsByCodeClient(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de generer un code client unique");
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

    @Override
    @Transactional(readOnly = true)
    public Page<Client> listerClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }
}