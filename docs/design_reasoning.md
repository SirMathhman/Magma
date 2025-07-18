# Design Reasoning

The compiler currently works with a single input file and a single output file.
This decision keeps the interface familiar and mirrors traditional compilers,
which simplifies future command-line integration.  For now, non-empty input was
initially echoed back with a `compiled:` prefix while the empty-file case
generated an empty `main` function in C.  This minimal behavior allowed tests to
drive the implementation while leaving room for the real compilation pipeline to
evolve.  The first extension to the grammar introduces a simple function form
`fn name() => {}` that compiles directly to `void name() {}` in C.  Supporting
this syntax keeps the compiler behavior obvious while providing a stepping stone
toward a richer language.

## Documentation Practice
When a new feature is introduced, ensure the relevant documentation is updated to capture why the feature exists and how it fits into the design.

## Continuous Integration
A lightweight CI pipeline runs tests on every push and pull request using GitHub Actions. The goal is to keep feedback fast and avoid regressions as features grow. The workflow installs dependencies from `requirements.txt` and executes `pytest` to honor our test-driven approach. Keeping the pipeline small adheres to simple design and ensures developers focus on the code rather than infrastructure.
