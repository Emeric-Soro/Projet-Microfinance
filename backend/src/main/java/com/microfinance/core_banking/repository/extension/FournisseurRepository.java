package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findByCodeFournisseur(String codeFournisseur);
}
