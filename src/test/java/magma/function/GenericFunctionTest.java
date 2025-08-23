package magma.function;

import magma.infra.TestUtils;
import org.junit.jupiter.api.Test;

public class GenericFunctionTest {
  @Test
  void testGenericPassThrough() {
    TestUtils.assertValidWithPrelude(
        "fn pass<T>(value : T) => value; pass<I32>(readInt())",
        "30",
        30);
  }

  @Test
  void testTwoTypeParams() {
    TestUtils.assertValidWithPrelude(
        "fn pick<T, U>(a : T, b : U) => a; pick<I32,Bool>(readInt(), true)",
        "30",
        30);
  }
}
