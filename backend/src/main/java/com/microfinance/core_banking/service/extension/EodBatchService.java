package com.microfinance.core_banking.service.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class EodBatchService {

    private static final Logger log = LoggerFactory.getLogger(EodBatchService.class);

    private final JobLauncher jobLauncher;
    private final Job eodJob;
    private final JobExplorer jobExplorer;

    public EodBatchService(JobLauncher jobLauncher, Job eodJob, JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.eodJob = eodJob;
        this.jobExplorer = jobExplorer;
    }

    public JobExecution runEndOfDay(LocalDate dateDebut, LocalDate dateFin) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("dateDebut", dateDebut.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .addString("dateFin", dateFin.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .addString("executionTimestamp", timestamp)
                    .toJobParameters();

            log.info("Launching EOD job for period {} to {} with timestamp {}", dateDebut, dateFin, timestamp);
            return jobLauncher.run(eodJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException e) {
            throw new IllegalStateException("EOD job could not be launched: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error launching EOD job: " + e.getMessage(), e);
        }
    }

    public Optional<JobExecution> getJobExecutionStatus(Long executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jobExplorer.getJobExecution(executionId));
    }

    public JobExecution restartFailedJob(Long executionId) {
        JobExecution failedExecution = jobExplorer.getJobExecution(executionId);
        if (failedExecution == null) {
            throw new IllegalArgumentException("No job execution found with id: " + executionId);
        }
        if (!failedExecution.getStatus().isUnsuccessful()) {
            throw new IllegalStateException("Job execution " + executionId
                    + " is not in a failed state. Current status: " + failedExecution.getStatus());
        }
        try {
            log.info("Restarting failed EOD job execution {}", executionId);
            return jobLauncher.run(eodJob, failedExecution.getJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException e) {
            throw new IllegalStateException("Failed EOD job could not be restarted: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error restarting EOD job: " + e.getMessage(), e);
        }
    }
}
