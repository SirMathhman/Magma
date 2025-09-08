# Quickstart: Magma → C compiler

This quickstart demonstrates the minimal expected flow once a prototype exists.

1. Create a simple Magma program `hello.mg`:

```magma
func main() {
  print("Hello, Magma-to-C\n");
}
```

2. Run the compiler (prototype CLI):

```
java -jar magma-compiler.jar --input hello.mg --out-dir out/
```

3. Compile the generated C:

```
gcc out/hello.c -o hello
./hello
```

Expected: program prints `Hello, Magma-to-C` to stdout.
