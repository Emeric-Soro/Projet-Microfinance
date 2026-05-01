package com.microfinance.core_banking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@Component
public class ReferenceDataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataInitializer.class);

    private final DataSource dataSource;
    private final boolean seedOnStartup;

    public ReferenceDataInitializer(
            DataSource dataSource,
            @Value("${app.reference-data.seed-on-startup:true}") boolean seedOnStartup
    ) {
        this.dataSource = dataSource;
        this.seedOnStartup = seedOnStartup;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedOnStartup) {
            LOGGER.info("Reference data seeding disabled by configuration.");
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding(StandardCharsets.UTF_8.name());
        populator.setContinueOnError(false);
        populator.addScript(new ClassPathResource("sql/reference-data.sql"));

        DatabasePopulatorUtils.execute(populator, dataSource);
        LOGGER.info("Reference data initialized successfully.");
    }
}
