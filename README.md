# Magma (sample Maven project)

This is a minimal Maven Java project configured to use the installed Java (Java 22 on the developer machine).

Build and test:

```pwsh
mvn -q test
```

Run the app:

```pwsh
mvn -q exec:java -Dexec.mainClass=magma.App -Dexec.args="YourName"
```

Run the interpreter (convenience scripts)

PowerShell (Windows):

```pwsh
.\run.ps1 [path/to/script.mgs]
```

POSIX (macOS / Linux):

```sh
./run.sh [path/to/script.mgs]
```

By default both scripts run `src/main/magma/magma/Main.mgs` which contains a small example.
