package com.microfinance.core_banking.service.loan;

import com.microfinance.core_banking.dto.request.loan.LoanFacilityRequestDTO;
import com.microfinance.core_banking.dto.response.loan.LoanFacilityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanFacilityService {

    LoanFacilityResponseDTO create(LoanFacilityRequestDTO request);

    LoanFacilityResponseDTO getById(Long id);

    Page<LoanFacilityResponseDTO> getAll(Pageable pageable);

    LoanFacilityResponseDTO update(Long id, LoanFacilityRequestDTO request);

    void delete(Long id);
}
