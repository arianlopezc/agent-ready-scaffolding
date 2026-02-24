package com.example.taskmanager.shared.service;

import com.example.taskmanager.model.dto.ImmutablePlaceholderRequest;
import com.example.taskmanager.model.entities.ImmutablePlaceholder;
import com.example.taskmanager.model.entities.PlaceholderRecord;
import com.example.taskmanager.sqldatastore.repository.PlaceholderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

/**
 * Service for Placeholder business logic.
 *
 * <p>Always uses ImmutablePlaceholder with builder pattern. Uses SQL repository (PlaceholderRecord)
 * for persistence.
 *
 * <p>Uses circuit breaker pattern for resilience. Replace this with your actual services.
 */
@Service
public class PlaceholderService {
  private final PlaceholderRepository repository;

  public PlaceholderService(PlaceholderRepository repository) {
    this.repository = repository;
  }

  /** Create a new placeholder. */
  @CircuitBreaker(name = "default")
  public ImmutablePlaceholder create(ImmutablePlaceholderRequest request) {
    PlaceholderRecord saved =
        repository.save(
            new PlaceholderRecord(request.name(), request.description(), Instant.now()));
    return ImmutablePlaceholder.builder()
        .id(saved.id())
        .name(saved.name())
        .description(saved.description())
        .createdAt(saved.createdAt())
        .updatedAt(saved.updatedAt())
        .build();
  }

  /** Get a placeholder by ID. */
  @CircuitBreaker(name = "default")
  public Optional<ImmutablePlaceholder> findById(Long id) {
    return repository
        .findById(id)
        .map(
            record ->
                ImmutablePlaceholder.builder()
                    .id(record.id())
                    .name(record.name())
                    .description(record.description())
                    .createdAt(record.createdAt())
                    .updatedAt(record.updatedAt())
                    .build());
  }

  /** Get all placeholders. */
  @CircuitBreaker(name = "default")
  public List<ImmutablePlaceholder> findAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .map(
            record ->
                ImmutablePlaceholder.builder()
                    .id(record.id())
                    .name(record.name())
                    .description(record.description())
                    .createdAt(record.createdAt())
                    .updatedAt(record.updatedAt())
                    .build())
        .toList();
  }

  /** Update an existing placeholder. */
  @CircuitBreaker(name = "default")
  public Optional<ImmutablePlaceholder> update(Long id, ImmutablePlaceholderRequest request) {
    return repository
        .findById(id)
        .map(
            existing -> {
              PlaceholderRecord saved =
                  repository.save(
                      existing.withNameAndDescription(request.name(), request.description()));
              return ImmutablePlaceholder.builder()
                  .id(saved.id())
                  .name(saved.name())
                  .description(saved.description())
                  .createdAt(saved.createdAt())
                  .updatedAt(saved.updatedAt())
                  .build();
            });
  }

  /** Delete a placeholder by ID. */
  @CircuitBreaker(name = "default")
  public boolean delete(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }
}
