package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.WalletClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletClientRepository extends JpaRepository<WalletClient, Long> {
    Optional<WalletClient> findByNumeroWallet(String numeroWallet);
    List<WalletClient> findByClient_IdClient(Long idClient);
}
