package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerWalletRequestDTO {
    @NotBlank(message = "Le code wallet est obligatoire")
    @Size(max = 20)
    private String codeWallet;

    @NotNull(message = "L'id operateur est obligatoire")
    private Long idOperateur;

    @NotNull(message = "L'id client est obligatoire")
    private Long idClient;

    @Positive
    private BigDecimal soldeInitial;

    @Size(max = 10)
    private String devise;

    @Size(max = 20)
    private String statut;
}
