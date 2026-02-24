package com.example.taskmanager.shared.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.taskmanager.model.dto.ImmutablePlaceholderRequest;
import com.example.taskmanager.model.entities.ImmutablePlaceholder;
import com.example.taskmanager.model.entities.PlaceholderRecord;
import com.example.taskmanager.sqldatastore.repository.PlaceholderRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for PlaceholderService.
 *
 * <p>Uses Mockito to mock the repository layer. Always uses ImmutableX types with builder pattern.
 */
@ExtendWith(MockitoExtension.class)
class PlaceholderServiceTest {
  @Mock private PlaceholderRepository repository;

  private PlaceholderService service;

  @BeforeEach
  void setUp() {
    service = new PlaceholderService(repository);
  }

  @Test
  void shouldCreatePlaceholder() {
    // Given
    var request =
        ImmutablePlaceholderRequest.builder().name("Test").description("Description").build();
    PlaceholderRecord savedRecord =
        new PlaceholderRecord(1L, "Test", "Description", Instant.now(), Instant.now());
    when(repository.save(any())).thenReturn(savedRecord);

    // When
    ImmutablePlaceholder result = service.create(request);

    // Then
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Test");
    verify(repository).save(any());
  }

  @Test
  void shouldFindById() {
    // Given
    PlaceholderRecord record =
        new PlaceholderRecord(1L, "Test", "Desc", Instant.now(), Instant.now());
    when(repository.findById(1L)).thenReturn(Optional.of(record));

    // When
    Optional<ImmutablePlaceholder> result = service.findById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().name()).isEqualTo("Test");
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    // Given
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<ImmutablePlaceholder> result = service.findById(999L);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeletePlaceholder() {
    // Given
    when(repository.existsById(1L)).thenReturn(true);
    doNothing().when(repository).deleteById(1L);

    // When
    boolean result = service.delete(1L);

    // Then
    assertThat(result).isTrue();
    verify(repository).deleteById(1L);
  }
}
