package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.config.EodBatchConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EodBatchServiceTest {

    @Mock private JobLauncher jobLauncher;
    @Mock private org.springframework.batch.core.Job eodJob;
    @Mock private JobExplorer jobExplorer;

    @InjectMocks
    private EodBatchService eodBatchService;

    @Test
    void getJobExecutionStatus_whenNoExecution_shouldReturnEmpty() {
        Optional<JobExecution> statut = eodBatchService.getJobExecutionStatus(null);
        assertNotNull(statut);
        assertFalse(statut.isPresent());
    }

    @Test
    void runEndOfDay_withValidDates_shouldNotThrow() throws Exception {
        JobExecution execution = new JobExecution(1L, new JobParameters());
        when(jobLauncher.run(any(), any())).thenReturn(execution);
        assertDoesNotThrow(() -> eodBatchService.runEndOfDay(LocalDate.now(), LocalDate.now()));
    }
}
