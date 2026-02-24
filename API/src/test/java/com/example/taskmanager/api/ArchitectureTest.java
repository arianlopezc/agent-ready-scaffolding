package com.example.taskmanager.api;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Architecture enforcement tests using ArchUnit.
 *
 * <p>These tests catch structural violations at compile time: - Controllers must not access
 * repositories directly - No field injection anywhere - No cyclic dependencies between packages -
 * Model module must not depend on api or datastore
 */
class ArchitectureTest {

  private static JavaClasses classes;

  @BeforeAll
  static void setUp() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.taskmanager");
  }

  @Test
  void controllersShouldNotAccessRepositories() {
    noClasses()
        .that()
        .resideInAPackage("..api.controller..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..sqldatastore.repository..")
        .because("Controllers must delegate to services, never access repositories directly")
        .check(classes);
  }

  @Test
  void noFieldInjection() {
    fields()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("com.example.taskmanager..")
        .should()
        .notBeAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
        .because("Use constructor injection only — no @Autowired on fields")
        .check(classes);
  }

  @Test
  void noCyclicDependencies() {
    slices()
        .matching("com.example.taskmanager.(*)..")
        .should()
        .beFreeOfCycles()
        .because("Module dependencies must be acyclic")
        .check(classes);
  }

  @Test
  void modelShouldNotDependOnOtherModules() {
    noClasses()
        .that()
        .resideInAPackage("..model..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..api..", "..sqldatastore..", "..shared..")
        .because("Model is a leaf module — it must not depend on api, datastore, or shared")
        .check(classes);
  }
}
