package magma;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void test() {
    Runner.run("intrinsic fn readInt() : I32; readInt()", "10");
  }
}
