# Codegen API Contract

## Purpose
Define the public behavior of the codegen module (library) used by the CLI and tests.

## API surface (conceptual)
- Codegen.generate(TranslationUnit tu, Path outDir, CodegenOptions options) -> void

## Guarantees
- For a semantically valid TranslationUnit, `generate` writes C source files and headers to `outDir`.
- Throws CodegenException for unrecoverable mapping errors with descriptive messages and source locations.
