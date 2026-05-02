package com.microfinance.core_banking.service.extension.action;

import com.microfinance.core_banking.entity.ActionEnAttente;
import java.util.Map;

public interface PendingActionHandler {
    String execute(ActionEnAttente action, Map<String, Object> payload);
    String getTypeAction();
}
