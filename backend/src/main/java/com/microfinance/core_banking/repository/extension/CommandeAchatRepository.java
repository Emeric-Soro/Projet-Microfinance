package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.CommandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeAchatRepository extends JpaRepository<CommandeAchat, Long> {
    List<CommandeAchat> findByAgence_IdAgenceOrderByDateCommandeDesc(Long idAgence);
}
