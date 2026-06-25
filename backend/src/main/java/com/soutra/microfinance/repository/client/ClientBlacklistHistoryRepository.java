package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.ClientBlacklistHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientBlacklistHistoryRepository extends JpaRepository<ClientBlacklistHistory, Long> {
    Page<ClientBlacklistHistory> findAllByOrderByDateActionDesc(Pageable pageable);
}
