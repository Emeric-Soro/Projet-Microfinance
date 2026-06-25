package com.soutra.microfinance.service.client;

import com.soutra.microfinance.dto.request.client.AddBlacklistRequestDTO;
import com.soutra.microfinance.dto.request.client.RemoveBlacklistRequestDTO;
import com.soutra.microfinance.entity.ClientBlacklist;
import com.soutra.microfinance.entity.ClientBlacklistHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientBlacklistService {
    Page<ClientBlacklist> listerBlacklist(Pageable pageable);
    Page<ClientBlacklistHistory> obtenirHistorique(Pageable pageable);
    ClientBlacklist ajouterABlacklist(Long idClient, AddBlacklistRequestDTO requestDTO, String operateur);
    void retirerDeBlacklist(Long idClient, RemoveBlacklistRequestDTO requestDTO, String operateur);
}
