import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void empty() {
    assertAllValidWithPrelude("", "", "");
  }

  private static final String PRELUDE = "extern fn readInt() : I32;";

  @Test
  void readInt() {
    assertAllValidWithPrelude("readInt()", "10", "10");
  }

  @Test
  void add() {
    assertAllValidWithPrelude("readInt() + readInt()", "10\r\n30", "40");
  }

  @Test
  void subtract() {
    assertAllValidWithPrelude("readInt() - readInt()", "10\r\n30", "-20");
  }

  @Test
  void multiply() {
    assertAllValidWithPrelude("readInt() * readInt()", "10\r\n30", "300");
  }

  @Test
  void let() {
    assertAllValidWithPrelude("let x = readInt(); x", "10", "10");
  }

  private void assertAllValidWithPrelude(String source, String stdIn, String stdOut) {
    assertAllValid(PRELUDE + " " + source, stdIn, stdOut);
  }

  @Test
  void assign() {
    assertAllValidWithPrelude("let mut x = 20; x = readInt(); x", "10", "10");
  }

  @Test
  void assignInvalidWithoutMut() {
    assertAllInvalid("let x = 20; x = readInt(); x");
  }

  @Test
  void braces() {
    assertAllValidWithPrelude("{readInt()}", "100", "100");
  }

  @Test
  void trueTest() {
    assertAllValidWithPrelude("true", "", "true");
  }

  @Test
  void falseTest() {
    assertAllValidWithPrelude("false", "", "false");
  }

  @Test
  void equalsTest() {
    assertAllValidWithPrelude("readInt() == readInt()", "10\r\n30", "false");
  }

  @Test
  void ifTest() {
    assertAllValidWithPrelude("if (true) readInt() else 0", "10", "10");
  }

  @Test
  void ifHasEqualsInCondition() {
    assertAllValidWithPrelude("if (readInt() == readInt()) 1 else 0", "10\r\n10", "1");
  }

  @Test
  void postIncrementTest() {
    assertAllValidWithPrelude("let mut x = readInt(); x++; x", "1", "2");
  }

  @Test
  void addAssign() {
    assertAllValidWithPrelude("let mut x = readInt(); x += 2; x", "1", "3");
  }

  @Test
  void whileTest() {
    assertAllValidWithPrelude("let mut x = readInt(); while (x < 5) x++; x", "1", "5");
  }

  @Test
  void forTest() {
    // Sum 0..9 when readInt() returns 10 -> 45
    assertAllValidWithPrelude("let mut sum = 0; for (let mut i = 0; i < readInt(); i++) { sum += i; }; sum", "10",
        "45");
  }

  @Test
  void functionTest() {
    assertAllValidWithPrelude("fn get() => readInt(); get()", "100", "100");
  }

  @Test
  void structTest() {
    assertAllValidWithPrelude("struct Wrapper { field : I32 } let struct = Wrapper { readInt() }; struct.field", "100",
        "100");
  }

  @Test
  void implTest() {
    assertAllValidWithPrelude("struct Empty {} impl Empty { fn get() => readInt(); } let value = Empty {}; value.get()",
        "100", "100");
  }

  @Test
  void traitTest() {
    assertAllValidWithPrelude(
        "struct Empty {} trait Getter { fn get() : I32; } impl Getter for Empty { fn get() => readInt(); } let value = Empty {}; value.get()",
        "100", "100");
  }

  private void assertAllInvalid(String source) {
    assertInvalid(new TSExecutor(), source);
    assertInvalid(new CExecutor(), source);
  }

  private void assertInvalid(Executor executor, String source) {
    assertTrue(new Runner(executor).run(PRELUDE + " " + source, "") instanceof Err,
        "LANG --- " + executor.getTargetLanguage() + ": Invalid code produced.");
  }

  private void assertValid(Executor executor, String source, String stdIn, String stdOut) {
    Runner runner = new Runner(executor);
    Result<String, RunError> result = runner.run(source, stdIn);
    switch (result) {
      case Err(var error) ->
        org.junit.jupiter.api.Assertions.fail("Lang --- " + executor.getTargetLanguage() + ": " + error.toString());
      case Ok(var value) -> org.junit.jupiter.api.Assertions.assertEquals(
          stdOut,
          value,
          "LANG " + executor.getTargetLanguage() + ": output mismatch");
    }
  }

  private void assertAllValid(String source, String stdIn, String stdOut) {
    assertValid(new TSExecutor(), source, stdIn, stdOut);
    assertValid(new CExecutor(), source, stdIn, stdOut);
  }
}
