package com.soutra.microfinance.service.reporting;

import com.soutra.microfinance.dto.request.parametrage.RapportPersonnaliseRequestDTO;
import com.soutra.microfinance.dto.response.common.*;

import java.util.List;

public interface ReportingService {

    RapportOperationnelResponseDTO genererRapportOperationnel(String dateDebut, String dateFin, Long agenceId);

    RapportFinancierResponseDTO genererRapportFinancier();

    RapportClientsResponseDTO genererRapportClients();

    RapportCreditsResponseDTO genererRapportCredits();

    RapportCaisseResponseDTO genererRapportCaisse();

    RapportBceaoResponseDTO genererRapportBceao(int trimestre, int annee);

    RapportExportResponseDTO exporterRapport(String type, String format);

    RapportExportResponseDTO genererRapportPersonnalise(RapportPersonnaliseRequestDTO requestDTO);
}
