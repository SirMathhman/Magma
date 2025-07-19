## Documentation Practice
When a new feature is introduced, ensure the relevant documentation is updated to capture why the feature exists and how it fits into the design.

## Continuous Integration
A lightweight CI pipeline runs tests on every push and pull request using GitHub Actions. The goal is to keep feedback fast and avoid regressions as features grow. The workflow installs dependencies from `requirements.txt` and executes `pytest` to honor our test-driven approach. Keeping the pipeline small adheres to simple design and ensures developers focus on the code rather than infrastructure.
