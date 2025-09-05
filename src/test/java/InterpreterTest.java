import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {

  private void assertInterprets(String src, String input, String expected) {
    Interpreter interp = new Interpreter();
    Result<String, InterpretError> res = interp.interpret(src, input);
    switch (res) {
      case Result.Ok<String, InterpretError> ok -> assertEquals(expected, ok.value());
      case Result.Err<String, InterpretError> err -> fail("Interpreter returned error: " + err.error());
      default -> fail("Unknown Result variant");
    }
  }

  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  private void assertInterpretsWithPrelude(String programSuffix, String input, String expected) {
    assertInterprets(PRELUDE + programSuffix, input, expected);
  }

  @Test
  public void read() {
    assertInterpretsWithPrelude("readInt()", "10", "10");
  }

  @Test
  public void add() {
    assertInterpretsWithPrelude("readInt() + readInt()", "10" + System.lineSeparator() + "20", "30");
  }

  @Test
  public void sub() {
    assertInterpretsWithPrelude("readInt() - readInt()", "20" + System.lineSeparator() + "10", "10");
  }

  @Test
  public void mul() {
    assertInterpretsWithPrelude("readInt() * readInt()", "2" + System.lineSeparator() + "3", "6");
  }

  @Test
  public void let() {
    assertInterpretsWithPrelude("let x = readInt(); x", "10", "10");
  }

  @Test
  public void typedLet() {
    assertInterpretsWithPrelude("let x : I32 = readInt(); x", "10", "10");
  }

  @Test
  public void chainedLet() {
    assertInterpretsWithPrelude("let x : I32 = readInt(); let y : I32 = x; y", "10", "10");
  }

}
