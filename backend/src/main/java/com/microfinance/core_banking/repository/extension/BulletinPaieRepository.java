package com.microfinance.core_banking.repository.extension;

import com.microfinance.core_banking.entity.BulletinPaie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BulletinPaieRepository extends JpaRepository<BulletinPaie, Long> {
    List<BulletinPaie> findByEmploye_IdEmployeOrderByCreatedAtDesc(Long idEmploye);
}
