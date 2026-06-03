package com.soutra.microfinance.dto.response.mobile;

import java.time.LocalDateTime;

public record MobileNotificationResponseDTO(
        Long id,
        String type,
        String message,
        Boolean lue,
        LocalDateTime dateCreation
) {}
