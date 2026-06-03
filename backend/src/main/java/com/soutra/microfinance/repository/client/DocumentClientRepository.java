package com.soutra.microfinance.repository.client;

import com.soutra.microfinance.entity.DocumentClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentClientRepository extends JpaRepository<DocumentClient, Long> {

    Page<DocumentClient> findByIdClientOrderByDateUploadDesc(Long idClient, Pageable pageable);
}
