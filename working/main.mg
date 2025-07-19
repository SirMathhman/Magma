class fn Compiler() => {
}

```magma
fn outer() => {
 fn inner() => {
 }
}
```

```c
void inner_outer() {
}

void outer() {
}
```