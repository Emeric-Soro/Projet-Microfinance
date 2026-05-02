package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.ConsentementOpenBankingRepository;
import com.microfinance.core_banking.repository.extension.PartenaireApiRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OpenBankingService {

    private final ConsentementOpenBankingRepository consentementRepository;
    private final PartenaireApiRepository partenaireApiRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;

    public OpenBankingService(ConsentementOpenBankingRepository consentementRepository,
                              PartenaireApiRepository partenaireApiRepository,
                              ClientRepository clientRepository,
                              CompteRepository compteRepository) {
        this.consentementRepository = consentementRepository;
        this.partenaireApiRepository = partenaireApiRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
    }

    @Transactional
    public ConsentementOpenBanking consentir(Long partenaireApiId, Long clientId,
                                              String typeConsentement, String scope) {
        PartenaireApi partenaire = partenaireApiRepository.findById(partenaireApiId)
                .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable: " + partenaireApiId));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));

        if (!"ACTIF".equals(partenaire.getStatut())) {
            throw new IllegalStateException("Le partenaire API n'est pas actif");
        }

        ConsentementOpenBanking consentement = new ConsentementOpenBanking();
        consentement.setRefConsentement("CONS-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase());
        consentement.setPartenaireApi(partenaire);
        consentement.setClient(client);
        consentement.setTypeConsentement(typeConsentement);
        consentement.setScope(scope);
        consentement.setDateDebut(LocalDateTime.now());
        consentement.setStatut("ACTIF");

        return consentementRepository.save(consentement);
    }

    @Transactional
    public ConsentementOpenBanking revoguer(String refConsentement, String motif) {
        ConsentementOpenBanking consentement = consentementRepository.findByRefConsentement(refConsentement)
                .orElseThrow(() -> new EntityNotFoundException("Consentement introuvable: " + refConsentement));
        consentement.setStatut("REVOQUE");
        consentement.setDateFin(LocalDateTime.now());
        return consentementRepository.save(consentement);
    }

    @Transactional(readOnly = true)
    public ConsentementOpenBanking verifierConsentement(String refConsentement) {
        ConsentementOpenBanking consentement = consentementRepository.findByRefConsentement(refConsentement)
                .orElseThrow(() -> new EntityNotFoundException("Consentement introuvable: " + refConsentement));

        if (!"ACTIF".equals(consentement.getStatut())) {
            throw new IllegalStateException("Le consentement n'est plus actif (statut: " + consentement.getStatut() + ")");
        }
        if (consentement.getDateFin() != null && consentement.getDateFin().isBefore(LocalDateTime.now())) {
            consentement.setStatut("EXPIRE");
            consentementRepository.save(consentement);
            throw new IllegalStateException("Le consentement a expire");
        }
        return consentement;
    }

    @Transactional(readOnly = true)
    public List<Compte> listerComptesClient(String refConsentement) {
        ConsentementOpenBanking consentement = verifierConsentement(refConsentement);
        return compteRepository.findByClient_IdClient(consentement.getClient().getIdClient());
    }

    @Transactional(readOnly = true)
    public List<ConsentementOpenBanking> listerConsentementsClient(Long clientId) {
        return consentementRepository.findByClient_IdClient(clientId);
    }

    @Transactional(readOnly = true)
    public List<ConsentementOpenBanking> listerConsentementsParStatut(String statut) {
        return consentementRepository.findByStatut(statut);
    }
}
