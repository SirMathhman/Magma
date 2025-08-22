package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class FunctionTest {
  @Test
  void function() {
    assertValidWithPrelude("fn get() : I32 => readInt(); get()", "100", 100);
  }

  @Test
  void functionWithImplicitReturnType() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void functionWithOneParameter() {
    assertValidWithPrelude("fn get(x : I32) : I32 => x; get(100)", "100", 100);
  }
}