package com.soutra.microfinance.dto.response.statistique;

import java.util.List;
import java.util.Map;

public record DashboardChartsResponseDTO(
        Map<String, Long> repartitionComptes,
        List<ActiviteJournaliereDTO> evolutionActivite
) {}
