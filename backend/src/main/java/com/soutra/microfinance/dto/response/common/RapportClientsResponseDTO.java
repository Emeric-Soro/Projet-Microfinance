package com.soutra.microfinance.dto.response.common;

import java.util.Map;

public record RapportClientsResponseDTO(
        String periode,
        long totalClients,
        long nouveauxClients,
        long clientsActifs,
        Map<String, Long> clientsParStatut
) {}
