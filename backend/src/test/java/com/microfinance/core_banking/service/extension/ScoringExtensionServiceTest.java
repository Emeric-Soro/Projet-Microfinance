package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerGrilleScoringRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringExtensionServiceTest {

    @Mock private GrilleScoringRepository grilleScoringRepository;
    @Mock private CritereScoringRepository critereScoringRepository;
    @Mock private GrilleScoringDetailRepository grilleScoringDetailRepository;
    @Mock private ResultatScoringRepository resultatScoringRepository;
    @Mock private DemandeCreditRepository demandeCreditRepository;

    @InjectMocks
    private ScoringExtensionService scoringExtensionService;

    @Test
    void creerGrille_withValidData_shouldSucceed() {
        CreerGrilleScoringRequestDTO dto = new CreerGrilleScoringRequestDTO();
        dto.setCodeGrille("GRILLE-CREDIT-001");
        dto.setLibelle("Grille credit standard");
        dto.setActif(true);

        GrilleScoring grille = new GrilleScoring();
        grille.setCodeGrille("GRILLE-CREDIT-001");
        grille.setLibelle("Grille credit standard");
        grille.setActif(true);

        when(grilleScoringRepository.save(any(GrilleScoring.class)))
                .thenReturn(grille);

        GrilleScoring resultat = scoringExtensionService.creerGrille(dto);

        assertNotNull(resultat);
        assertEquals("GRILLE-CREDIT-001", resultat.getCodeGrille());
        assertEquals("Grille credit standard", resultat.getLibelle());
    }
}
