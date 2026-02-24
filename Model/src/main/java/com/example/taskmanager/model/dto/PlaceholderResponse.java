package com.example.taskmanager.model.dto;

import com.example.taskmanager.model.ImmutableStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.annotation.Nullable;
import java.time.Instant;
import org.immutables.value.Value;

/**
 * Response DTO for Placeholder data.
 *
 * <p>Uses Immutables for immutable value objects with builder pattern. Always use
 * ImmutablePlaceholderResponse.builder() to create instances.
 *
 * <p>Replace this with your actual response DTOs.
 */
@Value.Immutable
@ImmutableStyle
@JsonSerialize(as = ImmutablePlaceholderResponse.class)
@JsonDeserialize(as = ImmutablePlaceholderResponse.class)
public interface PlaceholderResponse {
  /** SQL database ID (Long). */
  @Nullable
  Long id();

  String name();

  @Nullable
  String description();

  @Nullable
  Instant createdAt();

  @Nullable
  Instant updatedAt();
}
