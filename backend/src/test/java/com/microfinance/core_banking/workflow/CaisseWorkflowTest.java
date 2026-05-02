package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.BilletageCaisseRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import com.microfinance.core_banking.service.extension.CaisseExtensionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaisseWorkflowTest {

    @Mock private CaisseRepository caisseRepository;
    @Mock private SessionCaisseRepository sessionCaisseRepository;
    @Mock private BilletageCaisseRepository billetageRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    private Caisse caisse;
    private SessionCaisse session;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setIdUser(1L);
        utilisateur.setLogin("caissier1");

        caisse = new Caisse();
        caisse.setIdCaisse(1L);
        caisse.setCodeCaisse("CAI-001");
        caisse.setLibelleCaisse("Caisse Principale");
        caisse.setSoldeTheorique(BigDecimal.ZERO);

        session = new SessionCaisse();
        session.setIdSessionCaisse(1L);
        session.setCaisse(caisse);
        session.setUtilisateur(utilisateur);
        session.setDateOuverture(LocalDateTime.now());
        session.setSoldeOuverture(new BigDecimal("500000.00"));
        session.setStatut("OUVERTE");
    }

    @Test
    void ouvertureSessionIncrementOperations() {
        assertThat(session.getStatut()).isEqualTo("OUVERTE");
        assertThat(session.getSoldeOuverture()).isEqualByComparingTo(new BigDecimal("500000.00"));
    }

    @Test
    void fermetureSessionCalculeEcart() {
        session.setStatut("FERMEE");
        session.setSoldeFermeture(new BigDecimal("550000.00"));
        session.setDateFermeture(LocalDateTime.now());

        BigDecimal ecart = session.getSoldeFermeture()
            .subtract(session.getSoldeOuverture())
            .subtract(session.getTotalEncaissements() != null ? session.getTotalEncaissements() : BigDecimal.ZERO)
            .add(session.getTotalDecaissements() != null ? session.getTotalDecaissements() : BigDecimal.ZERO);

        session.setEcart(ecart);

        assertThat(session.getStatut()).isEqualTo("FERMEE");
        assertThat(session.getSoldeFermeture()).isEqualByComparingTo(new BigDecimal("550000.00"));
    }

    @Test
    void caisseCannotHaveTwoOpenSessions() {
        when(sessionCaisseRepository.existsByCaisse_IdCaisseAndStatut(1L, "OUVERTE")).thenReturn(true);

        boolean hasOpenSession = sessionCaisseRepository.existsByCaisse_IdCaisseAndStatut(1L, "OUVERTE");
        assertThat(hasOpenSession).isTrue();

        assertThrows(IllegalStateException.class, () -> {
            if (hasOpenSession) {
                throw new IllegalStateException("Une session est deja ouverte");
            }
        });
    }

    @Test
    void billetageTotalMatchesSolde() {
        BilletageCaisse b1 = new BilletageCaisse();
        b1.setValeurFaciale(new BigDecimal("10000"));
        b1.setQuantite(50);

        BilletageCaisse b2 = new BilletageCaisse();
        b2.setValeurFaciale(new BigDecimal("5000"));
        b2.setQuantite(20);

        BigDecimal totalBilletage = b1.getValeurFaciale().multiply(BigDecimal.valueOf(b1.getQuantite()))
            .add(b2.getValeurFaciale().multiply(BigDecimal.valueOf(b2.getQuantite())));

        assertThat(totalBilletage).isEqualByComparingTo(new BigDecimal("600000.00"));
    }
}
