package com.microfinance.core_banking.service.extension.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.LoanFacility;
import com.microfinance.core_banking.repository.extension.LoanFacilityRepository;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CreateLoanFacilityHandler implements PendingActionHandler {
    private final LoanFacilityRepository loanFacilityRepository;
    private final ObjectMapper objectMapper;
    public CreateLoanFacilityHandler(LoanFacilityRepository loanFacilityRepository, ObjectMapper objectMapper) {
        this.loanFacilityRepository = loanFacilityRepository;
        this.objectMapper = objectMapper;
    }
    @Override
    public String execute(ActionEnAttente action, Map<String, Object> payload) {
        if (action.getReferenceRessource() != null) {
            Long loanId = Long.valueOf(action.getReferenceRessource());
            LoanFacility existing = loanFacilityRepository.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("LoanFacility introuvable: " + loanId));
            LoanFacility updated = objectMapper.convertValue(payload, LoanFacility.class);
            existing.setCustomerId(updated.getCustomerId());
            existing.setProductId(updated.getProductId());
            existing.setPrincipalAmount(updated.getPrincipalAmount());
            existing.setOutstandingBalance(updated.getOutstandingBalance());
            existing.setInterestRate(updated.getInterestRate());
            existing.setTermMonths(updated.getTermMonths());
            existing.setStartDate(updated.getStartDate());
            existing.setEndDate(updated.getEndDate());
            if (updated.getStatus() != null) {
                existing.setStatus(updated.getStatus());
            }
            LoanFacility saved = loanFacilityRepository.save(existing);
            return String.valueOf(saved.getId());
        } else {
            LoanFacility toCreate = objectMapper.convertValue(payload, LoanFacility.class);
            LoanFacility saved = loanFacilityRepository.save(toCreate);
            return String.valueOf(saved.getId());
        }
    }
    @Override
    public String getTypeAction() { return "CREATE_LOAN_FACILITY"; }
}
