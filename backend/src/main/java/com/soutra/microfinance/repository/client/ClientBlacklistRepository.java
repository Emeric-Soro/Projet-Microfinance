package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.ClientBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientBlacklistRepository extends JpaRepository<ClientBlacklist, Long> {
    Optional<ClientBlacklist> findByClient_IdClient(Long idClient);
    boolean existsByClient_IdClient(Long idClient);
}
