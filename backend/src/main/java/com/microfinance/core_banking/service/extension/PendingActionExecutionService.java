package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.action.PendingActionHandler;
import com.microfinance.core_banking.service.extension.action.PendingActionHandlerRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PendingActionExecutionService {

    private final ObjectMapper objectMapper;
    private final PendingActionHandlerRegistry handlerRegistry;

    public PendingActionExecutionService(ObjectMapper objectMapper, PendingActionHandlerRegistry handlerRegistry) {
        this.objectMapper = objectMapper;
        this.handlerRegistry = handlerRegistry;
    }

    public String execute(ActionEnAttente action) {
        Map<String, Object> payload = readPayload(action.getNouvelleValeur());
        PendingActionHandler handler = handlerRegistry.getHandler(action.getTypeAction());
        return handler.execute(action, payload);
    }

    private Map<String, Object> readPayload(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, new TypeReference<>() { });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Impossible de deserialiser l'action en attente", exception);
        }
    }
}
