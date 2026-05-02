package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ConsentementOpenBanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsentementOpenBankingRepository extends JpaRepository<ConsentementOpenBanking, Long> {
    Optional<ConsentementOpenBanking> findByRefConsentement(String refConsentement);
    List<ConsentementOpenBanking> findByClient_IdClient(Long clientId);
    List<ConsentementOpenBanking> findByPartenaireApi_IdPartenaireApi(Long partenaireId);
    List<ConsentementOpenBanking> findByStatut(String statut);
    List<ConsentementOpenBanking> findByStatutAndDateFinBefore(String statut, LocalDateTime date);
}
