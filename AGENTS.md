# Agent Instructions for zendesk-java-client

This document provides guidance for AI agents and developers working on this project.

## Java Version Requirements

This project targets **Java 11** (as specified in `pom.xml` with `maven.compiler.source` and `maven.compiler.target` set to 11).

### Running Maven Commands

When running Maven commands, ensure you're using Java 11 by setting `JAVA_HOME`:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn <command>
```

### Common Commands

**Build the project:**
```bash
JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn clean install
```

**Run tests:**
```bash
JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn test
```

**Apply code formatting (Spotless):**
```bash
JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn spotless:apply
```

**Check code formatting:**
```bash
JAVA_HOME=$(/usr/libexec/java_home -v 11) mvn spotless:check
```

## Code Formatting

This project uses [Spotless](https://github.com/diffplug/spotless) with google-java-format for code formatting.

- All Java code must be formatted before committing
- Run `mvn spotless:apply` to format code automatically

## Project Structure

- **Source code:** `src/main/java/org/zendesk/client/v2/`
- **Tests:** `src/test/java/org/zendesk/client/v2/`
- **Main entry point:** `Zendesk.java` - The primary API client class

## Dependencies

Key dependencies include:
- async-http-client for HTTP operations
- Jackson for JSON serialization/deserialization
- SLF4J for logging
- JUnit 4 for testing
- WireMock for HTTP mocking in tests

## Enforcement Rules

The project uses Maven Enforcer Plugin to ensure:
- Bytecode version matches Java 11
- API compatibility with Java 8 (via animal-sniffer)