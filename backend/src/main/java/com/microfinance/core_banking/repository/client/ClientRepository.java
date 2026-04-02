package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
