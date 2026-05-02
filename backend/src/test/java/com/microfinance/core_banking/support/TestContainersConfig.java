package com.microfinance.core_banking.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Container
    private static final OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
        .withUsername("core_banking_test")
        .withPassword("core_banking_test_pwd")
        .withReuse(true);

    static {
        oracleContainer.start();
    }

    @Bean
    public OracleContainer oracleContainer() {
        return oracleContainer;
    }

    public static String getJdbcUrl() {
        return oracleContainer.getJdbcUrl();
    }

    public static String getUsername() {
        return oracleContainer.getUsername();
    }

    public static String getPassword() {
        return oracleContainer.getPassword();
    }

    public static String getDriverClassName() {
        return oracleContainer.getDriverClassName();
    }
}
