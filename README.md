# Magma Compiler Project

This repository hosts the initial scaffolding for **Magma**, a self-hosted compiler for a hybrid language inspired by Rust and TypeScript. Development follows Kent Beck's rules of simple design and test-driven development.


## Philosophy

We aim for clarity and minimalism. The initial implementation is written in Python to bootstrap the toolchain quickly. As the compiler matures, it will become self-hosted in the Magma language.

Documentation lives in the `docs/` directory and is updated alongside the code.
Whenever you add a new feature, update the relevant documents so the rationale stays clear.

See `docs/c_features_safety.md` for a checklist of C features and how we plan to handle them safely.

Development embraces Kent Beck's four rules of simple design:

1. Code passes all tests.
2. Code clearly expresses intent.
3. Code contains no duplication.
4. Code uses the fewest elements needed.

We write tests first and keep documentation in `docs/` updated with any design reasoning.

## Continuous Integration
Automated tests run via GitHub Actions on each push and pull request. The workflow keeps the codebase healthy by running `pytest` with minimal setup.
