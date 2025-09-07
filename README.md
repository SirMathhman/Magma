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
