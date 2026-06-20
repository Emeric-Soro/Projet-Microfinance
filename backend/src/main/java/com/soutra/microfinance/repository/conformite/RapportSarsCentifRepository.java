package com.soutra.microfinance.repository.conformite;

import com.soutra.microfinance.entity.conformite.RapportSarsCentif;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RapportSarsCentifRepository extends JpaRepository<RapportSarsCentif, Long> {

    Page<RapportSarsCentif> findByStatut(String statut, Pageable pageable);

    Page<RapportSarsCentif> findByDateCreationBetween(LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);
}
