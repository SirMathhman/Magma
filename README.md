StringSetListGenerator

This repository contains a small Java utility `StringSetListGenerator` with a method
`parseGroups(String)` that converts an input string into a `List<Set<String>>`.

Assumed input format:
- Groups separated by `;`
- Within a group, items separated by commas and/or whitespace

Quick compile & run (PowerShell):

```powershell
# compile
javac -d out src\main\java\com\example\StringSetListGenerator.java
# run with demo input
java -cp out com.example.StringSetListGenerator
# run with custom input
java -cp out com.example.StringSetListGenerator "one,two; three four; five"
```
