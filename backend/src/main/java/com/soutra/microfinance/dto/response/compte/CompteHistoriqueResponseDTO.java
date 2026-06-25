package com.soutra.microfinance.dto.response.compte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteHistoriqueResponseDTO {
    private LocalDateTime date;
    private String type;
    private String description;
}
