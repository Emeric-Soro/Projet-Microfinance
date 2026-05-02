package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerGrilleScoringRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ScoringExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateGrilleScoringHandler implements PendingActionHandler {
    private final ScoringExtensionService scoringExtensionService;
    private final ObjectMapper objectMapper;
    public CreateGrilleScoringHandler(ScoringExtensionService scoringExtensionService, ObjectMapper objectMapper) {
        this.scoringExtensionService = scoringExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerGrilleScoringRequestDTO dto = objectMapper.convertValue(payload, CreerGrilleScoringRequestDTO.class);
        return String.valueOf(scoringExtensionService.creerGrille(dto).getIdGrilleScoring());
    }
    @Override
    public String getTypeAction() { return "CREATE_GRILLE_SCORING"; }
}
