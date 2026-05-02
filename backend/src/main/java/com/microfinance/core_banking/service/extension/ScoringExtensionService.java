package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.AjouterDetailGrilleRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCritereScoringRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerGrilleScoringRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ExecuterScoringRequestDTO;
import com.microfinance.core_banking.entity.CritereScoring;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.GrilleScoring;
import com.microfinance.core_banking.entity.GrilleScoringDetail;
import com.microfinance.core_banking.entity.ResultatScoring;
import com.microfinance.core_banking.repository.extension.CritereScoringRepository;
import com.microfinance.core_banking.repository.extension.DemandeCreditRepository;
import com.microfinance.core_banking.repository.extension.GrilleScoringDetailRepository;
import com.microfinance.core_banking.repository.extension.GrilleScoringRepository;
import com.microfinance.core_banking.repository.extension.ResultatScoringRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScoringExtensionService {

    private final CritereScoringRepository critereScoringRepository;
    private final GrilleScoringRepository grilleScoringRepository;
    private final GrilleScoringDetailRepository grilleScoringDetailRepository;
    private final ResultatScoringRepository resultatScoringRepository;
    private final DemandeCreditRepository demandeCreditRepository;

    public ScoringExtensionService(
            CritereScoringRepository critereScoringRepository,
            GrilleScoringRepository grilleScoringRepository,
            GrilleScoringDetailRepository grilleScoringDetailRepository,
            ResultatScoringRepository resultatScoringRepository,
            DemandeCreditRepository demandeCreditRepository
    ) {
        this.critereScoringRepository = critereScoringRepository;
        this.grilleScoringRepository = grilleScoringRepository;
        this.grilleScoringDetailRepository = grilleScoringDetailRepository;
        this.resultatScoringRepository = resultatScoringRepository;
        this.demandeCreditRepository = demandeCreditRepository;
    }

    @Transactional
    public CritereScoring creerCritere(CreerCritereScoringRequestDTO dto) {
        CritereScoring critere = new CritereScoring();
        critere.setCodeCritere(dto.getCodeCritere());
        critere.setLibelle(dto.getLibelle());
        critere.setCategorie(dto.getCategorie());
        critere.setTypeValeur(dto.getTypeValeur());
        critere.setPoids(dto.getPoids());
        critere.setActif(dto.getActif() != null ? dto.getActif() : Boolean.TRUE);
        return critereScoringRepository.save(critere);
    }

    @Transactional
    public GrilleScoring creerGrille(CreerGrilleScoringRequestDTO dto) {
        GrilleScoring grille = new GrilleScoring();
        grille.setCodeGrille(dto.getCodeGrille());
        grille.setLibelle(dto.getLibelle());
        grille.setSeuilApprobation(dto.getSeuilApprobation());
        grille.setSeuilRejet(dto.getSeuilRejet());
        grille.setActif(dto.getActif() != null ? dto.getActif() : Boolean.TRUE);
        return grilleScoringRepository.save(grille);
    }

    @Transactional
    public GrilleScoringDetail ajouterDetail(AjouterDetailGrilleRequestDTO dto) {
        GrilleScoring grille = grilleScoringRepository.findById(dto.getIdGrilleScoring())
                .orElseThrow(() -> new EntityNotFoundException("Grille scoring introuvable"));
        CritereScoring critere = critereScoringRepository.findById(dto.getIdCritereScoring())
                .orElseThrow(() -> new EntityNotFoundException("Critere scoring introuvable"));
        GrilleScoringDetail detail = new GrilleScoringDetail();
        detail.setGrilleScoring(grille);
        detail.setCritereScoring(critere);
        detail.setValeurMin(dto.getValeurMin());
        detail.setValeurMax(dto.getValeurMax());
        detail.setPoints(dto.getPoints());
        return grilleScoringDetailRepository.save(detail);
    }

    @Transactional
    public ResultatScoring executerScoring(ExecuterScoringRequestDTO dto) {
        DemandeCredit demande = demandeCreditRepository.findById(dto.getIdDemandeCredit())
                .orElseThrow(() -> new EntityNotFoundException("Demande credit introuvable"));
        GrilleScoring grille = grilleScoringRepository.findById(dto.getIdGrilleScoring())
                .orElseThrow(() -> new EntityNotFoundException("Grille scoring introuvable"));
        List<GrilleScoringDetail> details = grilleScoringDetailRepository.findByGrilleScoring_IdGrilleScoring(grille.getIdGrilleScoring());

        int scoreTotal = 0;
        StringBuilder detailsBuilder = new StringBuilder();

        for (GrilleScoringDetail detail : details) {
            int points = detail.getPoints();
            scoreTotal += points;
            detailsBuilder.append(detail.getCritereScoring().getLibelle())
                    .append(": ").append(points).append(" pts\n");
        }

        String decision;
        if (scoreTotal >= grille.getSeuilApprobation()) {
            decision = "APPROUVE";
        } else if (scoreTotal <= grille.getSeuilRejet()) {
            decision = "REJETE";
        } else {
            decision = "MANUEL";
        }

        ResultatScoring resultat = new ResultatScoring();
        resultat.setDemandeCredit(demande);
        resultat.setGrilleScoring(grille);
        resultat.setScoreTotal(scoreTotal);
        resultat.setDecision(decision);
        resultat.setDetails(detailsBuilder.toString());
        resultat.setDateScoring(LocalDateTime.now());

        demande.setScoreCredit(scoreTotal);
        demandeCreditRepository.save(demande);

        return resultatScoringRepository.save(resultat);
    }

    @Transactional(readOnly = true)
    public List<CritereScoring> listerCriteres() {
        return critereScoringRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GrilleScoring> listerGrilles() {
        return grilleScoringRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ResultatScoring> getResultatByDemande(Long idDemandeCredit) {
        return resultatScoringRepository.findByDemandeCredit_IdDemandeCredit(idDemandeCredit);
    }
}
