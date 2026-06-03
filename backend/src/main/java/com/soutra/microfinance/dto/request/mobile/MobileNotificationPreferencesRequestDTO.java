package com.soutra.microfinance.dto.request.mobile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileNotificationPreferencesRequestDTO {

    private Boolean pushActif;
    private Boolean smsActif;
    private Boolean emailActif;
}
