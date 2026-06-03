package com.soutra.microfinance.repository.operation;

import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.SensEcriture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LigneEcritureRepository extends JpaRepository<LigneEcriture, Long> {

    Page<LigneEcriture> findByCompte_IdCompte(Long idCompte, Pageable pageable);

    Page<LigneEcriture> findBySens(SensEcriture sens, Pageable pageable);

    Page<LigneEcriture> findByCreatedAtBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    List<LigneEcriture> findByCompte_IdCompteAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long idCompte, LocalDateTime dateDebut, LocalDateTime dateFin);
}
