package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.HistoriqueMotDePasseUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueMotDePasseUtilisateurRepository extends JpaRepository<HistoriqueMotDePasseUtilisateur, Long> {

    List<HistoriqueMotDePasseUtilisateur> findByUtilisateur_IdUserOrderByDateChangementDesc(Long idUser);
}
