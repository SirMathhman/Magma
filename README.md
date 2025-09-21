# Magma

Minimal Maven project created by Copilot.

How to build:

```powershell
mvn package
```

Run:

```powershell
java -jar target/magma-0.1.0-SNAPSHOT.jar
```
 
Usage note:

This project provides a small, project-local `magma.Option<T>` sealed interface as an alternative to `java.util.Optional`.
It has two variants: `Some<T>` which wraps a value, and `None<T>` representing absence of a value.

Example:

```java
import magma.Option;
import magma.Option.Some;
import magma.Option.None;

// create values
Option<String> present = new Some<>("hello");
Option<String> absent = new None<>();

// pattern-match-like usage with instanceof (Java pattern matching):
if (present instanceof Some(var v)) {
	System.out.println("value=" + v);
} else if (present instanceof None) {
	System.out.println("no value");
}
```
