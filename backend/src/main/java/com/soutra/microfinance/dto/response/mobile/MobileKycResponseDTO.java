package com.soutra.microfinance.dto.response.mobile;

import java.time.LocalDate;
import java.util.List;

public record MobileKycResponseDTO(
        String statutKyc,
        String niveauKyc,
        LocalDate dateExpirationPiece,
        List<String> documentsFournis
) {}
