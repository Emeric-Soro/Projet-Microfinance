package com.microfinance.core_banking.service.operation;

import com.microfinance.core_banking.dto.request.operation.OuvertureCaisseRequestDTO;
import com.microfinance.core_banking.dto.request.operation.FermetureCaisseRequestDTO;
import com.microfinance.core_banking.entity.Caisse;
import org.springframework.transaction.annotation.Transactional;

public interface CaisseService {

    Caisse ouvrirCaisse(Long idUser, OuvertureCaisseRequestDTO dto);

    Caisse fermerCaisse(Long idUser, FermetureCaisseRequestDTO dto);

    @Transactional(readOnly = true)
    Caisse consulterCaisseOuverte(Long idUser);
}
