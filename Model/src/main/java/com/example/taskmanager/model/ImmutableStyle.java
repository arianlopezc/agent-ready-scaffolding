package com.example.taskmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Custom Immutables style configuration for TaskManager.
 *
 * <p>This style ensures that Spring Data annotations are properly passed through to the generated
 * immutable classes.
 *
 * <p>Key features: - Passes Spring Data annotations (@Id, etc.) to generated classes - Configures
 * appropriate visibility and builder patterns - Ensures compatibility with Spring Data repositories
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    passAnnotations = {Id.class, Table.class},
    visibility = Value.Style.ImplementationVisibility.PUBLIC)
public @interface ImmutableStyle {}
