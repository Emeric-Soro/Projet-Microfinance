package com.microfinance.core_banking.service.beneficiary;

import com.microfinance.core_banking.dto.request.beneficiary.BeneficiaryRequestDTO;
import com.microfinance.core_banking.dto.response.beneficiary.BeneficiaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BeneficiaryService {
    BeneficiaryResponseDTO create(BeneficiaryRequestDTO request);
    BeneficiaryResponseDTO getById(Long id);
    Page<BeneficiaryResponseDTO> getAll(Pageable pageable);
    BeneficiaryResponseDTO update(Long id, BeneficiaryRequestDTO request);
    void delete(Long id);
}
