package com.soutra.microfinance.repository.exception;

import com.soutra.microfinance.entity.Escalade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EscaladeRepository extends JpaRepository<Escalade, Long> {

    Optional<Escalade> findByReference(String reference);

    List<Escalade> findByStatutOrderByDateCreationDesc(String statut);

    List<Escalade> findByNiveauOrderByDateCreationDesc(String niveau);

    List<Escalade> findAllByOrderByDateCreationDesc();

    Page<Escalade> findAllByOrderByDateCreationDesc(Pageable pageable);
}
