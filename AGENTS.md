# Agent Guidelines

This project follows test-driven development and Kent Beck's rules for simple design. Please consult the documentation below before making changes.

- [README.md](README.md) – overview and build instructions.
- [docs/java-to-typescript-roadmap.md](docs/java-to-typescript-roadmap.md) – feature mapping and current roadmap.
- [docs/coding-standards.md](docs/coding-standards.md) – coding style preferences.

Run the build and unit tests with:

```bash
./build.sh
./test.sh
```

## Quick Project Structure
- `src/main/java/com/example/Transpiler.java` – core transpiler logic
- `src/main/java/com/example/Main.java` – command line entry point
