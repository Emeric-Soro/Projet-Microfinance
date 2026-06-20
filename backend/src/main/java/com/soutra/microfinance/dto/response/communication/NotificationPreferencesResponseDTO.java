package com.soutra.microfinance.dto.response.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesResponseDTO {

    private Long idClient;
    private Boolean pushActif;
    private Boolean smsActif;
    private Boolean emailActif;
    private LocalDateTime updatedAt;
}
