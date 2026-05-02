package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportEcheanceCreditHandler implements PendingActionHandler {

    private final CreditExtensionService creditExtensionService;
    private final ObjectMapper objectMapper;

    public ReportEcheanceCreditHandler(CreditExtensionService creditExtensionService, ObjectMapper objectMapper) {
        this.creditExtensionService = creditExtensionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        ReportEcheanceCreditRequestDTO dto = objectMapper.convertValue(payload, ReportEcheanceCreditRequestDTO.class);
        Long idCredit = dto.getIdCredit() != null ? dto.getIdCredit() : Long.valueOf(action.getReferenceRessource());
        Long idEcheanceCredit = dto.getIdEcheanceCredit() != null ? dto.getIdEcheanceCredit() : Long.valueOf(action.getReferenceRessource());
        return String.valueOf(creditExtensionService.reporterEcheance(idCredit, idEcheanceCredit, dto).getIdEcheanceCredit());
    }

    @Override
    public String getTypeAction() {
        return "REPORT_ECHEANCE_CREDIT";
    }
}
