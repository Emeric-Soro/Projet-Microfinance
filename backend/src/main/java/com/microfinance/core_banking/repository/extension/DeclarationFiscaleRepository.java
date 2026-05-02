package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.DeclarationFiscale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DeclarationFiscaleRepository extends JpaRepository<DeclarationFiscale, Long> {
    List<DeclarationFiscale> findByTypeDeclarationAndPeriodeDebutGreaterThanEqualAndPeriodeFinLessThanEqual(
            String type, LocalDate debut, LocalDate fin);
    List<DeclarationFiscale> findByStatut(String statut);
}
