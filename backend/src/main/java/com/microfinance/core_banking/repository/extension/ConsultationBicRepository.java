package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ConsultationBic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationBicRepository extends JpaRepository<ConsultationBic, Long> {
    List<ConsultationBic> findByClient_IdClientOrderByDateConsultationDesc(Long clientId);
    List<ConsultationBic> findByDateConsultationBetween(LocalDate debut, LocalDate fin);
    List<ConsultationBic> findByStatutBic(String statutBic);
}
