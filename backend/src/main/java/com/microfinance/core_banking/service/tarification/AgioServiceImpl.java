package com.microfinance.core_banking.service.tarification;

import com.microfinance.core_banking.entity.Agio;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.TypeAgio;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.tarification.AgioRepository;
import com.microfinance.core_banking.repository.tarification.TypeAgioRepository;
import com.microfinance.core_banking.service.operation.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgioServiceImpl implements AgioService {

    private static final BigDecimal FRAIS_TENUE_MENSUEL = new BigDecimal("1000.00");
    private static final BigDecimal TAUX_PENALITE_DECOUVERT = new BigDecimal("0.05");

    private final AgioRepository agioRepository;
    private final TypeAgioRepository typeAgioRepository;
    private final CompteRepository compteRepository;
    private final TransactionService transactionService;

    public AgioServiceImpl(
            AgioRepository agioRepository,
            TypeAgioRepository typeAgioRepository,
            CompteRepository compteRepository,
            TransactionService transactionService
    ) {
        this.agioRepository = agioRepository;
        this.typeAgioRepository = typeAgioRepository;
        this.compteRepository = compteRepository;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional
    public List<Agio> calculerFraisTenueCompteMensuel() {
        TypeAgio typeTenueCompte = chargerTypeAgioStrict("FRAIS_TENUE");
        LocalDate dateCalcul = LocalDate.now();
        List<Agio> resultats = new ArrayList<>();

        // Traitement par lots (Batch) : On charge les comptes par pages de 500 pour ne pas saturer la RAM
        int page = 0;
        Page<Compte> comptesPage;

        do {
            comptesPage = compteRepository.findAll(PageRequest.of(page, 500));

            for (Compte compte : comptesPage.getContent()) {
                boolean existe = agioRepository.existsByCompte_IdCompteAndTypeAgio_IdTypeAgioAndDateCalcul(
                        compte.getIdCompte(),
                        typeTenueCompte.getIdTypeAgio(),
                        dateCalcul
                );
                if (existe) {
                    continue;
                }

                Agio agio = new Agio();
                agio.setCompte(compte);
                agio.setTypeAgio(typeTenueCompte);
                agio.setDateCalcul(dateCalcul);
                agio.setMontant(FRAIS_TENUE_MENSUEL);
                agio.setEstPreleve(Boolean.FALSE);
                resultats.add(agioRepository.save(agio));
            }
            page++;
        } while (comptesPage.hasNext());

        return resultats;
    }

    @Override
    @Transactional
    public Optional<Agio> calculerPenaliteDecouvert(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        if (compte.getSolde().compareTo(BigDecimal.ZERO) >= 0) {
            return Optional.empty(); // Pas en découvert, pas de pénalité
        }

        TypeAgio typePenalite = chargerTypeAgioStrict("PENALITE_DECOUVERT");
        LocalDate dateCalcul = LocalDate.now();

        if (agioRepository.existsByCompte_IdCompteAndTypeAgio_IdTypeAgioAndDateCalcul(
                compte.getIdCompte(),
                typePenalite.getIdTypeAgio(),
                dateCalcul
        )) {
            return agioRepository.findByCompte_IdCompteAndTypeAgio_IdTypeAgioAndDateCalcul(
                    compte.getIdCompte(),
                    typePenalite.getIdTypeAgio(),
                    dateCalcul
            );
        }

        BigDecimal montantPenalite = compte.getSolde().abs()
                .multiply(TAUX_PENALITE_DECOUVERT)
                .setScale(2, RoundingMode.HALF_UP);

        Agio agio = new Agio();
        agio.setCompte(compte);
        agio.setTypeAgio(typePenalite);
        agio.setDateCalcul(dateCalcul);
        agio.setMontant(montantPenalite);
        agio.setEstPreleve(Boolean.FALSE);

        return Optional.of(agioRepository.save(agio));
    }

    @Override
    @Transactional
    public List<Agio> executerPrelevementsEnAttente(Long idUserSysteme) {
        Page<Agio> page = agioRepository.findByEstPreleve(Boolean.FALSE, PageRequest.of(0, 1000));
        List<Agio> preleves = new ArrayList<>();

        for (Agio agio : page.getContent()) {
            try {
                // On tente de prélever
                transactionService.faireRetrait(agio.getCompte().getNumCompte(), agio.getMontant(), idUserSysteme);
                agio.setEstPreleve(Boolean.TRUE);
                preleves.add(agioRepository.save(agio));
            } catch (IllegalStateException e) {
                // Si le client n'a pas assez d'argent, l'erreur est attrapée ici.
                // On ignore ce client pour le moment, l'agio reste "estPreleve = false"
                // et on continue la boucle pour les autres clients !
            }
        }
        return preleves;
    }

    // --- RÉCUPÉRATION STRICTE DES PARAMÈTRES ---

    private TypeAgio chargerTypeAgioStrict(String code) {
        return typeAgioRepository.findByCodeTypeAgio(code)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le type d'agio '" + code + "' n'est pas paramétré en base."));
    }
}