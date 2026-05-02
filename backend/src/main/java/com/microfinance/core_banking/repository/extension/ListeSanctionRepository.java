package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.ListeSanction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListeSanctionRepository extends JpaRepository<ListeSanction, Long> {
    List<ListeSanction> findByActifTrue();
    List<ListeSanction> findByNomCompletContainingIgnoreCase(String nom);
    List<ListeSanction> findByTypeSanction(String typeSanction);
    List<ListeSanction> findByTypePersonne(String typePersonne);
    boolean existsByNomCompletIgnoreCase(String nomComplet);
}
