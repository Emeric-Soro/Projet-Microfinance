package com.microfinance.core_banking.service.beneficiary;

import com.microfinance.core_banking.dto.request.beneficiary.BeneficiaryRequestDTO;
import com.microfinance.core_banking.dto.response.beneficiary.BeneficiaryResponseDTO;
import com.microfinance.core_banking.entity.Beneficiary;
import com.microfinance.core_banking.mapper.BeneficiaryMapper;
import com.microfinance.core_banking.repository.extension.BeneficiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceImplTest {

    @Mock private BeneficiaryRepository beneficiaryRepository;
    @Mock private BeneficiaryMapper beneficiaryMapper;

    @InjectMocks
    private BeneficiaryServiceImpl beneficiaryService;

    @Test
    void ajouterBeneficiaire_withValidData_shouldSucceed() {
        BeneficiaryRequestDTO request = new BeneficiaryRequestDTO();
        request.setLoanFacilityId(1L);
        request.setBeneficiaryAccount("CPT-002");
        request.setBeneficiaryName("Martin");

        Beneficiary entity = new Beneficiary();
        entity.setId(1L);
        entity.setLoanFacilityId(1L);
        entity.setBeneficiaryAccount("CPT-002");
        entity.setBeneficiaryName("Martin");

        BeneficiaryResponseDTO response = new BeneficiaryResponseDTO();
        response.setId(1L);
        response.setLoanFacilityId(1L);
        response.setBeneficiaryAccount("CPT-002");
        response.setBeneficiaryName("Martin");

        when(beneficiaryMapper.toEntity(request)).thenReturn(entity);
        when(beneficiaryRepository.save(any(Beneficiary.class))).thenReturn(entity);
        when(beneficiaryMapper.toResponse(entity)).thenReturn(response);

        BeneficiaryResponseDTO resultat = beneficiaryService.create(request);

        assertNotNull(resultat);
        assertEquals("Martin", resultat.getBeneficiaryName());
        assertEquals("CPT-002", resultat.getBeneficiaryAccount());
    }

    @Test
    void ajouterBeneficiaire_withNullRequest_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> beneficiaryService.create(null));
    }
}
