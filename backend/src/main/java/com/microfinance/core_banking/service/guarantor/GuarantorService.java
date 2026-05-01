package com.microfinance.core_banking.service.guarantor;

import com.microfinance.core_banking.dto.request.guarantor.GuarantorRequestDTO;
import com.microfinance.core_banking.dto.response.guarantor.GuarantorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuarantorService {
    GuarantorResponseDTO create(GuarantorRequestDTO request);
    GuarantorResponseDTO getById(Long id);
    Page<GuarantorResponseDTO> getAll(Pageable pageable);
    GuarantorResponseDTO update(Long id, GuarantorRequestDTO request);
    void delete(Long id);
}
