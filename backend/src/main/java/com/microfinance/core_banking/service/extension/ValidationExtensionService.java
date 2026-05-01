package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.extension.ActionEnAttenteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ValidationExtensionService {

    private static final Set<String> STATUTS_AUTORISES = Set.of(
            "APPROUVEE",
            "VALIDEE",
            "REJETEE",
            "CORRECTION_DEMANDEE"
    );

    private final ActionEnAttenteRepository actionEnAttenteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PendingActionExecutionService pendingActionExecutionService;
    private final AuthenticatedUserService authenticatedUserService;

    public ValidationExtensionService(
            ActionEnAttenteRepository actionEnAttenteRepository,
            UtilisateurRepository utilisateurRepository,
            PendingActionExecutionService pendingActionExecutionService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.actionEnAttenteRepository = actionEnAttenteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pendingActionExecutionService = pendingActionExecutionService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public ActionEnAttente creerAction(Map<String, Object> payload) {
        Utilisateur maker = payload.get("idMaker") == null
                ? authenticatedUserService.getCurrentUserOrThrow()
                : utilisateurRepository.findById(Long.valueOf(required(payload, "idMaker")))
                .orElseThrow(() -> new EntityNotFoundException("Maker introuvable"));

        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction(required(payload, "typeAction"));
        action.setRessource(required(payload, "ressource"));
        action.setReferenceRessource((String) payload.get("referenceRessource"));
        action.setAncienneValeur((String) payload.get("ancienneValeur"));
        action.setNouvelleValeur((String) payload.get("nouvelleValeur"));
        action.setCommentaireMaker((String) payload.get("commentaireMaker"));
        action.setStatut("EN_ATTENTE");
        action.setMaker(maker);
        return actionEnAttenteRepository.save(action);
    }

    @Transactional
    public ActionEnAttente validerAction(Long idAction, Map<String, Object> payload) {
        ActionEnAttente action = actionEnAttenteRepository.findById(idAction)
                .orElseThrow(() -> new EntityNotFoundException("Action en attente introuvable"));
        Utilisateur checker = authenticatedUserService.getCurrentUserOrThrow();
        if (!"EN_ATTENTE".equalsIgnoreCase(action.getStatut())) {
            throw new IllegalStateException("Seules les actions en attente peuvent etre decidees");
        }
        if (!authenticatedUserService.hasAnyRoleOrPermission(
                new String[]{"ADMIN", "SUPERVISEUR"},
                new String[]{"VALIDATION_DECIDE"}
        )) {
            throw new IllegalStateException("Le checker doit porter un role de validation");
        }
        if (action.getMaker().getIdUser().equals(checker.getIdUser())) {
            throw new IllegalStateException("Le maker ne peut pas valider sa propre action");
        }
        String statutDecision = normalizeDecisionStatus(required(payload, "statut"));
        String commentaireChecker = optional(payload, "commentaireChecker");
        if (Set.of("REJETEE", "CORRECTION_DEMANDEE").contains(statutDecision)
                && (commentaireChecker == null || commentaireChecker.isBlank())) {
            throw new IllegalArgumentException("Un commentaire checker est obligatoire pour un rejet ou un renvoi en correction");
        }
        action.setChecker(checker);
        action.setCommentaireChecker(commentaireChecker);
        action.setStatut(statutDecision);
        action.setDateValidation(LocalDateTime.now());
        if ("APPROUVEE".equalsIgnoreCase(action.getStatut()) || "VALIDEE".equalsIgnoreCase(action.getStatut())) {
            action.setReferenceRessource(pendingActionExecutionService.execute(action));
        }
        return actionEnAttenteRepository.save(action);
    }

    @Transactional(readOnly = true)
    public List<ActionEnAttente> listerActions() {
        return actionEnAttenteRepository.findAll();
    }

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private String optional(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? null : value.toString().trim();
    }

    private String normalizeDecisionStatus(String rawStatus) {
        String normalized = rawStatus.trim().toUpperCase(Locale.ROOT);
        if (!STATUTS_AUTORISES.contains(normalized)) {
            throw new IllegalArgumentException("Statut de decision non supporte: " + rawStatus);
        }
        return normalized;
    }
}
