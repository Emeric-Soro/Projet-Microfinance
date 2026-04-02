package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
}
