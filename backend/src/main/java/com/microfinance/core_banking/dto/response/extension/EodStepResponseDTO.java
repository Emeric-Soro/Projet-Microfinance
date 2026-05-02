package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EodStepResponseDTO {
    private String stepName;
    private String status;
    private int readCount;
    private int writeCount;
    private int commitCount;
    private int rollbackCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
