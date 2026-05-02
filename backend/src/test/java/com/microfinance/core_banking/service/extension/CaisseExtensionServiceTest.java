package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.BilletageCaisse;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.extension.BilletageCaisseRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaisseExtensionServiceTest {

    @Mock private CaisseRepository caisseRepository;
    @Mock private SessionCaisseRepository sessionCaisseRepository;
    @Mock private BilletageCaisseRepository billetageCaisseRepository;

    private CaisseExtensionService service;

    @BeforeEach
    void setUp() {
        service = new CaisseExtensionService(caisseRepository, sessionCaisseRepository, billetageCaisseRepository);
    }

    @Test
    void listerCaisses_retourneToutes() {
        Caisse caisse = new Caisse();
        caisse.setIdCaisse(1L);
        caisse.setCodeCaisse("CAIS-001");
        when(caisseRepository.findAll()).thenReturn(List.of(caisse));

        List<Caisse> resultat = service.listerCaisses();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCodeCaisse()).isEqualTo("CAIS-001");
    }

    @Test
    void consulterCaisse_trouvee() {
        Caisse caisse = new Caisse();
        caisse.setIdCaisse(1L);
        when(caisseRepository.findById(1L)).thenReturn(Optional.of(caisse));

        Optional<Caisse> resultat = service.consulterCaisse(1L);

        assertThat(resultat).isPresent();
    }

    @Test
    void consulterCaisse_introuvable() {
        when(caisseRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Caisse> resultat = service.consulterCaisse(99L);

        assertThat(resultat).isEmpty();
    }

    @Test
    void enregistrerBilletage_calculeTotal() {
        SessionCaisse session = new SessionCaisse();
        session.setIdSessionCaisse(1L);
        when(sessionCaisseRepository.findById(1L)).thenReturn(Optional.of(session));
        when(billetageCaisseRepository.save(any(BilletageCaisse.class))).thenAnswer(i -> i.getArgument(0));

        BilletageCaisse resultat = service.enregistrerBilletage(1L, new BigDecimal("10000"), 5, "BILLET");

        assertThat(resultat.getCoupure()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(resultat.getQuantite()).isEqualTo(5);
        assertThat(resultat.getTotal()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(resultat.getTypeBilletage()).isEqualTo("BILLET");
        assertThat(resultat.getDateBilletage()).isNotNull();
    }

    @Test
    void enregistrerBilletage_sessionIntrouvable() {
        when(sessionCaisseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enregistrerBilletage(99L, new BigDecimal("1000"), 10, "PIECE"))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    void enregistrerBilletage_typePiece() {
        SessionCaisse session = new SessionCaisse();
        session.setIdSessionCaisse(2L);
        when(sessionCaisseRepository.findById(2L)).thenReturn(Optional.of(session));
        when(billetageCaisseRepository.save(any(BilletageCaisse.class))).thenAnswer(i -> i.getArgument(0));

        BilletageCaisse resultat = service.enregistrerBilletage(2L, new BigDecimal("500"), 20, "PIECE");

        assertThat(resultat.getTypeBilletage()).isEqualTo("PIECE");
        assertThat(resultat.getTotal()).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    void listerBilletageParSession_retourneListe() {
        SessionCaisse session = new SessionCaisse();
        session.setIdSessionCaisse(1L);
        when(sessionCaisseRepository.findById(1L)).thenReturn(Optional.of(session));

        BilletageCaisse billet = new BilletageCaisse();
        billet.setIdBilletage(1L);
        billet.setSessionCaisse(session);
        when(billetageCaisseRepository.findBySessionCaisse_IdSessionCaisse(1L)).thenReturn(List.of(billet));

        List<BilletageCaisse> resultat = service.listerBilletageParSession(1L);

        assertThat(resultat).hasSize(1);
    }

    @Test
    void genererArreteCaisse_agregeDonnees() {
        Agence agence = new Agence();
        agence.setIdAgence(1L);
        agence.setLibelle("Agence Dakar");

        Caisse caisse = new Caisse();
        caisse.setIdCaisse(1L);
        caisse.setCodeCaisse("CAIS-001");
        caisse.setLibelle("Caisse Principale");
        caisse.setAgence(agence);
        caisse.setSoldeTheorique(new BigDecimal("1500000"));

        Utilisateur user = new Utilisateur();
        user.setIdUser(1L);

        SessionCaisse session1 = new SessionCaisse();
        session1.setIdSessionCaisse(1L);
        session1.setCaisse(caisse);
        session1.setUtilisateur(user);
        session1.setSoldeOuverture(new BigDecimal("500000"));
        session1.setSoldeTheoriqueFermeture(new BigDecimal("1500000"));
        session1.setSoldePhysiqueFermeture(new BigDecimal("1495000"));
        session1.setEcart(new BigDecimal("-5000"));
        session1.setStatut("FERMEE");
        session1.setDateOuverture(LocalDateTime.of(2026, 5, 2, 8, 0));
        session1.setDateFermeture(LocalDateTime.of(2026, 5, 2, 17, 0));

        when(caisseRepository.findById(1L)).thenReturn(Optional.of(caisse));
        when(sessionCaisseRepository.findByCaisse_IdCaisseOrderByDateOuvertureDesc(1L)).thenReturn(List.of(session1));

        Map<String, Object> resultat = service.genererArreteCaisse(1L, LocalDate.of(2026, 5, 2));

        assertThat(resultat.get("codeCaisse")).isEqualTo("CAIS-001");
        assertThat(resultat.get("totalSessions")).isEqualTo(1);
        assertThat(resultat.get("totalSoldeTheorique")).isEqualTo(new BigDecimal("1500000"));
        assertThat(resultat.get("totalSoldePhysique")).isEqualTo(new BigDecimal("1495000"));
        assertThat(resultat.get("totalEcart")).isEqualTo(new BigDecimal("-5000"));
    }

    @Test
    void genererArreteCaisse_caisseIntrouvable() {
        when(caisseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.genererArreteCaisse(99L, LocalDate.now()))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    void genererArreteCaisse_sansDateFiltreTout() {
        Caisse caisse = new Caisse();
        caisse.setIdCaisse(1L);
        caisse.setCodeCaisse("CAIS-001");
        caisse.setLibelle("Caisse");
        caisse.setSoldeTheorique(new BigDecimal("1000000"));

        SessionCaisse session1 = new SessionCaisse();
        session1.setIdSessionCaisse(1L);
        session1.setCaisse(caisse);
        session1.setSoldeOuverture(new BigDecimal("200000"));
        session1.setSoldeTheoriqueFermeture(new BigDecimal("1000000"));
        session1.setSoldePhysiqueFermeture(new BigDecimal("1000000"));
        session1.setEcart(BigDecimal.ZERO);
        session1.setStatut("FERMEE");

        when(caisseRepository.findById(1L)).thenReturn(Optional.of(caisse));
        when(sessionCaisseRepository.findByCaisse_IdCaisseOrderByDateOuvertureDesc(1L)).thenReturn(List.of(session1));

        Map<String, Object> resultat = service.genererArreteCaisse(1L, null);

        assertThat(resultat.get("totalSessions")).isEqualTo(1);
    }
}
