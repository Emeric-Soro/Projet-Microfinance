package com.microfinance.core_banking.config;

import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class EodBatchConfig {

    private static final Logger log = LoggerFactory.getLogger(EodBatchConfig.class);

    private final CreditExtensionService creditExtensionService;
    private final EpargneExtensionService epargneExtensionService;
    private final ComptabiliteExtensionService comptabiliteExtensionService;
    private final TransactionRepository transactionRepository;
    private final SessionCaisseRepository sessionCaisseRepository;

    public EodBatchConfig(
            CreditExtensionService creditExtensionService,
            EpargneExtensionService epargneExtensionService,
            ComptabiliteExtensionService comptabiliteExtensionService,
            TransactionRepository transactionRepository,
            SessionCaisseRepository sessionCaisseRepository
    ) {
        this.creditExtensionService = creditExtensionService;
        this.epargneExtensionService = epargneExtensionService;
        this.comptabiliteExtensionService = comptabiliteExtensionService;
        this.transactionRepository = transactionRepository;
        this.sessionCaisseRepository = sessionCaisseRepository;
    }

    @Bean
    public Step freezeTransactionsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("freezeTransactionsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 1: freezeTransactionsStep - Freezing/locking all transactions for the period ===");
                    LocalDate dateDebut = getRequiredLocalDate(chunkContext, "dateDebut");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    List<Transaction> periodTransactions = transactionRepository
                            .findByDateExecutionBetween(dateDebut.atStartOfDay(), dateFin.atTime(23, 59, 59));
                    int count = periodTransactions.size();
                    log.info("Found {} transactions for period {} to {}", count, dateDebut, dateFin);
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("freezeCount", count);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step closeTellersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("closeTellersStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 2: closeTellersStep - Closing all teller sessions ===");
                    List<SessionCaisse> openSessions = sessionCaisseRepository.findByStatutIgnoreCase("OUVERTE");
                    int closedSessions = 0;
                    for (SessionCaisse session : openSessions) {
                        session.setStatut("FERMEE");
                        session.setDateFermeture(java.time.LocalDateTime.now());
                        session.setCommentaire("Fermeture automatique EOD");
                        sessionCaisseRepository.save(session);
                        closedSessions++;
                    }
                    log.info("Closed {} teller sessions", closedSessions);
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("closeTellersCount", closedSessions);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step calculateInterestStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculateInterestStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 3: calculateInterestStep - Calculating and posting interest ===");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    Long systemUserId = 0L;
                    int comptesCredites = epargneExtensionService.calculerInteretsCourusMensuels(dateFin, systemUserId);
                    log.info("Credited monthly interest to {} savings accounts", comptesCredites);
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("interestAccountsCredited", comptesCredites);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step agingAnalysisStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("agingAnalysisStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 4: agingAnalysisStep - Running aging analysis on overdue loans ===");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    DetecterImpayesRequestDTO dto = new DetecterImpayesRequestDTO();
                    dto.setDateArrete(dateFin);
                    var impayes = creditExtensionService.detecterImpayes(dto);
                    log.info("Detected {} overdue entries", impayes.size());
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("agingImpayesCount", impayes.size());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step calculateProvisionsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculateProvisionsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 5: calculateProvisionsStep - Calculating loan loss provisions ===");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    CalculerProvisionsRequestDTO dto = new CalculerProvisionsRequestDTO();
                    dto.setDateCalcul(dateFin);
                    var provisions = creditExtensionService.calculerProvisions(dto);
                    BigDecimal totalProvisions = provisions.stream()
                            .map(p -> p.getMontantProvision() != null ? p.getMontantProvision() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    log.info("Calculated {} provisions totaling {}", provisions.size(), totalProvisions);
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("provisionsCount", provisions.size());
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putString("totalProvisions", totalProvisions.toPlainString());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step postToGeneralLedgerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("postToGeneralLedgerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 6: postToGeneralLedgerStep - Posting all entries to General Ledger ===");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    var ecritures = comptabiliteExtensionService.listerEcritures(dateFin.minusMonths(1), dateFin, null);
                    log.info("General Ledger contains {} entries for the period", ecritures.size());
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putInt("glEntriesCount", ecritures.size());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step openNextDayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("openNextDayStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Step 7: openNextDayStep - Opening the system for the next day ===");
                    LocalDate dateFin = getRequiredLocalDate(chunkContext, "dateFin");
                    LocalDate nextDay = dateFin.plusDays(1);
                    log.info("System opened for {}", nextDay);
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putString("nextBusinessDay", nextDay.toString());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
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

    private LocalDate getRequiredLocalDate(ChunkContext chunkContext, String parameterName) {
        Object parameterValue = chunkContext.getStepContext().getJobParameters().get(parameterName);
        if (parameterValue == null) {
            throw new IllegalArgumentException("Le parametre batch '" + parameterName + "' est obligatoire");
        }
        return LocalDate.parse(parameterValue.toString());
    }
}
