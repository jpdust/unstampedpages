# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.1 REST API backend application using Java 25 and Gradle 9.2.1.

## Build Commands

```bash
./gradlew build          # Full build with tests
./gradlew bootRun        # Run the application
./gradlew bootJar        # Create executable JAR
./gradlew clean          # Remove build artifacts
```

## Testing

```bash
./gradlew test                                              # Run all tests
./gradlew test --tests UnstampedpagesApplicationTests       # Run specific test class
./gradlew test --tests "*ControllerTest"                    # Run tests matching pattern
./gradlew test --fail-fast                                  # Stop on first failure
```

## Architecture

- **Entry Point**: `UnstampedpagesApplication.java` - Spring Boot main class with `@RestController`
- **Package Structure**: `com.unstampedpages`
- **Resources**: `src/main/resources/` contains `application.properties`, `static/`, and `templates/`
- **Test Framework**: JUnit 5 with Spring Boot Test (`@SpringBootTest`)

## Key Conventions

- Standard Maven-style Gradle project layout
- REST controllers use `@RestController` annotation
- Tests are in `src/test/java/` mirroring the main source structure
