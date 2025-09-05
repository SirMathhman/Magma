import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {

  private void assertInterprets(String src, String input, String expected) {
    assertOk(run(src, input), expected);
  }

  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  // Helper to run the interpreter and centralize instantiation.
  private Result<String, InterpretError> run(String src, String input) {
    Interpreter interp = new Interpreter();
    return interp.interpret(src, input);
  }

  private void assertInterpretsWithPrelude(String programSuffix, String input, String expected) {
    assertInterprets(PRELUDE + programSuffix, input, expected);
  }

  private void assertErrors(String src, String input) {
    assertErr(run(src, input));
  }

  // Helper to assert an Ok result with expected value
  private void assertOk(Result<String, InterpretError> res, String expected) {
    switch (res) {
      case Result.Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
      case Result.Err<String, InterpretError>(InterpretError error) -> fail("Interpreter returned error: " + error);
      default -> fail("Unknown Result variant");
    }
  }

  // Helper to assert an Err result
  private void assertErr(Result<String, InterpretError> res) {
    switch (res) {
      case Result.Err<String, InterpretError>(InterpretError error) ->
        org.junit.jupiter.api.Assertions.assertNotNull(error.message());
      case Result.Ok<String, InterpretError>(String value) -> fail("Expected error but got value: " + value);
      default -> fail("Unknown Result variant");
    }
  }

  private void assertErrorsWithPrelude(String programSuffix, String input) {
    assertErrors(PRELUDE + programSuffix, input);
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

  @Test
  public void duplicateLetShouldError() {
    assertErrorsWithPrelude("let x : I32 = readInt(); let x : I32 = 0;", "");
  }

  @Test
  public void booleanTrue() {
    assertInterpretsWithPrelude("true", "", "true");
  }

  @Test
  public void booleanFalse() {
    assertInterpretsWithPrelude("false", "", "false");
  }

  @Test
  public void typedBoolAssignFromNumberShouldError() {
    assertErrorsWithPrelude("let x : Bool = 0;", "");
  }

  @Test
  public void typedI32AssignFromBoolShouldError() {
    assertErrorsWithPrelude("let x : I32 = true;", "");
  }

}
