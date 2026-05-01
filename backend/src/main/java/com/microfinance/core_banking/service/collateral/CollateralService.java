package com.microfinance.core_banking.service.collateral;

import com.microfinance.core_banking.dto.request.collateral.CollateralRequestDTO;
import com.microfinance.core_banking.dto.response.collateral.CollateralResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollateralService {
    CollateralResponseDTO create(CollateralRequestDTO request);
    CollateralResponseDTO getById(Long id);
    Page<CollateralResponseDTO> getAll(Pageable pageable);
    CollateralResponseDTO update(Long id, CollateralRequestDTO request);
    void delete(Long id);
}
