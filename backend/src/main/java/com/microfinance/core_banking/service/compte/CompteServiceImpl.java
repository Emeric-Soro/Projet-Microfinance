package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.StatutCompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CompteServiceImpl implements CompteService {

    private static final String PREFIXE_COMPTE = "CPT";

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TypeCompteRepository typeCompteRepository;
    private final StatutCompteRepository statutCompteRepository;

    public CompteServiceImpl(
            CompteRepository compteRepository,
            ClientRepository clientRepository,
            TypeCompteRepository typeCompteRepository,
            StatutCompteRepository statutCompteRepository
    ) {
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.typeCompteRepository = typeCompteRepository;
        this.statutCompteRepository = statutCompteRepository;
    }

    @Override
    @Transactional
    public Compte ouvrirCompte(Long idClient, String codeTypeCompte, BigDecimal depotInitial) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (codeTypeCompte == null || codeTypeCompte.isBlank()) {
            throw new IllegalArgumentException("Le type de compte est obligatoire");
        }
        if (depotInitial == null || depotInitial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le depot initial doit etre strictement positif");
        }
        if (client.getStatutKyc() != StatutKycClient.VALIDE) {
            throw new IllegalStateException("Le client doit disposer d'un dossier KYC valide avant l'ouverture d'un compte");
        }
        if (clientBloque(client.getStatutClient())) {
            throw new IllegalStateException("Impossible d'ouvrir un compte pour un client bloque ou inactif");
        }

        TypeCompte typeCompte = typeCompteRepository.findByLibelleIgnoreCase(codeTypeCompte)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type de compte '" + codeTypeCompte + "' n'est pas configuré."));

        Compte compte = new Compte();
        compte.setNumCompte(genererNumeroCompteUnique(client));
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setDateOuverture(LocalDate.now());
        compte.setSolde(depotInitial);
        compte.setDevise("XOF");
        compte.setTauxInteret(BigDecimal.ZERO);
        compte.setDecouvertAutorise(BigDecimal.ZERO);

        Compte compteSauvegarde = compteRepository.save(compte);

        StatutCompte statutActif = new StatutCompte();
        statutActif.setCompte(compteSauvegarde);
        statutActif.setLibelleStatut("ACTIF");
        statutActif.setDateStatut(LocalDateTime.now());
        StatutCompte statutActifSauvegarde = statutCompteRepository.save(statutActif);
        compteSauvegarde.getStatutsCompte().add(statutActifSauvegarde);

        return compteSauvegarde;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal consulterSolde(String numCompte) {
        return compteRepository.findByNumCompte(numCompte)
                .map(Compte::getSolde)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
    }

    @Override
    @Transactional
    public Compte changerDecouvertAutorise(String numCompte, BigDecimal nouveauPlafond) {
        if (nouveauPlafond == null || nouveauPlafond.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le découvert autorisé doit être positif ou nul");
        }

        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        compte.setDecouvertAutorise(nouveauPlafond);
        return compteRepository.save(compte);
    }

    @Override
    @Transactional
    public Compte cloturerCompte(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        if (compte.getSolde().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Impossible de clôturer un compte avec un solde non nul");
        }

        StatutCompte statutFerme = new StatutCompte();
        statutFerme.setCompte(compte);
        statutFerme.setLibelleStatut("FERME");
        statutFerme.setDateStatut(LocalDateTime.now());
        StatutCompte statutFermeSauvegarde = statutCompteRepository.save(statutFerme);
        compte.getStatutsCompte().add(statutFermeSauvegarde);

        return compte;
    }

    private String genererNumeroCompteUnique(Client client) {
        String date = LocalDate.now().toString().replace("-", "");
        long sequenceDepart = compteRepository.countByClient_IdClient(client.getIdClient()) + 1;

        for (long increment = 0; increment < 100; increment++) {
            long sequence = sequenceDepart + increment;
            String numero = "%s-%s-%06d-%02d".formatted(
                    PREFIXE_COMPTE,
                    date,
                    client.getIdClient(),
                    sequence
            );
            if (!compteRepository.existsByNumCompte(numero)) {
                return numero;
            }
        }

        throw new IllegalStateException("Impossible de generer un numero de compte unique");
    }

    private boolean clientBloque(StatutClient statutClient) {
        if (statutClient == null || statutClient.getLibelleStatut() == null) {
            return false;
        }
        String statut = statutClient.getLibelleStatut().trim().toUpperCase();
        return "BLOQUE".equals(statut) || "SUSPENDU".equals(statut) || "INACTIF".equals(statut);
    }
}
