package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.dto.request.extension.CreerCritereScoringRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ScoringExtensionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateCritereScoringHandler implements PendingActionHandler {
    private final ScoringExtensionService scoringExtensionService;
    private final ObjectMapper objectMapper;
    public CreateCritereScoringHandler(ScoringExtensionService scoringExtensionService, ObjectMapper objectMapper) {
        this.scoringExtensionService = scoringExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerCritereScoringRequestDTO dto = objectMapper.convertValue(payload, CreerCritereScoringRequestDTO.class);
        return String.valueOf(scoringExtensionService.creerCritere(dto).getIdCritereScoring());
    }
    @Override
    public String getTypeAction() { return "CREATE_CRITERE_SCORING"; }
}
