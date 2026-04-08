# Agent Instructions for zendesk-java-client

This document provides guidance for AI agents and developers working on this project.

## Java Version Requirements

This project **compiles with Java 11** (as specified in `pom.xml` with `maven.compiler.source` and `maven.compiler.target` set to 11) but **must maintain Java 8 API compatibility**.

### CRITICAL: Java 8 API Compatibility

**The project enforces Java 8 API compatibility via animal-sniffer, even though it compiles with Java 11.**

This means:
- ✅ **Allowed:** Java 8 language features (lambdas, method references, streams, Optional, etc.)
- ✅ **Allowed:** Java 11 compiler and build tools
- ❌ **NOT Allowed:** APIs added in Java 9+ (e.g., `Objects.requireNonNullElse()`, `List.of()`, `Map.of()`, `String.isBlank()`, etc.)

When modernizing code or adding features:
1. Use Java 8 language syntax features freely (lambdas, streams, etc.)
2. Only use APIs available in Java 8 (check the Javadoc version!)
3. The animal-sniffer plugin will fail the build if you use Java 9+ APIs
4. If in doubt, verify API availability at https://docs.oracle.com/javase/8/docs/api/

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
- **API compatibility with Java 8 (via animal-sniffer)** - This is enforced at build time and will fail if Java 9+ APIs are used

### Why Java 8 API Compatibility?

This library is designed to be usable by applications running on Java 8 JVMs, even though it's built with Java 11 tooling. This is a common pattern for libraries that want maximum compatibility while benefiting from modern build tools.