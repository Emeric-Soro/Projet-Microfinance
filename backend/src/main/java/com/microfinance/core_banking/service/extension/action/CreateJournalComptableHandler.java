package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.CreerJournalComptableRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateJournalComptableHandler implements PendingActionHandler {
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final ObjectMapper objectMapper;
    public CreateJournalComptableHandler(ComptabiliteExtensionService comptabiliteExtensionService, ObjectMapper objectMapper) {
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        CreerJournalComptableRequestDTO dto = objectMapper.convertValue(payload, CreerJournalComptableRequestDTO.class);
        return String.valueOf(comptabiliteExtensionService.creerJournal(dto).getIdJournalComptable());
    }
    @Override
    public String getTypeAction() { return "CREATE_JOURNAL_COMPTABLE"; }
}
