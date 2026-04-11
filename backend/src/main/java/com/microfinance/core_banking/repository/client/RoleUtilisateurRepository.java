package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.RoleUtilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RoleUtilisateurRepository extends JpaRepository<RoleUtilisateur, Long> {

	// Recherche unique par code role metier.
	Optional<RoleUtilisateur> findByCodeRoleUtilisateur(String codeRoleUtilisateur);
	// Verification rapide de si un role existe par code.
	boolean existsByCodeRoleUtilisateur(String codeRoleUtilisateur);

}
