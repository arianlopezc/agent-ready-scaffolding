package com.example.taskmanager.sqldatastore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.taskmanager.model.entities.PlaceholderRecord;
import com.example.taskmanager.sqldatastore.TestConfig;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Integration tests for PlaceholderRepository.
 *
 * <p>Uses Testcontainers for realistic database testing. Tests are automatically skipped if Docker
 * is not available.
 */
@DataJdbcTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PlaceholderRepositoryTest {
  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine");

  @Autowired private PlaceholderRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void shouldSavePlaceholder() {
    // Given
    PlaceholderRecord record = new PlaceholderRecord("Test", "Test description", Instant.now());

    // When
    PlaceholderRecord saved = repository.save(record);

    // Then
    assertThat(saved.id()).isNotNull();
    assertThat(saved.name()).isEqualTo("Test");
    assertThat(saved.description()).isEqualTo("Test description");
  }

  @Test
  void shouldFindById() {
    // Given
    PlaceholderRecord saved =
        repository.save(new PlaceholderRecord("Find Me", "Description", Instant.now()));

    // When
    Optional<PlaceholderRecord> found = repository.findById(saved.id());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().name()).isEqualTo("Find Me");
  }

  @Test
  void shouldFindByName() {
    // Given
    repository.save(new PlaceholderRecord("Unique Name", "Description", Instant.now()));

    // When
    Optional<PlaceholderRecord> found = repository.findByName("Unique Name");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().description()).isEqualTo("Description");
  }

  @Test
  void shouldDeletePlaceholder() {
    // Given
    PlaceholderRecord saved =
        repository.save(new PlaceholderRecord("To Delete", "Will be deleted", Instant.now()));

    // When
    repository.deleteById(saved.id());

    // Then
    assertThat(repository.findById(saved.id())).isEmpty();
  }
}
