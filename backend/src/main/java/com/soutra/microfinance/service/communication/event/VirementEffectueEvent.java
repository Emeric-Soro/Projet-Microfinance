package com.soutra.microfinance.service.communication.event;

import java.math.BigDecimal;

public record VirementEffectueEvent(String numCompteDestination, BigDecimal montant) {
}
