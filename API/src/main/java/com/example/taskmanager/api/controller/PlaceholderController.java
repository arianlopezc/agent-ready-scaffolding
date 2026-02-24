package com.example.taskmanager.api.controller;

import com.example.taskmanager.model.dto.ImmutablePlaceholderRequest;
import com.example.taskmanager.model.dto.ImmutablePlaceholderResponse;
import com.example.taskmanager.shared.service.PlaceholderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Placeholder CRUD operations.
 *
 * <p>Always uses ImmutableX types with builder pattern. Replace this with your actual controllers.
 */
@RestController
@RequestMapping("/api/placeholders")
public class PlaceholderController {
  // Using Shared service with SQL datastore
  private final PlaceholderService service;

  public PlaceholderController(PlaceholderService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ImmutablePlaceholderResponse> create(
      @Valid @RequestBody ImmutablePlaceholderRequest request) {
    var created = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ImmutablePlaceholderResponse.builder()
                .id(created.id())
                .name(created.name())
                .description(created.description())
                .createdAt(created.createdAt())
                .updatedAt(created.updatedAt())
                .build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ImmutablePlaceholderResponse> getById(@PathVariable Long id) {
    return service
        .findById(id)
        .map(
            p ->
                ResponseEntity.ok(
                    ImmutablePlaceholderResponse.builder()
                        .id(p.id())
                        .name(p.name())
                        .description(p.description())
                        .createdAt(p.createdAt())
                        .updatedAt(p.updatedAt())
                        .build()))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<ImmutablePlaceholderResponse>> getAll() {
    var placeholders =
        service.findAll().stream()
            .map(
                p ->
                    ImmutablePlaceholderResponse.builder()
                        .id(p.id())
                        .name(p.name())
                        .description(p.description())
                        .createdAt(p.createdAt())
                        .updatedAt(p.updatedAt())
                        .build())
            .toList();
    return ResponseEntity.ok(placeholders);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ImmutablePlaceholderResponse> update(
      @PathVariable Long id, @Valid @RequestBody ImmutablePlaceholderRequest request) {
    return service
        .update(id, request)
        .map(
            p ->
                ResponseEntity.ok(
                    ImmutablePlaceholderResponse.builder()
                        .id(p.id())
                        .name(p.name())
                        .description(p.description())
                        .createdAt(p.createdAt())
                        .updatedAt(p.updatedAt())
                        .build()))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
