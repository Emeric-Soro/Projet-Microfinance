package com.microfinance.core_banking.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.OracleContainer;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class TarificationMigrationIntegrationTest {

    @Test
    void shouldApplyMigrationOnFreshSchemaAndRemainSafeWhenReplayed() throws Exception {
        Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable(),
                "Docker is required to validate the Oracle Flyway migration."
        );

        try (OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
                .withUsername("core_banking_test")
                .withPassword("core_banking_test_pwd")) {
            oracle.start();

            Path migrationsPath = Path.of("src", "main", "resources", "db", "migration").toAbsolutePath();
            Flyway flyway = Flyway.configure()
                    .dataSource(oracle.getJdbcUrl(), oracle.getUsername(), oracle.getPassword())
                    .locations("filesystem:" + migrationsPath)
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load();

            flyway.migrate();
            assertTarificationKeys(
                    oracle,
                    List.of(
                            "AGIO_FRAIS_TENUE_MENSUEL",
                            "AGIO_TAUX_PENALITE_DECOUVERT",
                            "TRANSACTION_FEE_RATE_RETRAIT",
                            "TRANSACTION_FEE_RATE_VIREMENT"
                    )
            );

            flyway.migrate();
            assertTarificationKeys(
                    oracle,
                    List.of(
                            "AGIO_FRAIS_TENUE_MENSUEL",
                            "AGIO_TAUX_PENALITE_DECOUVERT",
                            "TRANSACTION_FEE_RATE_RETRAIT",
                            "TRANSACTION_FEE_RATE_VIREMENT"
                    )
            );
        }
    }

    private static void assertTarificationKeys(OracleContainer oracle, List<String> expectedKeys) throws Exception {
        List<String> actualKeys = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                oracle.getJdbcUrl(),
                oracle.getUsername(),
                oracle.getPassword()
        ); Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT cle_parametre FROM tarification_parametre ORDER BY cle_parametre"
             )) {
            while (resultSet.next()) {
                actualKeys.add(resultSet.getString(1));
            }
        }

        assertEquals(expectedKeys.size(), actualKeys.size());
        assertIterableEquals(expectedKeys, actualKeys);
    }
}
