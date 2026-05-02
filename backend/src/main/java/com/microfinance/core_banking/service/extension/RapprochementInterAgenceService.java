package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.RapprochementInterAgence;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.RapprochementInterAgenceRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RapprochementInterAgenceService {

    private final RapprochementInterAgenceRepository rapprochementRepository;
    private final AgenceRepository agenceRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public RapprochementInterAgenceService(
            RapprochementInterAgenceRepository rapprochementRepository,
            AgenceRepository agenceRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.rapprochementRepository = rapprochementRepository;
        this.agenceRepository = agenceRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public RapprochementInterAgence creerRapprochement(
            Long idAgenceSource, Long idAgenceDestination,
            LocalDate periodeDebut, LocalDate periodeFin,
            BigDecimal montantDebit, BigDecimal montantCredit,
            String commentaire
    ) {
        Agence source = agenceRepository.findById(idAgenceSource)
                .orElseThrow(() -> new EntityNotFoundException("Agence source introuvable"));
        Agence destination = agenceRepository.findById(idAgenceDestination)
                .orElseThrow(() -> new EntityNotFoundException("Agence destination introuvable"));

        LocalDate debut = periodeDebut != null ? periodeDebut : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = periodeFin != null ? periodeFin : LocalDate.now();

        if (fin.isBefore(debut)) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure a la date de debut");
        }

        BigDecimal debit = montantDebit != null ? montantDebit : BigDecimal.ZERO;
        BigDecimal credit = montantCredit != null ? montantCredit : BigDecimal.ZERO;
        BigDecimal ecart = debit.subtract(credit);

        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setAgenceSource(source);
        rapprochement.setAgenceDestination(destination);
        rapprochement.setPeriodeDebut(debut);
        rapprochement.setPeriodeFin(fin);
        rapprochement.setDateRapprochement(LocalDateTime.now());
        rapprochement.setMontantDebit(debit);
        rapprochement.setMontantCredit(credit);
        rapprochement.setEcart(ecart);
        rapprochement.setStatut(ecart.compareTo(BigDecimal.ZERO) == 0 ? "EQUILIBRE" : "EN_ECART");
        rapprochement.setCommentaire(commentaire);
        return rapprochementRepository.save(rapprochement);
    }

    @Transactional
    public RapprochementInterAgence validerRapprochement(Long id, Long idValidateur) {
        RapprochementInterAgence rapprochement = rapprochementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapprochement introuvable"));
        if ("APPROUVE".equalsIgnoreCase(rapprochement.getStatut())) {
            throw new IllegalStateException("Le rapprochement est deja approuve");
        }
        if (!"EN_ECART".equalsIgnoreCase(rapprochement.getStatut()) && !"EQUILIBRE".equalsIgnoreCase(rapprochement.getStatut())) {
            throw new IllegalStateException("Seuls les rapprochements avec statut EN_ECART ou EQUILIBRE peuvent etre valides");
        }
        rapprochement.setStatut("APPROUVE");
        rapprochement.setValidateur(authenticatedUserService.getCurrentUserOrThrow());
        if (idValidateur != null) {
            rapprochement.setValidateur(authenticatedUserService.getCurrentUserOrThrow());
        }
        if (rapprochement.getEcart().compareTo(BigDecimal.ZERO) != 0) {
            rapprochement.setCommentaire(rapprochement.getCommentaire() != null
                    ? rapprochement.getCommentaire() + " | VALIDE AVEC ECART: " + rapprochement.getEcart()
                    : "VALIDE AVEC ECART: " + rapprochement.getEcart());
        }
        return rapprochementRepository.save(rapprochement);
    }

    @Transactional
    public RapprochementInterAgence rejeterRapprochement(Long id, String motif) {
        RapprochementInterAgence rapprochement = rapprochementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapprochement introuvable"));
        if ("APPROUVE".equalsIgnoreCase(rapprochement.getStatut())) {
            throw new IllegalStateException("Le rapprochement est deja approuve");
        }
        rapprochement.setStatut("REJETE");
        rapprochement.setCommentaire(rapprochement.getCommentaire() != null
                ? rapprochement.getCommentaire() + " | REJETE: " + motif
                : "REJETE: " + motif);
        return rapprochementRepository.save(rapprochement);
    }

    @Transactional(readOnly = true)
    public List<RapprochementInterAgence> listerRapprochements(Long idAgence) {
        return rapprochementRepository.findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateRapprochementDesc(idAgence, idAgence);
    }

    @Transactional(readOnly = true)
    public Optional<RapprochementInterAgence> consulterRapprochement(Long id) {
        return rapprochementRepository.findById(id);
    }
}
