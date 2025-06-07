# Self Replicator

This project demonstrates a simple Java program that copies its own compiled class file to another location. It follows a test-driven approach and keeps the design minimal.

## Main Classes

- `com.example.SelfReplicator` – contains the logic to copy the running class file and a small `main` method.
- `com.example.SelfReplicatorTest` – JUnit test verifying the copy operation.

To run the tests:

```bash
mvn test
```

To execute the program and copy the class file:

```bash
mvn package
java -cp target/self-replicator-1.0-SNAPSHOT.jar com.example.SelfReplicator <destination file>
```
