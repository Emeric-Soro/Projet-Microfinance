package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;
import java.util.List;

public record MobileDashboardResponseDTO(
        BigDecimal soldeTotal,
        int nbComptes,
        List<String> dernieresOperations,
        int nbNotificationsNonLues,
        List<String> alertes
) {}
