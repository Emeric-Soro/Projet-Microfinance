package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.AffectationUtilisateurAgence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffectationUtilisateurAgenceRepository extends JpaRepository<AffectationUtilisateurAgence, Long> {
    List<AffectationUtilisateurAgence> findByUtilisateur_IdUserOrderByDateDebutDesc(Long idUser);
}

