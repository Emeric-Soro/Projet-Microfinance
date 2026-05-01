package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.repository.extension.ActionEnAttenteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingActionSubmissionService {

    private final ActionEnAttenteRepository actionEnAttenteRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ObjectMapper objectMapper;

    public PendingActionSubmissionService(
            ActionEnAttenteRepository actionEnAttenteRepository,
            AuthenticatedUserService authenticatedUserService,
            ObjectMapper objectMapper
    ) {
        this.actionEnAttenteRepository = actionEnAttenteRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ActionEnAttente submit(String typeAction, String ressource, String referenceRessource, Object payload, String commentaireMaker) {
        ActionEnAttente action = new ActionEnAttente();
        action.setTypeAction(typeAction);
        action.setRessource(ressource);
        action.setReferenceRessource(referenceRessource);
        action.setCommentaireMaker(commentaireMaker);
        action.setStatut("EN_ATTENTE");
        action.setMaker(authenticatedUserService.getCurrentUserOrThrow());
        action.setNouvelleValeur(toJson(payload));
        return actionEnAttenteRepository.save(action);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Impossible de serialiser l'action en attente", exception);
        }
    }
}

