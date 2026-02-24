package com.example.taskmanager.model.entities;

import com.example.taskmanager.model.ImmutableStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.annotation.Nullable;
import java.time.Instant;
import org.immutables.value.Value;

/**
 * Placeholder entity for SERVICE LAYER business logic.
 *
 * <p>Always use ImmutablePlaceholder.builder() to create instances. PlaceholderRecord is used for
 * SQL database persistence.
 *
 * <p>Key Design Principles: - Always use ImmutablePlaceholder, never the Placeholder interface
 * directly - Use builder pattern for all object creation - Immutable for thread safety and
 * functional programming
 *
 * <p>Replace this with your actual domain entities.
 */
@Value.Immutable
@ImmutableStyle
@JsonSerialize(as = ImmutablePlaceholder.class)
@JsonDeserialize(as = ImmutablePlaceholder.class)
public interface Placeholder {
  /** SQL unique identifier (auto-generated Long). */
  @Nullable
  Long id();

  /** Name of the placeholder. */
  String name();

  /** Optional description. */
  @Nullable
  String description();

  /** Timestamp when record was created. */
  @Nullable
  Instant createdAt();

  /** Timestamp when record was last updated. */
  @Nullable
  Instant updatedAt();
}
