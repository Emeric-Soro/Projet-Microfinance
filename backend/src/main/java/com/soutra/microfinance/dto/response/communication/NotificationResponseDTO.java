package com.soutra.microfinance.dto.response.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private String message;
    private String typeNotification;
    private LocalDate dateEnvoi;
    private String statutEnvoi;
    private String erreurEnvoi;
    private Long clientId;
    private LocalDateTime createdAt;
    private Boolean lu;
    private LocalDateTime lueLe;
}
