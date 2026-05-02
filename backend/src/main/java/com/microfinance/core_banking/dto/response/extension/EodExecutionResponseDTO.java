package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EodExecutionResponseDTO {
    private Long executionId;
    private String status;
    private String exitCode;
    private String exitDescription;
    private String jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdated;
    private List<EodStepResponseDTO> steps;
}
