package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.constant.AppConstants;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutKycClient;
import com.soutra.microfinance.entity.TypeCompte;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.compte.StatutCompteRepository;
import com.soutra.microfinance.repository.compte.TypeCompteRepository;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CompteServiceImpl implements CompteService {

    private static final String PREFIXE_COMPTE = "CPT";

    @Value("${app.default.currency}")
    private String defaultCurrency;

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
        // Validation du montant : doit être positif pour que faireDepotInitial puisse l'exécuter
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
        // Le solde est initialisé à zéro : il sera crédité par faireDepotInitial
        // via une vraie Transaction comptable (LigneEcriture tracée).
        compte.setSolde(BigDecimal.ZERO);
        compte.setDevise(defaultCurrency);
        compte.setTauxInteret(BigDecimal.ZERO);
        compte.setDecouvertAutorise(BigDecimal.ZERO);

        Compte compteSauvegarde = compteRepository.save(compte);

        StatutCompte statutActif = new StatutCompte();
        statutActif.setCompte(compteSauvegarde);
        statutActif.setLibelleStatut(AppConstants.STATUT_COMPTE_ACTIF);
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
    @Transactional(readOnly = true)
    public Compte consulterCompte(Long idCompte) {
        return compteRepository.findById(idCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + idCompte));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Compte> listerComptesClient(Long idClient, Pageable pageable) {
        if (!clientRepository.existsById(idClient)) {
            throw new EntityNotFoundException("Client introuvable: " + idClient);
        }
        return compteRepository.findByClient_IdClient(idClient, pageable);
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
            throw new IllegalStateException("Impossible de cloturer un compte avec un solde non nul");
        }

        StatutCompte statutFerme = new StatutCompte();
        statutFerme.setCompte(compte);
        statutFerme.setLibelleStatut(AppConstants.STATUT_COMPTE_FERME);
        statutFerme.setDateStatut(LocalDateTime.now());
        StatutCompte statutFermeSauvegarde = statutCompteRepository.save(statutFerme);
        compte.getStatutsCompte().add(statutFermeSauvegarde);

        return compte;
    }

    @Override
    @Transactional
    public Compte bloquerCompte(String numCompte, String motif) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        String statutCourant = extraireStatutCourant(compte);
        if (AppConstants.STATUT_COMPTE_FERME.equalsIgnoreCase(statutCourant)) {
            throw new IllegalStateException("Impossible de bloquer un compte deja cloture");
        }
        if (AppConstants.STATUT_COMPTE_BLOQUE.equalsIgnoreCase(statutCourant)) {
            throw new IllegalStateException("Le compte est deja bloque");
        }

        StatutCompte statutBloque = new StatutCompte();
        statutBloque.setCompte(compte);
        statutBloque.setLibelleStatut(AppConstants.STATUT_COMPTE_BLOQUE);
        statutBloque.setDateStatut(LocalDateTime.now());
        StatutCompte statutBloqueSauvegarde = statutCompteRepository.save(statutBloque);
        compte.getStatutsCompte().add(statutBloqueSauvegarde);

        return compte;
    }

    @Override
    @Transactional
    public Compte debloquerCompte(String numCompte, String motif) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        String statutCourant = extraireStatutCourant(compte);
        if (!AppConstants.STATUT_COMPTE_BLOQUE.equalsIgnoreCase(statutCourant)) {
            throw new IllegalStateException("Le compte n'est pas bloque, statut actuel: " + statutCourant);
        }

        StatutCompte statutActif = new StatutCompte();
        statutActif.setCompte(compte);
        statutActif.setLibelleStatut(AppConstants.STATUT_COMPTE_ACTIF);
        statutActif.setDateStatut(LocalDateTime.now());
        StatutCompte statutActifSauvegarde = statutCompteRepository.save(statutActif);
        compte.getStatutsCompte().add(statutActifSauvegarde);

        return compte;
    }

    @Override
    @Transactional(readOnly = true)
    public Compte obtenirCompteParNumero(String numCompte) {
        return compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
    }

    private String extraireStatutCourant(Compte compte) {
        if (compte.getStatutsCompte() == null || compte.getStatutsCompte().isEmpty()) {
            return null;
        }
        return compte.getStatutsCompte().stream()
                .max(java.util.Comparator.comparing(StatutCompte::getDateStatut, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .map(StatutCompte::getLibelleStatut)
                .orElse(null);
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
        return AppConstants.STATUT_CLIENT_BLOQUE.equals(statut)
                || AppConstants.STATUT_CLIENT_SUSPENDU.equals(statut)
                || AppConstants.STATUT_CLIENT_INACTIF.equals(statut);
    }
}
