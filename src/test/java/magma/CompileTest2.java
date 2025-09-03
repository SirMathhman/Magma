package magma;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompileTest2 {
  @Test
  void falseTest() {
    TestHelpers.assertValid("intrinsic fn readInt() : I32; false", "", "false");
  }

  @Test
  void let() {
    TestHelpers.assertValid("let x : I32 = readInt(); x", "10", "10");
  }

  @Test
  void multipleLetInvalidWithSameNames() {
    assertTrue(Runner.run("let x : I32 = readInt(); let x : I32 = readInt(); x", "10\r\n20") instanceof Result.Err);
  }

  @Test
  void letWithImplicitType() {
    TestHelpers.assertValid("let x = readInt(); x", "10", "10");
  }

  @Test
  void multipleLetAdd() {
    TestHelpers.assertValid("let x = readInt(); let y = readInt(); x + y", "10\r\n20", "30");
  }

  @Test
  void equalsTrue() {
    TestHelpers.assertValid("readInt() == readInt()", "10\r\n10", "true");
  }

  @Test
  void ifTrue() {
    TestHelpers.assertValid("if readInt() == readInt() 10 else 20", "10\r\n10", "10");
  }

  @Test
  void ifFalse() {
    TestHelpers.assertValid("if readInt() == readInt() 10 else 20", "10\r\n20", "20");
  }

  @Test
  void letInitWithBool() {
    TestHelpers.assertValid("let x : Bool = true; x", "", "true");
  }
}
