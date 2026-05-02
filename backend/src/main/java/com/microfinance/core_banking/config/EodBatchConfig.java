package com.microfinance.core_banking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class EodBatchConfig {

    private static final Logger log = LoggerFactory.getLogger(EodBatchConfig.class);

    @Bean
    public Step freezeTransactionsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("freezeTransactionsStep", jobRepository)
                .tasklet(freezeTransactionsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step closeTellersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("closeTellersStep", jobRepository)
                .tasklet(closeTellersTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step calculateInterestStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculateInterestStep", jobRepository)
                .tasklet(calculateInterestTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step agingAnalysisStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("agingAnalysisStep", jobRepository)
                .tasklet(agingAnalysisTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step calculateProvisionsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculateProvisionsStep", jobRepository)
                .tasklet(calculateProvisionsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step postToGeneralLedgerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("postToGeneralLedgerStep", jobRepository)
                .tasklet(postToGeneralLedgerTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step openNextDayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("openNextDayStep", jobRepository)
                .tasklet(openNextDayTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job eodJob(
            JobRepository jobRepository,
            Step freezeTransactionsStep,
            Step closeTellersStep,
            Step calculateInterestStep,
            Step agingAnalysisStep,
            Step calculateProvisionsStep,
            Step postToGeneralLedgerStep,
            Step openNextDayStep
    ) {
        return new JobBuilder("eodJob", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("EOD Job '{}' starting with parameters: {}", jobExecution.getJobInstance().getJobName(),
                                jobExecution.getJobParameters());
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("EOD Job '{}' finished with status: {}", jobExecution.getJobInstance().getJobName(),
                                jobExecution.getStatus());
                    }
                })
                .start(freezeTransactionsStep)
                .next(closeTellersStep)
                .next(calculateInterestStep)
                .next(agingAnalysisStep)
                .next(calculateProvisionsStep)
                .next(postToGeneralLedgerStep)
                .next(openNextDayStep)
                .build();
    }

    private Tasklet freezeTransactionsTasklet() {
        return (org.springframework.batch.core.step.tasklet.Tasklet) (contribution, chunkContext) -> {
            log.info("=== Step 1: freezeTransactionsStep - Freezing/locking all transactions for the period ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("freezeStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet closeTellersTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 2: closeTellersStep - Closing all teller sessions ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("closeTellersStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet calculateInterestTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 3: calculateInterestStep - Calculating and posting interest ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("interestStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet agingAnalysisTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 4: agingAnalysisStep - Running aging analysis on overdue loans ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("agingStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet calculateProvisionsTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 5: calculateProvisionsStep - Calculating loan loss provisions ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("provisionsStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet postToGeneralLedgerTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 6: postToGeneralLedgerStep - Posting all entries to General Ledger ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("glPostingStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet openNextDayTasklet() {
        return (contribution, chunkContext) -> {
            log.info("=== Step 7: openNextDayStep - Opening the system for the next day ===");
            chunkContext.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().putString("openNextDayStatus", "COMPLETED");
            return RepeatStatus.FINISHED;
        };
    }
}
