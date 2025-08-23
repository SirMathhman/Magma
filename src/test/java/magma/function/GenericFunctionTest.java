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
}
