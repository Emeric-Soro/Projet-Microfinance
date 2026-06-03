package com.soutra.microfinance.dto.request.client;

import com.soutra.microfinance.entity.ReleveFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleveRequestDTO {

    @NotNull(message = "La date de debut est obligatoire")
    private LocalDate du;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate au;

    @NotNull(message = "Le format est obligatoire (PDF ou CSV)")
    private ReleveFormat format;
}
