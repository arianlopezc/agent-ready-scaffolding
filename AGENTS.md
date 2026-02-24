# task-manager — AI Agent Guide

This file provides a cross-tool baseline for any AI coding agent working on this project.

## Project Structure

Java 21+ multi-module Maven project (Spring Boot):

| Module | Purpose |
|--------|---------|
| **Model** | Entities, DTOs, Enums (Immutables) |
| **SQLDatastore** | Spring Data JDBC, Flyway migrations |
| **Shared** | Business services, circuit breaker |
| **API** | REST controllers, OpenAPI |

## Build Commands

```bash
mvn clean compile          # Build all modules
mvn test                   # Run all tests
mvn clean package          # Package all modules
```

## Quality Commands

```bash
mvn spotless:apply         # Auto-format all Java files (Google Java Format)
mvn spotless:check         # Check formatting without modifying (CI)
mvn enforcer:enforce       # Check dependency and version rules
```

## Module Dependency Rules

```
Model          → (none)
SQLDatastore   → Model
Shared         → Model, SQLDatastore
API            → Model, Shared
```

Never import from API in Shared or vice versa.

## Key Patterns

- **Immutables**: Use `ImmutableX.builder()...build()` for all DTOs and entities
- **Constructor injection**: All fields `private final`, no `@Autowired` on fields
- **Modern Java**: Streams over loops, pattern matching, `Optional.map()`/`orElse()`
- **Method size**: Max 30 lines, extract helpers for complex logic

## Testing

- **Write tests BEFORE implementation code** — one test at a time, not in bulk
- **Fix implementation to make tests pass** — never modify tests to pass
- **Test behavior, not internals** — test through public interfaces, not private methods
- **Naming**: `should_ExpectedBehavior_When_Condition` with Given/When/Then comments
- **Cover all categories**: happy path, not-found/empty, validation failures, error conditions
- **Full guide**: `.ai/prompts/testing-guide.md` | **Standards**: `.ai/prompts/JAVA_CODE_QUALITY.md` (Section 7)

## Quality Specification

For the full coding standards, read: `.ai/prompts/JAVA_CODE_QUALITY.md`

## Why This Architecture

| Decision | Why |
|----------|-----|
| Spring Data JDBC over JPA | No lazy loading surprises, no proxy magic, no @Transactional gotchas. What you write is what runs. |
| Immutables over Lombok/records | Type-safe builders, true immutability, generated equals/hashCode. Records are used only at persistence boundaries. |
| Multi-module over monolith | Enforces dependency boundaries at compile time. API can't import datastore code directly. Clear ownership. |
| Constructor injection only | Testable without Spring context. All dependencies explicit. No hidden @Autowired magic. |

## Using Placeholder Code as Patterns

The generated code includes working `Placeholder*` classes as reference implementations. When creating new entities, endpoints, or services, follow these patterns:

| Task | Look at | Then read |
|------|---------|-----------|
| Add entity | `Placeholder.java`, `PlaceholderRecord.java` | `.ai/prompts/add-entity.md` |
| Add endpoint | `PlaceholderController.java`, `PlaceholderService.java` | `.ai/prompts/add-endpoint.md` |

**Do not delete placeholder classes until you have at least one real implementation** — they serve as compilation-verified examples.
