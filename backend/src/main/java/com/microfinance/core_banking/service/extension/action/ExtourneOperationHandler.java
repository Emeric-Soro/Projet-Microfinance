package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.operation.ActionTransactionRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.operation.TransactionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtourneOperationHandler implements PendingActionHandler {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public ExtourneOperationHandler(TransactionService transactionService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ActionTransactionRequestDTO dto = objectMapper.convertValue(payload, ActionTransactionRequestDTO.class);
        return transactionService.extournerTransaction(action.getReferenceRessource(), action.getChecker().getIdUser(), dto.getMotif()).getReferenceUnique();
    }

    @Override
    public String getTypeAction() {
        return "EXTOURNE_OPERATION";
    }
}
