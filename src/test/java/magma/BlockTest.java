package magma;

import org.junit.jupiter.api.Test;

public class BlockTest {
  @Test
  public void blockWithValue() {
    TestUtils.assertValidWithPrelude("{5}", "", 5);
  }

  @Test
  public void letBeforeBlockIsVisibleInsideBlock() {
    TestUtils.assertValidWithPrelude("let x = readInt(); {x}", "7", 7);
  }

  @Test
  public void letInsideBlockDoesNotLeakOutside() {
    TestUtils.assertInvalid(TestUtils.PRELUDE + "{let x = 10;} x");
  }

  @Test
  public void functionInsideBlockDoesNotLeakOutside() {
    TestUtils.assertInvalid(TestUtils.PRELUDE + "{fn get() => readInt();} get()");
  }

  @Test
  public void functionBeforeBlockIsVisibleInsideBlock() {
    TestUtils.assertValidWithPrelude("fn get() => readInt(); { get() }", "33", 33);
  }

  @Test
  public void structInsideBlockDoesNotLeakOutside() {
    TestUtils.assertInvalid(TestUtils.PRELUDE + "{struct S { f : I32 } } S");
  }

  @Test
  public void structBeforeBlockIsVisibleInsideBlock() {
    TestUtils.assertValidWithPrelude("struct S { f : I32 } { let s = S { readInt() }; s.f }", "12", 12);
  }

  @Test
  public void assignmentInsideBlockCanMutateOuter() {
    TestUtils.assertValidWithPrelude("let mut x = 0; { x = readInt(); } x", "77", 77);
  }

  
}
