package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.TesterSchemaComptableRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ControlesComptablesResponseDTO;
import com.microfinance.core_banking.dto.response.extension.SchemaTestResponseDTO;
import com.microfinance.core_banking.entity.ClasseComptable;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.JournalComptable;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.SchemaComptable;
import com.microfinance.core_banking.service.DoubleEntryService;
import com.microfinance.core_banking.service.DoubleEntryServiceImpl;
import com.microfinance.core_banking.service.tarification.AgioService;
import org.springframework.beans.factory.ObjectProvider;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.ClasseComptableRepository;
import com.microfinance.core_banking.repository.extension.ClotureComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import com.microfinance.core_banking.repository.extension.EcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.JournalComptableRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.ProvisionCreditRepository;
import com.microfinance.core_banking.repository.extension.RemboursementCreditRepository;
import com.microfinance.core_banking.repository.extension.SchemaComptableRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComptabiliteExtensionServiceTest {

    @Mock
    private ClasseComptableRepository classeComptableRepository;
    @Mock
    private CompteComptableRepository compteComptableRepository;
    @Mock
    private JournalComptableRepository journalComptableRepository;
    @Mock
    private SchemaComptableRepository schemaComptableRepository;
    @Mock
    private EcritureComptableRepository ecritureComptableRepository;
    @Mock
    private LigneEcritureComptableRepository ligneEcritureComptableRepository;
    @Mock
    private ClotureComptableRepository clotureComptableRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private RemboursementCreditRepository remboursementCreditRepository;
    @Mock
    private ProvisionCreditRepository provisionCreditRepository;
    @Mock
    private AgenceRepository agenceRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private ObjectProvider<CreditExtensionService> creditExtensionServiceProvider;
    @Mock
    private ObjectProvider<PaiementExterneService> paiementExterneServiceProvider;
    @Mock
    private ObjectProvider<AgioService> agioServiceProvider;
    @Spy
    private DoubleEntryService doubleEntryService = new DoubleEntryServiceImpl();

    @InjectMocks
    private ComptabiliteExtensionService comptabiliteExtensionService;

    @Test
    void testerSchemaComptable_renvoieSimulationEquilibree() {
        ClasseComptable classe = new ClasseComptable();
        classe.setCodeClasse("2");
        when(classeComptableRepository.findByCodeClasse(anyString())).thenReturn(Optional.of(classe));

        com.microfinance.core_banking.entity.CompteComptable compte = new com.microfinance.core_banking.entity.CompteComptable();
        compte.setNumeroCompte("251000");
        when(compteComptableRepository.findByNumeroCompte(anyString())).thenReturn(Optional.of(compte));

        JournalComptable journal = new JournalComptable();
        journal.setCodeJournal("CAI");
        when(journalComptableRepository.findByCodeJournal(anyString())).thenReturn(Optional.of(journal));

        SchemaComptable schema = new SchemaComptable();
        schema.setCodeOperation("DEPOT_CASH");
        schema.setCompteDebit("571000");
        schema.setCompteCredit("251000");
        schema.setCompteFrais("701000");
        schema.setJournalCode("CAI");
        when(schemaComptableRepository.findByCodeOperation(anyString())).thenReturn(Optional.of(schema));

        TesterSchemaComptableRequestDTO request = new TesterSchemaComptableRequestDTO();
        request.setCodeOperation("DEPOT_CASH");
        request.setMontant(new BigDecimal("1000.00"));
        request.setFrais(new BigDecimal("25.00"));

        SchemaTestResponseDTO resultat = comptabiliteExtensionService.testerSchemaComptable(request);

        assertEquals(0, new BigDecimal("1025.00").compareTo(resultat.getTotalDebit()));
        assertEquals(0, new BigDecimal("1025.00").compareTo(resultat.getTotalCredit()));
        assertTrue(resultat.getEquilibree());
        assertEquals(3, resultat.getLignes().size());
    }

    @Test
    void controlesComptables_detecteEcritureDesequilibree() {
        LocalDate debut = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 1, 31);

        EcritureComptable ecriture1 = new EcritureComptable();
        ecriture1.setIdEcritureComptable(1L);
        ecriture1.setReferencePiece("PC-1");
        ecriture1.setDateComptable(LocalDate.of(2026, 1, 10));

        EcritureComptable ecriture2 = new EcritureComptable();
        ecriture2.setIdEcritureComptable(2L);
        ecriture2.setReferencePiece("PC-2");
        ecriture2.setDateComptable(LocalDate.of(2026, 1, 11));

        LigneEcritureComptable ligneDebit1 = new LigneEcritureComptable();
        ligneDebit1.setEcritureComptable(ecriture1);
        ligneDebit1.setSens("DEBIT");
        ligneDebit1.setMontant(new BigDecimal("100.00"));

        LigneEcritureComptable ligneCredit1 = new LigneEcritureComptable();
        ligneCredit1.setEcritureComptable(ecriture1);
        ligneCredit1.setSens("CREDIT");
        ligneCredit1.setMontant(new BigDecimal("100.00"));

        LigneEcritureComptable ligneDebit2 = new LigneEcritureComptable();
        ligneDebit2.setEcritureComptable(ecriture2);
        ligneDebit2.setSens("DEBIT");
        ligneDebit2.setMontant(new BigDecimal("50.00"));

        when(ecritureComptableRepository.findByDateComptableBetween(debut, fin)).thenReturn(List.of(ecriture1, ecriture2));
        when(ligneEcritureComptableRepository.findByEcritureComptable_DateComptableBetween(debut, fin))
                .thenReturn(List.of(ligneDebit1, ligneCredit1, ligneDebit2));

        ControlesComptablesResponseDTO resultat = comptabiliteExtensionService.controlesComptables(debut, fin);

        assertEquals(2, resultat.getTotalEcritures());
        assertEquals(3, resultat.getTotalLignes());
        assertFalse(resultat.getEquilibreGlobal());
        assertEquals(0, resultat.getEcrituresSansLignes());
        assertEquals(1, resultat.getEcrituresDesequilibrees().size());
        assertEquals("PC-2", resultat.getEcrituresDesequilibrees().get(0).getReferencePiece());
    }

    @Test
    void calculerSoldeComptable_returnsBalanceFromLedgerLines() {
        LigneEcritureComptable ligneDebit = new LigneEcritureComptable();
        ligneDebit.setSens("DEBIT");
        ligneDebit.setMontant(new BigDecimal("1000.00"));

        LigneEcritureComptable ligneCredit = new LigneEcritureComptable();
        ligneCredit.setSens("CREDIT");
        ligneCredit.setMontant(new BigDecimal("1500.00"));

        when(ligneEcritureComptableRepository.findByReferenceAuxiliaire("CPT-001"))
                .thenReturn(List.of(ligneDebit, ligneCredit));

        BigDecimal solde = comptabiliteExtensionService.calculerSoldeComptable("CPT-001");

        assertEquals(0, new BigDecimal("500.00").compareTo(solde));
    }
}
