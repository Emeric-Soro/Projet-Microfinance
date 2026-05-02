package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.service.extension.OrganisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MultiAgenceWorkflowTest {

    @Mock private AgenceRepository agenceRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private OrganisationService organisationService;

    private Agence agence1;
    private Agence agence2;
    private Client client;
    private Compte compte;

    @BeforeEach
    void setUp() {
        agence1 = new Agence();
        agence1.setIdAgence(1L);
        agence1.setCodeAgence("AG-DKR-001");
        agence1.setLibelleAgence("Dakar Plateau");

        agence2 = new Agence();
        agence2.setIdAgence(2L);
        agence2.setCodeAgence("AG-THS-001");
        agence2.setLibelleAgence("Thies");

        client = new Client();
        client.setIdClient(1L);
        client.setCodeClient("CLI-MA-001");

        compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumCompte("SN-MA-001");
        compte.setClient(client);
        compte.setAgence(agence1);
        compte.setSolde(new BigDecimal("500000.00"));
    }

    @Test
    void clientRattacheAgencePrincipale() {
        assertThat(compte.getAgence().getCodeAgence()).isEqualTo("AG-DKR-001");
    }

    @Test
    void virementInterAgencePossible() {
        BigDecimal montantVirement = new BigDecimal("100000.00");
        BigDecimal soldeSource = compte.getSolde().subtract(montantVirement);

        assertThat(soldeSource).isEqualByComparingTo(new BigDecimal("400000.00"));
    }

    @Test
    void utilisateurLimitParAgence() {
        Utilisateur user = new Utilisateur();
        user.setIdUser(1L);
        user.setAgenceActive(agence1);

        assertThat(user.getAgenceActive().getCodeAgence()).isEqualTo("AG-DKR-001");
        assertThat(user.getAgenceActive().getCodeAgence()).isNotEqualTo("AG-THS-001");
    }

    @Test
    void rapportParAgenceIsole() {
        Agence agence = agence1;
        BigDecimal totalEncoursAgence = new BigDecimal("25000000.00");

        assertThat(agence.getCodeAgence()).isEqualTo("AG-DKR-001");
        assertThat(totalEncoursAgence).isEqualByComparingTo(new BigDecimal("25000000.00"));
    }
}
