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
  public void typedBoolAssignNumberErr() {
    assertErrorsWithPrelude("let x : Bool = 0;", "");
  }

  @Test
  public void typedI32AssignBoolErr() {
    assertErrorsWithPrelude("let x : I32 = true;", "");
  }

  @Test
  public void untypedNumericToBoolErr() {
    assertErrorsWithPrelude("let x = 0; let y : Bool = x;", "");
  }

  @Test
  public void literalAddition() {
    assertInterprets("2 + 4", "", "6");
  }

  @Test
  public void literalPlusReadInt() {
    // mixed literal + readInt(): first operand is literal, second from input
    assertInterpretsWithPrelude("3 + readInt()", "5", "8");
  }

  @Test
  public void literalSubtraction() {
    assertInterprets("2 - 1", "", "1");
  }

  @Test
  public void literalPlusBoolShouldError() {
    assertErrors("1 + true", "");
  }

  @Test
  public void readIntPlusBoolShouldError() {
    assertErrorsWithPrelude("readInt() + true", "");
  }

  @Test
  public void boolPlusLiteralShouldError() {
    assertErrors("true + 1", "");
  }

  @Test
  public void readIntPlusLiteralPlusReadInt() {
    assertInterpretsWithPrelude("readInt() + 3 + readInt()", "1" + System.lineSeparator() + "5", "9");
  }

  @Test
  public void letInitExprEval() {
    assertInterpretsWithPrelude("let x = 3 + 5; x", "", "8");
  }

  @Test
  public void ifTrueThen3Else4() {
    assertInterprets("if (true) 3 else 4", "", "3");
  }

  @Test
  public void ifNonBoolCondShouldError() {
    assertErrors("if (0) 1 else 1", "");
  }

  @Test
  public void mutableLetAssignmentReads() {
    assertInterpretsWithPrelude("let mut x = 0; x = readInt(); x", "3", "3");
  }

  @Test
  public void assignToImmutableShouldError() {
    assertErrorsWithPrelude("let x = 0; x = readInt();", "");
  }

  @Test
  public void assignImmutableOtherLetsErr() {
    assertErrorsWithPrelude("let x = 0; let y = 100; x = readInt();", "");
  }

  @Test
  public void postIncMutableVar() {
    assertInterpretsWithPrelude("let mut x = readInt(); x++; x", "2", "3");
  }

  @Test
  public void simpleFnReturningReadInt() {
    assertInterpretsWithPrelude("fn get() => readInt(); get()", "2", "2");
  } 

  @Test
  public void simpleFnReturningBool() {
    assertInterpretsWithPrelude("fn get() => true; get()", "", "true");
  }

  @Test
  public void fnBoolAssignI32Err() {
    assertErrorsWithPrelude("fn get() => true; let x : I32 = get();", "");
  }
}
