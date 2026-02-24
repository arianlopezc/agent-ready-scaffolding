# task-manager

Java multi-module Maven project using Spring Boot with PostgreSQL.

## Code Quality (IMPORTANT)

**Code quality specification:** `.ai/prompts/JAVA_CODE_QUALITY.md`

This project enforces strict code quality standards. You MUST:

1. **Follow the specification** when generating code
2. **Self-review** generated code against the specification
3. **Refactor** any violations immediately
4. **Verify** the code compiles and tests pass

### Quick Quality Checks

| Check | Requirement |
|-------|-------------|
| Method length | Max 30 lines, extract helpers |
| Loops vs Streams | Prefer streams for filter/map/collect |
| Optional | Return types only, use `map`/`orElse`, never `isPresent()+get()` |
| Records | Use for simple data, copy mutable fields |
| Pattern matching | Use `instanceof Type t`, switch expressions |
| Collections | Use `List.of()`, `.toList()`, no `Arrays.asList()` |
| Null handling | `Objects.requireNonNull` in constructors, Bean Validation on DTOs |
| Injection | Constructor injection only, all fields `private final` |

**Full specification:** `.ai/prompts/JAVA_CODE_QUALITY.md`

## Build & Test Commands

| Command | Description |
|---------|-------------|
| `mvn clean compile` | Build all modules |
| `mvn test` | Run all tests |
| `mvn clean package` | Package all modules |
| `cd API && mvn spring-boot:run` | Start API server (port 8080) |
| `docker-compose up -d` | Start infrastructure services |

## Module Dependencies

Dependency direction — modules may only import from modules they depend on:

```
Model          → (none)
SQLDatastore   → Model
Shared         → Model, SQLDatastore
API            → Model, Shared
```

Never import from API in Shared or vice versa. Violations are caught by ArchUnit tests in `ArchitectureTest.java`.

## Code Patterns

| Pattern | Description |
|---------|-------------|
| Entities | Use `ImmutableX.builder()...build()` pattern with `@Value.Immutable` |
| DTOs | Use `@Value.Immutable` with `@JsonSerialize/@JsonDeserialize` |
| Services | Inject repositories, add `@CircuitBreaker(name = "default")` on external calls |
| Controllers | Use `@RestController`, return `ImmutableXResponse` types |
| SQL Records | Use Java records at repository boundary, convert to Immutables immediately |
| Migrations | Use `V{number}__{description}.sql`, never modify existing migrations |

## File Locations

Paths below use `com/example/taskmanager/` as the package directory.

| Purpose | Location |
|---------|----------|
| Entities | `Model/src/main/java/com/example/taskmanager/model/entities/` |
| DTOs | `Model/src/main/java/com/example/taskmanager/model/dto/` |
| SQL Repositories | `SQLDatastore/src/main/java/com/example/taskmanager/sqldatastore/repository/` |
| SQL Migrations | `SQLDatastore/src/main/resources/db/migration/` |
| Services | `Shared/src/main/java/com/example/taskmanager/shared/service/` |
| Controllers | `API/src/main/java/com/example/taskmanager/api/controller/` |
| API Config | `API/src/main/java/com/example/taskmanager/api/config/` |

## Immutables

This project uses [Immutables](https://immutables.github.io/) for all DTOs and entities. This is a critical pattern that must be followed consistently.

**Rules:**
- Always use `ImmutableX` concrete types in method signatures, not interface types
- Always use builders: `ImmutablePlaceholder.builder()...build()`
- Never use `new` for Immutable objects
- Always add `@JsonSerialize` and `@JsonDeserialize` annotations
- Enums do NOT use Immutables

**Correct:**
```java
public ImmutablePlaceholder create(ImmutablePlaceholderRequest request) {
    return ImmutablePlaceholder.builder()
        .name(request.name())
        .build();
}
```

**Wrong:**
```java
public Placeholder create(PlaceholderRequest request) {  // interface type
    return new Placeholder(...);  // constructor
}
```

## Persistence Boundaries

**SQL (Spring Data JDBC):**
- `PlaceholderRecord` (Java record) exists only at repository boundary
- Convert to `ImmutablePlaceholder` immediately after repository calls
- Never expose `PlaceholderRecord` outside repository/service layer
- Flyway migrations: `V{number}__{description}.sql` — never modify existing migrations

## Exception Handling

`GlobalExceptionHandler` handles all common exceptions — do not add try-catch in controllers for:
- Validation errors (400)
- JSON parsing errors (400)
- Missing/mistyped parameters (400)
- Not found (404)
- Method not allowed (405)
- Unsupported media type (415)
- Uncaught exceptions (500)

## Configuration

- Use `${ENV_VAR:default}` pattern in `application.yml` — never hardcode credentials
- Log format is in `logback-spring.xml`, log levels are in `application.yml` — do not mix these
- Default profile is `local` (colored console); other profiles use JSON logging
- Use `@CircuitBreaker(name = "default")` on methods calling external resources

## Observability

**Metrics (Prometheus):**
- All modules expose metrics at `/actuator/prometheus`

**API Documentation (OpenAPI/Swagger):**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/api-docs`

**Correlation IDs:**
- `CorrelationIdFilter.java` adds `X-Correlation-ID` header to all requests
- IDs are included in logs via MDC and returned in response headers

**Health Probes:**
- `/actuator/health` — Overall health
- `/actuator/health/readiness` — Kubernetes readiness
- `/actuator/health/liveness` — Kubernetes liveness

## Testing

Repository tests use Testcontainers — Docker must be running.

- **Write tests BEFORE implementation code** — one test at a time, not in bulk
- **Fix implementation to make tests pass** — never modify tests to pass
- **Test behavior, not internals** — test through public interfaces
- **Naming**: `should_ExpectedBehavior_When_Condition` with Given/When/Then comments
- **Full guide**: `.ai/prompts/testing-guide.md`

## Task Guides

For complex tasks, read the corresponding guide in `.ai/prompts/`:

| Task | Guide | When to Use |
|------|-------|-------------|
| **Code quality** | `.ai/prompts/JAVA_CODE_QUALITY.md` | Before/after writing ANY Java code |
| **Testing** | `.ai/prompts/testing-guide.md` | Before writing ANY test code |
| Add new entity | `.ai/prompts/add-entity.md` | Creating a new domain object |
| Add REST endpoint | `.ai/prompts/add-endpoint.md` | Exposing new API functionality |

## Git

These files are local and already in `.gitignore`:
- Agent-specific local settings (e.g. `CLAUDE.local.md`, `.claude/settings.local.json`)
- Plan mode scratch files (`*PLAN.md`)
