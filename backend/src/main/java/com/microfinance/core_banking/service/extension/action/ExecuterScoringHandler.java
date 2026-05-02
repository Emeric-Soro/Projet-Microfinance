package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.ExecuterScoringRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ScoringExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExecuterScoringHandler implements PendingActionHandler {

    private final ScoringExtensionService scoringExtensionService;
    private final ObjectMapper objectMapper;

    public ExecuterScoringHandler(ScoringExtensionService scoringExtensionService, ObjectMapper objectMapper) {
        this.scoringExtensionService = scoringExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ExecuterScoringRequestDTO dto = objectMapper.convertValue(payload, ExecuterScoringRequestDTO.class);
        return String.valueOf(scoringExtensionService.executerScoring(dto).getIdResultatScoring());
    }

    @Override
    public String getTypeAction() {
        return "EXECUTE_SCORING";
    }
}
