# Add New Entity

## Overview

Create a new domain entity with all required layers: entity definition, repository, service, and optionally a REST endpoint.

## Prerequisites

- Project compiles successfully (`mvn clean compile`)
- Docker running (for repository tests with Testcontainers)

## Steps

### 1. Create the Entity Interface

**File**: `Model/src/main/java/com/example/taskmanager/model/entities/{EntityName}.java`

```java
package com.example.taskmanager.model.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import com.example.taskmanager.model.ImmutableStyle;
import org.jspecify.annotations.Nullable;

@Value.Immutable
@ImmutableStyle
@JsonSerialize(as = Immutable{EntityName}.class)
@JsonDeserialize(as = Immutable{EntityName}.class)
public interface {EntityName} {
    @Nullable
    String id();

    String name();
    // Add other fields as needed
}
```

**Important:**
- Replace `{EntityName}` with actual name (e.g., `Product`, `Order`)
- Always use `@Nullable` for optional fields
- Always add `@JsonSerialize` and `@JsonDeserialize` annotations

### 2a. Create SQL Record (if using SQLDatastore)

**File**: `Model/src/main/java/com/example/taskmanager/model/entities/{EntityName}Record.java`

```java
package com.example.taskmanager.model.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("{entity_name_snake}")
public record {EntityName}Record(
    @Id Long id,
    String name
    // Add other fields matching the entity
) {
    public static {EntityName}Record fromEntity(Immutable{EntityName} entity) {
        return new {EntityName}Record(
            entity.id() != null ? Long.valueOf(entity.id()) : null,
            entity.name()
        );
    }

    public Immutable{EntityName} toEntity() {
        return Immutable{EntityName}.builder()
            .id(String.valueOf(id))
            .name(name)
            .build();
    }
}
```

### 2b. Create SQL Migration

**File**: `SQLDatastore/src/main/resources/db/migration/V{next_version}__{description}.sql`

```sql
CREATE TABLE {entity_name_snake} (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Important:**
- Find the next version number by checking existing migrations
- Never modify existing migration files
- Use snake_case for table and column names

### 2c. Create SQL Repository

**File**: `SQLDatastore/src/main/java/com/example/taskmanager/sqldatastore/repository/{EntityName}Repository.java`

```java
package com.example.taskmanager.sqldatastore.repository;

import com.example.taskmanager.model.entities.{EntityName}Record;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface {EntityName}Repository extends CrudRepository<{EntityName}Record, Long> {
    // Add custom query methods as needed
}
```

### 3. Create Service

**File**: `Shared/src/main/java/com/example/taskmanager/shared/service/{EntityName}Service.java`

```java
package com.example.taskmanager.shared.service;

import com.example.taskmanager.model.entities.Immutable{EntityName};
import com.example.taskmanager.model.entities.{EntityName}Record;
import com.example.taskmanager.sqldatastore.repository.{EntityName}Repository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class {EntityName}Service {
    private final {EntityName}Repository repository;

    public {EntityName}Service({EntityName}Repository repository) {
        this.repository = repository;
    }

    public List<Immutable{EntityName}> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
            .map({EntityName}Record::toEntity)
            .toList();
    }

    public Optional<Immutable{EntityName}> findById(Long id) {
        return repository.findById(id).map({EntityName}Record::toEntity);
    }

    public Immutable{EntityName} save(Immutable{EntityName} entity) {
        {EntityName}Record saved = repository.save({EntityName}Record.fromEntity(entity));
        return saved.toEntity();
    }
}
```

### 4. Create Request/Response DTOs (if API needed)

**File**: `Model/src/main/java/com/example/taskmanager/model/dto/{EntityName}Request.java`

```java
package com.example.taskmanager.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import com.example.taskmanager.model.ImmutableStyle;

@Value.Immutable
@ImmutableStyle
@JsonSerialize(as = Immutable{EntityName}Request.class)
@JsonDeserialize(as = Immutable{EntityName}Request.class)
public interface {EntityName}Request {
    String name();
    // Add other input fields
}
```

**File**: `Model/src/main/java/com/example/taskmanager/model/dto/{EntityName}Response.java`

```java
package com.example.taskmanager.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import com.example.taskmanager.model.ImmutableStyle;
import com.example.taskmanager.model.entities.Immutable{EntityName};

@Value.Immutable
@ImmutableStyle
@JsonSerialize(as = Immutable{EntityName}Response.class)
@JsonDeserialize(as = Immutable{EntityName}Response.class)
public interface {EntityName}Response {
    String id();
    String name();
    // Add other output fields

    static Immutable{EntityName}Response from(Immutable{EntityName} entity) {
        return Immutable{EntityName}Response.builder()
            .id(entity.id())
            .name(entity.name())
            .build();
    }
}
```

### 5. Compile and Test

```bash
mvn clean compile
mvn test
```

## Checklist

- [ ] Entity interface created with `@Value.Immutable` and JSON annotations
- [ ] SQL Record created with conversion methods
- [ ] Flyway migration added (new version, not modified existing)
- [ ] SQL Repository interface created
- [ ] Service class created with CRUD operations
- [ ] Request/Response DTOs created (if API endpoint needed)
- [ ] Code compiles (`mvn clean compile`)
- [ ] Tests pass (`mvn test`)

## Common Mistakes

- **Using `new` for Immutables**: Always use `ImmutableX.builder()...build()`
- **Interface types in signatures**: Use `ImmutableX` not `X` interface
- **Missing JSON annotations**: Always add both `@JsonSerialize` and `@JsonDeserialize`
- **Modifying existing migrations**: Create new migration file instead
- **Exposing Record/Document types**: Convert at repository boundary, return Immutables
