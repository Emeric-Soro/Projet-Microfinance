package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.RapprochementInterAgence;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.RapprochementInterAgenceRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapprochementInterAgenceServiceTest {

    @Mock private RapprochementInterAgenceRepository rapprochementRepository;
    @Mock private AgenceRepository agenceRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    private RapprochementInterAgenceService service;

    @BeforeEach
    void setUp() {
        service = new RapprochementInterAgenceService(
                rapprochementRepository, agenceRepository, authenticatedUserService);
    }

    @Test
    void creerRapprochement_creeAvecEcartNul() {
        Utilisateur user = new Utilisateur();
        user.setIdUser(1L);

        Agence source = new Agence();
        source.setIdAgence(1L);
        source.setLibelle("Agence Dakar");

        Agence dest = new Agence();
        dest.setIdAgence(2L);
        dest.setLibelle("Agence Thies");

        when(agenceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(user);
        when(rapprochementRepository.save(any(RapprochementInterAgence.class)))
                .thenAnswer(i -> i.getArgument(0));

        RapprochementInterAgence resultat = service.creerRapprochement(
                1L, 2L,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30),
                new BigDecimal("1000000"), new BigDecimal("1000000"),
                "Rapprochement mensuel");

        assertThat(resultat.getAgenceSource().getIdAgence()).isEqualTo(1L);
        assertThat(resultat.getAgenceDestination().getIdAgence()).isEqualTo(2L);
        assertThat(resultat.getMontantDebit()).isEqualByComparingTo(new BigDecimal("1000000"));
        assertThat(resultat.getMontantCredit()).isEqualByComparingTo(new BigDecimal("1000000"));
        assertThat(resultat.getEcart()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultat.getStatut()).isEqualTo("EQUILIBRE");
        assertThat(resultat.getDateRapprochement()).isNotNull();
    }

    @Test
    void creerRapprochement_detecteEcart() {
        Utilisateur user = new Utilisateur();
        user.setIdUser(1L);

        Agence source = new Agence();
        source.setIdAgence(1L);

        Agence dest = new Agence();
        dest.setIdAgence(2L);

        when(agenceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(user);
        when(rapprochementRepository.save(any(RapprochementInterAgence.class)))
                .thenAnswer(i -> i.getArgument(0));

        RapprochementInterAgence resultat = service.creerRapprochement(
                1L, 2L, null, null,
                new BigDecimal("1000000"), new BigDecimal("800000"),
                null);

        assertThat(resultat.getEcart()).isEqualByComparingTo(new BigDecimal("200000"));
        assertThat(resultat.getStatut()).isEqualTo("EN_ECART");
    }

    @Test
    void creerRapprochement_agenceSourceIntrouvable() {
        when(agenceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.creerRapprochement(99L, 1L, null, null, null, null, null))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    void validerRapprochement_changeStatut() {
        Utilisateur user = new Utilisateur();
        user.setIdUser(5L);

        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setIdRapprochementInterAgence(1L);
        rapprochement.setStatut("EQUILIBRE");

        when(rapprochementRepository.findById(1L)).thenReturn(Optional.of(rapprochement));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(user);
        when(rapprochementRepository.save(any(RapprochementInterAgence.class)))
                .thenAnswer(i -> i.getArgument(0));

        RapprochementInterAgence resultat = service.validerRapprochement(1L, null);

        assertThat(resultat.getStatut()).isEqualTo("APPROUVE");
        assertThat(resultat.getValidateur().getIdUser()).isEqualTo(5L);
    }

    @Test
    void validerRapprochement_refuseEnEcart() {
        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setIdRapprochementInterAgence(2L);
        rapprochement.setStatut("EN_ECART");
        rapprochement.setEcart(new BigDecimal("50000"));

        when(rapprochementRepository.findById(2L)).thenReturn(Optional.of(rapprochement));

        assertThatThrownBy(() -> service.validerRapprochement(2L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ecart");
    }

    @Test
    void validerRapprochement_refuseDejaApprouve() {
        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setIdRapprochementInterAgence(3L);
        rapprochement.setStatut("APPROUVE");

        when(rapprochementRepository.findById(3L)).thenReturn(Optional.of(rapprochement));

        assertThatThrownBy(() -> service.validerRapprochement(3L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("deja");
    }

    @Test
    void rejeterRapprochement_changeStatut() {
        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setIdRapprochementInterAgence(4L);
        rapprochement.setStatut("EQUILIBRE");

        when(rapprochementRepository.findById(4L)).thenReturn(Optional.of(rapprochement));
        when(rapprochementRepository.save(any(RapprochementInterAgence.class)))
                .thenAnswer(i -> i.getArgument(0));

        RapprochementInterAgence resultat = service.rejeterRapprochement(4L, "Motif test");

        assertThat(resultat.getStatut()).isEqualTo("REJETE");
        assertThat(resultat.getCommentaire()).contains("Motif test");
    }

    @Test
    void listerRapprochements_parAgence() {
        when(rapprochementRepository.findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateRapprochementDesc(1L, 1L))
                .thenReturn(List.of(new RapprochementInterAgence()));

        List<RapprochementInterAgence> resultat = service.listerRapprochements(1L);

        assertThat(resultat).hasSize(1);
    }

    @Test
    void consulterRapprochement_trouve() {
        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setIdRapprochementInterAgence(1L);
        when(rapprochementRepository.findById(1L)).thenReturn(Optional.of(rapprochement));

        Optional<RapprochementInterAgence> resultat = service.consulterRapprochement(1L);

        assertThat(resultat).isPresent();
    }
}
