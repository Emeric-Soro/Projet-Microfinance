package com.microfinance.core_banking.repository.credit;

import com.microfinance.core_banking.entity.Garantie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarantieRepository extends JpaRepository<Garantie, Long> {

	// Liste les garanties d'un credit.
	List<Garantie> findByCredit_IdCredit(Long idCredit);

	// Liste les garanties actives d'un credit.
	List<Garantie> findByCredit_IdCreditAndEstActiveTrue(Long idCredit);
}
