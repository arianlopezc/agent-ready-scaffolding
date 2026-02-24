package com.example.taskmanager.sqldatastore;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Test configuration for SQLDatastore module integration tests.
 *
 * <p>Since SQLDatastore is a library module (not a Spring Boot application), tests need this
 * configuration to bootstrap the Spring context.
 *
 * <p>The scanBasePackages includes both sqldatastore (for repositories/config) and model (for
 * entities that Spring Data JDBC needs to read).
 */
@SpringBootApplication(
    scanBasePackages = {"com.example.taskmanager.sqldatastore", "com.example.taskmanager.model"})
@EnableJdbcRepositories(basePackages = "com.example.taskmanager.sqldatastore.repository")
public class TestConfig {}
