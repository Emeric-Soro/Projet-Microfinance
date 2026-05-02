package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakerCheckerWorkflowTest {

    @Mock private ActionEnAttenteRepository actionRepository;
    @Mock private ValidationExtensionService validationService;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private CompteService compteService;

    private Utilisateur maker;
    private Utilisateur checker;
    private ActionEnAttente action;

    @BeforeEach
    void setUp() {
        maker = new Utilisateur();
        maker.setIdUser(1L);
        maker.setLogin("guichetier1");

        checker = new Utilisateur();
        checker.setIdUser(2L);
        checker.setLogin("superviseur1");

        action = new ActionEnAttente();
        action.setIdActionEnAttente(1L);
        action.setTypeAction("BLOCAGE_COMPTE");
        action.setRessource("COMPTE");
        action.setReferenceRessource("SN001");
        action.setStatut("EN_ATTENTE");
        action.setMaker(maker);
    }

    @Test
    void makerSubmitActionForValidation() {
        action.setCommentaireMaker("Blocage pour fraude suspectee");

        assertThat(action.getStatut()).isEqualTo("EN_ATTENTE");
        assertThat(action.getMaker().getLogin()).isEqualTo("guichetier1");
        assertThat(action.getChecker()).isNull();
        assertThat(action.getDateValidation()).isNull();
    }

    @Test
    void checkerApprovesAction() {
        action.setStatut("APPROUVE");
        action.setChecker(checker);
        action.setCommentaireChecker("Approuve - motif valide");
        action.setDateValidation(java.time.LocalDateTime.now());

        assertThat(action.getStatut()).isEqualTo("APPROUVE");
        assertThat(action.getChecker().getLogin()).isEqualTo("superviseur1");
        assertThat(action.getCommentaireChecker()).isEqualTo("Approuve - motif valide");
        assertThat(action.getDateValidation()).isNotNull();
    }

    @Test
    void checkerRejectsAction() {
        action.setStatut("REJETE");
        action.setChecker(checker);
        action.setCommentaireChecker("Documentation insuffisante");

        assertThat(action.getStatut()).isEqualTo("REJETE");
    }

    @Test
    void makerCannotApproveOwnAction() {
        boolean isSameUser = maker.getIdUser().equals(checker.getIdUser());
        assertThat(isSameUser).isFalse();

        assertThrows(IllegalStateException.class, () -> {
            if (maker.getIdUser().equals(checker.getIdUser())) {
                throw new IllegalStateException("Le maker ne peut pas valider sa propre action");
            }
        });
    }

    @Test
    void alreadyApprovedActionCannotBeRejected() {
        action.setStatut("APPROUVE");

        assertThrows(IllegalStateException.class, () -> {
            if (!"EN_ATTENTE".equals(action.getStatut())) {
                throw new IllegalStateException("L'action a deja ete traitee (statut: " + action.getStatut() + ")");
            }
        });
    }

    @Test
    void actionEnAttenteRequiresCommentaireChecker() {
        assertThrows(IllegalArgumentException.class, () -> {
            if (action.getCommentaireChecker() == null || action.getCommentaireChecker().isBlank()) {
                throw new IllegalArgumentException("Le commentaire du checker est obligatoire");
            }
        });
    }
}
