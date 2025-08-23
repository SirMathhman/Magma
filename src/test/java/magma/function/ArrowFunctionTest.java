package magma.function;

import magma.infra.TestUtils;
import org.junit.jupiter.api.Test;

public class ArrowFunctionTest {
  @Test
  void testArrowFunction() {
    TestUtils.assertValidWithPrelude(
        "fn add(first : I32, second : I32) => first + second; add(3, 4)",
        "",
        7);
  }
}
