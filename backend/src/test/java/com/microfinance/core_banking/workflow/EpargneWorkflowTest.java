package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.DepotATermeRepository;
import com.microfinance.core_banking.repository.extension.EpargneRepository;
import com.microfinance.core_banking.repository.extension.ProduitEpargneRepository;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EpargneWorkflowTest {

    @Mock private EpargneRepository epargneRepository;
    @Mock private ProduitEpargneRepository produitEpargneRepository;
    @Mock private DepotATermeRepository depotATermeRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    private Epargne epargne;
    private DepotATerme dat;
    private ProduitEpargne produit;

    @BeforeEach
    void setUp() {
        produit = new ProduitEpargne();
        produit.setIdProduitEpargne(1L);
        produit.setCodeProduit("EPG_STANDARD");
        produit.setTauxRemuneration(new BigDecimal("3.5000"));

        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumCompte("SN-EPG-001");
        compte.setSolde(new BigDecimal("1000000.00"));

        epargne = new Epargne();
        epargne.setIdEpargne(1L);
        epargne.setCompte(compte);
        epargne.setSoldeActuel(new BigDecimal("500000.00"));
        epargne.setTauxRemuneration(new BigDecimal("3.5000"));
        epargne.setProduitEpargne(produit);

        dat = new DepotATerme();
        dat.setIdDepotATerme(1L);
        dat.setCompte(compte);
        dat.setMontant(new BigDecimal("1000000.00"));
        dat.setDureeMois(12);
        dat.setTauxInteret(new BigDecimal("6.5000"));
        dat.setDateEcheance(LocalDate.now().plusMonths(12));
        dat.setStatut("ACTIF");
    }

    @Test
    void calculInteretsEpargne() {
        BigDecimal solde = epargne.getSoldeActuel();
        BigDecimal tauxAnnuel = epargne.getTauxRemuneration();
        BigDecimal interetMensuel = solde
            .multiply(tauxAnnuel)
            .divide(new BigDecimal("1200"), 2, RoundingMode.HALF_UP);

        assertThat(interetMensuel).isEqualByComparingTo(new BigDecimal("1458.33"));
    }

    @Test
    void depotAugmenteSoldeEpargne() {
        BigDecimal depot = new BigDecimal("100000.00");
        BigDecimal nouveauSolde = epargne.getSoldeActuel().add(depot);

        assertThat(nouveauSolde).isEqualByComparingTo(new BigDecimal("600000.00"));
    }

    @Test
    void retraitDiminueSoldeEpargne() {
        BigDecimal retrait = new BigDecimal("200000.00");
        BigDecimal nouveauSolde = epargne.getSoldeActuel().subtract(retrait);

        assertThat(nouveauSolde).isEqualByComparingTo(new BigDecimal("300000.00"));
    }

    @Test
    void datCalculInteretsEcheance() {
        BigDecimal montant = dat.getMontant();
        BigDecimal tauxAnnuel = dat.getTauxInteret();
        int dureeMois = dat.getDureeMois();

        BigDecimal interets = montant
            .multiply(tauxAnnuel)
            .multiply(BigDecimal.valueOf(dureeMois))
            .divide(new BigDecimal("1200"), 2, RoundingMode.HALF_UP);

        BigDecimal montantTotalEcheance = montant.add(interets);

        assertThat(interets).isEqualByComparingTo(new BigDecimal("65000.00"));
        assertThat(montantTotalEcheance).isEqualByComparingTo(new BigDecimal("1065000.00"));
    }

    @Test
    void datClotureAnticipeeAppliquePenalite() {
        BigDecimal montant = dat.getMontant();
        BigDecimal penaliteTaux = new BigDecimal("0.02");
        BigDecimal penalite = montant.multiply(penaliteTaux).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montantRembourse = montant.subtract(penalite);

        assertThat(penalite).isEqualByComparingTo(new BigDecimal("20000.00"));
        assertThat(montantRembourse).isEqualByComparingTo(new BigDecimal("980000.00"));
    }
}
