package com.microfinance.core_banking.service.communication.event;

import java.math.BigDecimal;

public record VirementEffectueEvent(String numCompteDestination, BigDecimal montant) {
}
