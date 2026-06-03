package com.soutra.microfinance.repository.conformite;

import com.soutra.microfinance.entity.conformite.Reclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    Page<Reclamation> findByStatut(String statut, Pageable pageable);

    List<Reclamation> findByIdClient(Long idClient);
}
