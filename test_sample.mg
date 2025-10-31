fn add(a : I32, b : I32) : I32 => a + b;

fn multiply(x : I32, y : I32) : I32 => x * y;

struct Point {
  x : I32,
  y : I32
}

fn calculate() : I32 => add(5, 3) + multiply(2, 4);
