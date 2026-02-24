# Testing Infrastructure Guide

## Quick Reference

| Layer | Test Style | Annotations / Tools | Container? |
|-------|-----------|---------------------|------------|
| Service | Unit test | `@ExtendWith(MockitoExtension.class)`, `@Mock` | No |
| Controller | Slice test | `@WebMvcTest(Controller.class)`, `MockMvc`, `@MockitoBean` | No |
| Repository | Integration test | `@DataJdbcTest`, `@Testcontainers`, `PostgreSQLContainer` | Yes |
| Architecture | Static analysis | `@AnalyzeClasses`, `@ArchTest` (ArchUnit) | No |

**Assertions**: Use AssertJ (`assertThat`) for all assertions.

---

## TDD Workflow (MANDATORY)

```
1. Write the test FIRST — define expected behavior
2. Run it — confirm it FAILS (a test that passes immediately is suspect)
3. Write the minimum implementation to make it pass
4. Run it again — confirm it PASSES
5. Refactor if needed — tests must still pass
6. Never modify a test to make it pass — fix the implementation
```

**Run one test at a time**, not the full suite. Full suite runs happen at the end, as final verification.

```bash
# Run a single test class
mvn -pl Shared test -Dtest=PlaceholderServiceTest -B

# Run a single test method
mvn -pl Shared test -Dtest=PlaceholderServiceTest#shouldCreatePlaceholder -B

# Run the full suite (final verification only)
mvn test -B
```

---

## Test Naming Convention

```
should_ExpectedBehavior_When_Condition
```

Examples:
- `shouldCreatePlaceholder` (happy path)
- `shouldReturnEmptyWhenNotFound`
- `shouldThrowWhenNameIsBlank`
- `shouldDeletePlaceholder`

Every test method uses **Given/When/Then** comments:

```java
@Test
void shouldReturnEmptyWhenNotFound() {
    // Given
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<ImmutablePlaceholder> result = service.findById(999L);

    // Then
    assertThat(result).isEmpty();
}
```

---

## Full Test Examples

### Service Layer — Unit Test

```java
@ExtendWith(MockitoExtension.class)
class PlaceholderServiceTest {
    @Mock
    private PlaceholderRepository repository;

    private PlaceholderService service;

    @BeforeEach
    void setUp() {
        service = new PlaceholderService(repository);
    }

    @Test
    void shouldCreatePlaceholder() {
        // Given
        var request = ImmutablePlaceholderRequest.builder()
            .name("Test")
            .description("Description")
            .build();
        PlaceholderRecord savedRecord = new PlaceholderRecord(
            1L, "Test", "Description", Instant.now(), Instant.now()
        );
        when(repository.save(any())).thenReturn(savedRecord);

        // When
        ImmutablePlaceholder result = service.create(request);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test");
        verify(repository).save(any());
    }
}
```

### Repository Layer — Integration Test

```java
@DataJdbcTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PlaceholderRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine");

    @Autowired
    private PlaceholderRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldSavePlaceholder() {
        // Given
        PlaceholderRecord record = new PlaceholderRecord("Test", "Description", Instant.now());

        // When
        PlaceholderRecord saved = repository.save(record);

        // Then
        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("Test");
    }
}
```

### Controller Layer — Web Slice Test

```java
@WebMvcTest(PlaceholderController.class)
class PlaceholderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceholderService service;

    @Test
    void shouldReturnCreated() throws Exception {
        // Given
        var placeholder = ImmutablePlaceholder.builder()
            .id(1L)
            .name("Test")
            .description("Desc")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        when(service.create(any())).thenReturn(placeholder);

        // When / Then
        mockMvc.perform(post("/api/placeholders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Test", "description": "Desc"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test"));
    }
}
```

---

## Anti-Patterns

| Anti-Pattern | Why It Fails | Do This Instead |
|-------------|-------------|-----------------|
| Testing implementation details | Breaks on every refactor | Test behavior and outcomes |
| Mocking value objects | Unnecessary complexity | Use real instances (Immutables builders) |
| Shared mutable test state | Tests pollute each other | Fresh fixtures per test (`@BeforeEach`) |
| Sleep-based synchronization | Flaky, slow | Use async testing utilities |
| Ignoring edge cases | False confidence | Test nulls, empty, boundaries |
| Multiple assertions per test | Hard to diagnose which failed | One logical assertion per test |

---

## Troubleshooting

| Exception | Cause | Fix |
|-----------|-------|-----|
| `BeanCreationException` | Missing mock for dependency | Add `@MockitoBean` for the dependency |
| `Connection refused` | Container not started | Add `@Testcontainers` and `@Container` |
| `404 on endpoint test` | Wrong URL or missing annotation | Check `@RequestMapping` path |
| `NullPointerException` in test | Dependency not injected | Check constructor wiring / mock setup |
| `IllegalStateException: duplicate key` | Test data from previous test | Add `repository.deleteAll()` in `@BeforeEach` |
| `JsonProcessingException` | Missing Jackson annotations | Add `@JsonSerialize`/`@JsonDeserialize` on Immutables |
| `ConstraintViolationException` | Bean Validation failed | Check `@NotBlank`/`@Size` on request DTO |
| `DataIntegrityViolationException` | Unique constraint violated | Ensure test data doesn't conflict |

---

## Testcontainers Rules

1. **Container must be `static`** — shared across all test methods in the class
2. **Use `@ServiceConnection`** — auto-configures datasource URL (no manual JDBC URL setup)
3. **Use `@AutoConfigureTestDatabase(replace = NONE)`** — prevents Spring from substituting an in-memory DB
4. **Match production image version** — use `postgres:15-alpine` or whatever matches `docker-compose.yml`
