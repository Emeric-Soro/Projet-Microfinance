package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutCompte;
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
import java.util.UUID;

@Service
public class CompteServiceImpl implements CompteService {

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
    public Compte ouvrirCompte(Long idClient, String codeTypeCompte) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (codeTypeCompte == null || codeTypeCompte.isBlank()) {
            throw new IllegalArgumentException("Le type de compte est obligatoire");
        }

        // Récupération stricte : on refuse d'inventer un type de compte !
        TypeCompte typeCompte = typeCompteRepository.findByLibelleIgnoreCase(codeTypeCompte)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type de compte '" + codeTypeCompte + "' n'est pas configuré."));

        Compte compte = new Compte();
        compte.setNumCompte(genererNumeroCompteUnique());
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setDateOuverture(LocalDate.now());
        compte.setSolde(BigDecimal.ZERO);
        compte.setDevise("XOF"); // Standard BCEAO
        compte.setTauxInteret(BigDecimal.ZERO);
        compte.setDecouvertAutorise(BigDecimal.ZERO);

        Compte compteSauvegarde = compteRepository.save(compte);

        // Ajout de la première trace dans l'historique des statuts
        StatutCompte statutActif = new StatutCompte();
        statutActif.setCompte(compteSauvegarde);
        statutActif.setLibelleStatut("ACTIF");
        statutActif.setDateStatut(LocalDateTime.now());
        statutCompteRepository.save(statutActif);

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

        // Ajout d'une nouvelle trace dans l'historique des statuts
        StatutCompte statutFerme = new StatutCompte();
        statutFerme.setCompte(compte);
        statutFerme.setLibelleStatut("FERME");
        statutFerme.setDateStatut(LocalDateTime.now());
        statutCompteRepository.save(statutFerme);

        return compte;
    }

    private String genererNumeroCompteUnique() {
        String numero;
        do {
            numero = "CP" + UUID.randomUUID().toString().replace("-", "").substring(0, 18).toUpperCase();
        } while (compteRepository.existsByNumCompte(numero));
        return numero;
    }
}