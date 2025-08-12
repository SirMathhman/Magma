# Magma

A TypeScript project with strict code quality standards.

## Setup

```bash
npm install
```

## Running Tests

```bash
npm test
```

This command runs:
1. Prettier formatting check
2. ESLint linting (including cyclomatic complexity check)
3. Jest test suite

## Code Quality Standards

### Cyclomatic Complexity

All functions must have a cyclomatic complexity of 10 or less. This is enforced via ESLint during the test run.

See [COMPLEXITY.md](./COMPLEXITY.md) for more details about the complexity requirements and refactoring strategies.

### Linting

```bash
npm run lint        # Check for issues
npm run lint:fix    # Auto-fix issues where possible
```

## Project Structure

```
src/
├── compile.ts         # Main compilation logic
├── compile.test.ts    # Tests for compile function
├── alwaysThrows.ts    # Utility function that always throws
└── alwaysThrows.test.ts # Tests for alwaysThrows function
```
