package magma.function;

import magma.infra.TestUtils;
import org.junit.jupiter.api.Test;

public class IncrementCaptureTest {
  @Test
  void testIncrementCapture() {
    TestUtils.assertValidWithPrelude(
        "let x = readInt(); fn increment() => x++; increment(); x",
        "30",
        31);
  }
}
