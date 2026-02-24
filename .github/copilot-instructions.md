# task-manager

Java 21+ multi-module Maven project using Spring Boot with PostgreSQL.

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

## Module Dependencies

```
Model          → (none)
SQLDatastore   → Model
Shared         → Model, SQLDatastore
API            → Model, Shared
```

Never import from API in Shared or vice versa.

## Build & Test Commands

| Command | Description |
|---------|-------------|
| `mvn clean compile` | Build all modules |
| `mvn test` | Run all tests |
| `mvn clean package` | Package all modules |
| `cd API && mvn spring-boot:run` | Start API server (port 8080) |
| `docker-compose up -d` | Start infrastructure services |

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

| Purpose | Location |
|---------|----------|
| Entities | `Model/src/main/java/com/example/taskmanager/model/entities/` |
| DTOs | `Model/src/main/java/com/example/taskmanager/model/dto/` |
| SQL Repositories | `SQLDatastore/src/main/java/com/example/taskmanager/sqldatastore/repository/` |
| SQL Migrations | `SQLDatastore/src/main/resources/db/migration/` |
| Services | `Shared/src/main/java/com/example/taskmanager/shared/service/` |
| Controllers | `API/src/main/java/com/example/taskmanager/api/controller/` |
| API Config | `API/src/main/java/com/example/taskmanager/api/config/` |

## Testing

- **Write tests BEFORE implementation code** — one test at a time, not in bulk
- **Fix implementation to make tests pass** — never modify tests to pass
- **Test behavior, not internals** — test through public interfaces
- **Naming**: `should_ExpectedBehavior_When_Condition` with Given/When/Then comments
- Full spec: `.ai/prompts/JAVA_CODE_QUALITY.md` (Section 7) | Guide: `.ai/prompts/testing-guide.md`

## Task Guides

| Task | Guide |
|------|-------|
| **Code quality** | `.ai/prompts/JAVA_CODE_QUALITY.md` |
| **Testing** | `.ai/prompts/testing-guide.md` |
| Add new entity | `.ai/prompts/add-entity.md` |
| Add REST endpoint | `.ai/prompts/add-endpoint.md` |
