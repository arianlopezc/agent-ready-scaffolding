---
applyTo: "**/*.java"
---

# Java Coding Standards

## Module Dependencies

```
Model          → (none)
SQLDatastore   → Model
Shared         → Model, SQLDatastore
API            → Model, Shared
```

Never import from API in Shared or vice versa.

## Immutables Pattern (CRITICAL)

Always use `ImmutableX` concrete types and builders:

```java
// CORRECT
public ImmutableUser create(ImmutableCreateUserRequest request) {
    return ImmutableUser.builder()
        .name(request.name())
        .build();
}

// WRONG
public User create(CreateUserRequest request) {
    return new User(request.name());
}
```

## Modern Java (21+)

- Streams over loops for filter/map/collect
- Pattern matching: `instanceof Type t`
- Optional: `map()`/`orElse()`, never `isPresent()+get()`
- Collections: `List.of()`, `.toList()`

## Method Guidelines

- Maximum 30 lines per method
- Maximum 5 parameters (use objects for more)
- Maximum 2 levels of nesting
- Use early returns to reduce complexity

## Dependency Injection

Constructor injection only. Never use `@Autowired` on fields.

## Testing

- Write a **failing test BEFORE** writing implementation code
- **One test at a time** — do not write all tests in bulk
- Fix implementation to make tests pass — **never modify tests to pass**
- Name tests: `should_ExpectedBehavior_When_Condition`
- Full spec: `.ai/prompts/JAVA_CODE_QUALITY.md` | Guide: `.ai/prompts/testing-guide.md`
