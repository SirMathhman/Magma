package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class FunctionTest {
  @Test
  void function() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }
}