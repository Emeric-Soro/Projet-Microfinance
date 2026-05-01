package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.MutationPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MutationPersonnelRepository extends JpaRepository<MutationPersonnel, Long> {
    List<MutationPersonnel> findByEmploye_IdEmployeOrderByDateMutationDesc(Long idEmploye);
    List<MutationPersonnel> findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateMutationDesc(Long idAgenceSource, Long idAgenceDestination);
}

