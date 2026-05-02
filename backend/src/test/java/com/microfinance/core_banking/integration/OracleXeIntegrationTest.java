package com.microfinance.core_banking.integration;

import com.microfinance.core_banking.CoreBankingApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.flyway.enabled=false",
    "spring.jpa.show-sql=true"
})
class OracleXeIntegrationTest {

    @Container
    static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
        .withUsername("core_banking_test")
        .withPassword("core_banking_test_pwd");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.datasource.driver-class-name", oracle::getDriverClassName);
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToOracleContainer() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isValid(5)).isTrue();
            assertThat(conn.getMetaData().getDatabaseProductName()).containsIgnoringCase("Oracle");
        }
    }

    @Test
    void shouldCreateSchemaOnStartup() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM user_tables")) {
            assertThat(rs.next()).isTrue();
            int tableCount = rs.getInt(1);
            assertThat(tableCount).isGreaterThan(0);
        }
    }
}
