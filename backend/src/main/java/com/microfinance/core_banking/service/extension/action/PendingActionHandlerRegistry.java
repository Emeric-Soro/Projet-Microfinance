package com.microfinance.core_banking.service.extension.action;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PendingActionHandlerRegistry {
    private final Map<String, PendingActionHandler> handlerMap;

    public PendingActionHandlerRegistry(List<PendingActionHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(PendingActionHandler::getTypeAction, Function.identity()));
    }

    public PendingActionHandler getHandler(String typeAction) {
        PendingActionHandler handler = handlerMap.get(typeAction);
        if (handler == null) {
            throw new IllegalArgumentException("Type d'action non supporte: " + typeAction);
        }
        return handler;
    }
}
