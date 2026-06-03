package com.soutra.microfinance.dto.request.communication;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesRequestDTO {

    @NotNull(message = "Le canal push doit etre defini (actif ou inactif)")
    private Boolean pushActif;

    @NotNull(message = "Le canal SMS doit etre defini (actif ou inactif)")
    private Boolean smsActif;

    @NotNull(message = "Le canal email doit etre defini (actif ou inactif)")
    private Boolean emailActif;
}
