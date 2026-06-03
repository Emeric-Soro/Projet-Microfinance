package com.soutra.microfinance.repository.exception;

import com.soutra.microfinance.entity.Derogation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DerogationRepository extends JpaRepository<Derogation, Long> {

    Optional<Derogation> findByReference(String reference);

    List<Derogation> findByStatutOrderByDateCreationDesc(String statut);

    List<Derogation> findAllByOrderByDateCreationDesc();

    Page<Derogation> findAllByOrderByDateCreationDesc(Pageable pageable);
}
