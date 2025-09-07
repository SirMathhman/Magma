# Magma

Minimal Maven project scaffold created by assistant.

How to build:

```powershell
mvn -v
mvn -B clean test
```

Generating .c and .h stubs from Java sources
-------------------------------------------

This project includes a small utility `Main` (previously JavaToCGenerator) which scans Java
source files under `src/main/java` and creates corresponding `.h` and `.c`
stub files while preserving the relative directory structure under
`src/main/windows`.

Default usage (from project root):

```powershell
mvn -B package
java -cp target/classes com.example.magma.tools.Main
```

You can also pass custom paths:

```powershell
java -cp target/classes com.example.magma.tools.Main src/main/java src/main/windows
```

The generated files contain simple TODO comments for manual translation.

Example output structure (after running the generator):

```
src/main/windows/com/example/magma/App.h
src/main/windows/com/example/magma/App.c
```


