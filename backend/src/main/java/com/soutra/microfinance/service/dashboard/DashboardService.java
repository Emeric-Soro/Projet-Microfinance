package com.soutra.microfinance.service.dashboard;

import com.soutra.microfinance.dto.response.statistique.DashboardAgenceResponseDTO;
import com.soutra.microfinance.dto.response.statistique.DashboardDirectionResponseDTO;
import com.soutra.microfinance.dto.response.statistique.IndicateurTempsReelResponseDTO;
import com.soutra.microfinance.dto.response.statistique.DashboardChartsResponseDTO;

public interface DashboardService {

    DashboardAgenceResponseDTO getKpisAgence(Long agenceId, String periode);

    DashboardDirectionResponseDTO getKpisDirection();

    IndicateurTempsReelResponseDTO getIndicateursTempsReel();

    DashboardChartsResponseDTO getGraphiques(Long agenceId);
}
