# Agent Instructions for zendesk-java-client

This document provides guidance for AI agents and developers working on this project.

## Java Version Requirements

This project **compiles with Java 11** (as specified in `pom.xml` with `maven.compiler.source` and
`maven.compiler.target` set to 11) but **must maintain Java 8 API compatibility**.

For example, you're allowed to use Java 11 compiler features in the code base (such as type inference
with `var`) but not any new standard library features introduced after Java 8, such as `VarHandle`.
This is enforced at build time using the `animal-sniffer` enforcer plugin.

### Running Maven Commands

**NB:** When running Maven commands, ensure you're using the Java 11 version of the JDK to avoid
any build issues and ensure compatibility. The precise way to do so depends on developer machine
setup.

### Common Commands

**Build the project:**
```bash
mvn verify
```

**Run tests:**
```bash
mvn test
```

**Apply code formatting:**
```bash
mvn spotless:apply
```

**Check code formatting without applying changes:**
```bash
mvn spotless:check
```

## Code Formatting

This project uses [Spotless](https://github.com/diffplug/spotless) with google-java-format for code
formatting.

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