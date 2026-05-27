package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.dto.request.operation.OuvertureCaisseRequestDTO;
import com.soutra.microfinance.dto.request.operation.FermetureCaisseRequestDTO;
import com.soutra.microfinance.entity.Caisse;
import org.springframework.transaction.annotation.Transactional;

public interface CaisseService {

    Caisse ouvrirCaisse(Long idUser, OuvertureCaisseRequestDTO dto);

    Caisse fermerCaisse(Long idUser, FermetureCaisseRequestDTO dto);

    @Transactional(readOnly = true)
    Caisse consulterCaisseOuverte(Long idUser);
}
