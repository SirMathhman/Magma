package magma;

import static org.junit.jupiter.api.Assertions.fail;

import magma.run.CExecutor;
import magma.run.Executor;
import magma.run.RunError;
import magma.run.Runner;
import magma.run.TSExecutor;
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
  void undefined() {
    assertAllInvalid("readInt");
  }

  @Test
  void readIntTooManyArguments() {
    assertAllInvalidWithPrelude("readInt(1, 2)");
  }

  @Test
  void add() {
    assertAllValidWithPrelude("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void subtract() {
    assertAllValidWithPrelude("readInt() - readInt()", "10\r\n20", "-10");
  }

  @Test
  void multiply() {
    assertAllValidWithPrelude("readInt() * readInt()", "10\r\n20", "200");
  }

  @Test
  void divide() {
    assertAllValidWithPrelude("readInt() / readInt()", "20\r\n10", "2");
  }

  @Test
  void let() {
    assertAllValidWithPrelude("let x : I32 = readInt(); x", "10", "10");
  }

  @Test
  void letWithImplicitType() {
    assertAllValidWithPrelude("let x = readInt(); x", "10", "10");
  }

  @Test
  void letInvalidWithDuplicateName() {
    assertAllInvalidWithPrelude("let x : I32 = readInt(); let x : I32 = readInt();");
  }

  @Test
  void letInvalidWithMismatchedTypes() {
    assertAllInvalidWithPrelude("let x : I32 = readInt;");
  }

  @Test
  void functionType() {
    assertAllValidWithPrelude("let func : () => I32 = readInt; func()", "100", "100");
  }

  @Test
  void assign() {
    assertAllValidWithPrelude("let mut x = 5; x = readInt(); x", "10", "10");
  }

  @Test
  void assignInvalidWithoutMut() {
    assertAllInvalidWithPrelude("let x = 5; x = readInt(); x");
  }

  @Test
  void assignInvalidWhenLhsUndefined() {
    assertAllInvalidWithPrelude("let x = 5; y = readInt(); x");
  }

  @Test
  void assignInvalidTypeMismatch() {
    assertAllInvalidWithPrelude("let mut x = 5; x = readInt; x");
  }

  @Test
  void assignBetweenLet() {
    assertAllValidWithPrelude("let mut x = 0; x = readInt(); let y = x; y", "100", "100");
  }

  @Test
  void assignInvalidWhenLhsIsFunction() {
    assertAllInvalidWithPrelude("readInt = 5;");
  }

  @Test
  void brokenInitialization() {
    assertAllValidWithPrelude("let x : I32; x = readInt(); x", "100", "100");
  }

  @Test
  void brokenInitializationWithMut() {
    assertAllValidWithPrelude("let mut x : I32; x = 10; x = readInt(); x", "100", "100");
  }

  @Test
  void brokenInitializationInvalidWithoutMut() {
    assertAllInvalidWithPrelude("let x : I32; x = 10; x = readInt(); x");
  }

  @Test
  void letInvalidWhenNotInitialized() {
    assertAllInvalidWithPrelude("let x : I32;");
  }

  @Test
  void letMultiple() {
    assertAllValidWithPrelude("let mut x = 0; let mut y = 1; x = readInt(); y = readInt(); x + y", "100\r\n200", "300");
  }

  @Test
  void trueTest() {
    assertAllValid("true", "", "true");
  }

  @Test
  void falseTest() {
    assertAllValid("false", "", "false");
  }

  @Test
  void letHasBoolType() {
    assertAllValidWithPrelude("let x : Bool = true; x", "", "true");
  }

  @Test
  void equalsTrueTest() {
    assertAllValidWithPrelude("readInt() == readInt()", "100\r\n100", "true");
  }

  @Test
  void equalsFalseTest() {
    assertAllValidWithPrelude("readInt() == readInt()", "100\r\n200", "false");
  }

  @Test
  void equalsInvalidMismatchedTypes() {
    assertAllInvalidWithPrelude("5 == readInt");
  }

  @Test
  void lessThan() {
    assertAllValidWithPrelude("readInt() < readInt()", "100\r\n200", "true");
  }

  @Test
  void ifTrue() {
    assertAllValidWithPrelude("if (readInt() == 100) 3 else 4", "100", "3");
  }

  @Test
  void ifFalse() {
    assertAllValidWithPrelude("if (readInt() == 100) 3 else 4", "200", "4");
  }

  @Test
  void ifInvalidWhenConditionNotBool() {
    assertAllInvalidWithPrelude("if (5) 3 else 4");
  }

  @Test
  void ifStatement() {
    assertAllValidWithPrelude("let x : I32; if (readInt() == 100) x = 10; else x = 20; x", "100", "10");
  }

  @Test
  void letInitWithIf() {
    assertAllValidWithPrelude("let x : I32 = if (readInt() == 100) 3 else 4; x", "100", "3");
  }

  @Test
  void braces() {
    assertAllValidWithPrelude("{readInt()}", "100", "100");
  }

  @Test
  void bracesContainsLet() {
    assertAllValidWithPrelude("{let x = readInt(); x}", "100", "100");
  }

  @Test
  void bracesCanAccessLetBefore() {
    assertAllValidWithPrelude("let x = readInt(); {x}", "100", "100");
  }

  @Test
  void bracesDoNotLeakDeclarations() {
    assertAllInvalidWithPrelude("{let x = readInt();} x");
  }

  @Test
  void bracesRhsLet() {
    assertAllValidWithPrelude("let x = {readInt()}; x", "100", "100");
  }

  @Test
  void postIncrement() {
    assertAllValidWithPrelude("let mut x = readInt(); x++; x", "0", "1");
  }

  @Test
  void postIncrementInvalidWithoutMut() {
    assertAllInvalidWithPrelude("let x = readInt(); x++; x");
  }

  @Test
  void postIncrementMustBeNumeric() {
    assertAllInvalidWithPrelude("let mut x = readInt; x++;");
  }

  @Test
  void addAssign() {
    assertAllValidWithPrelude("let mut x = readInt(); x += 5; x", "0", "5");
  }

  @Test
  void addAssignInvalidWhenNotMutable() {
    assertAllInvalidWithPrelude("let x = readInt(); x += 5; x");
  }

  @Test
  void addAssignInvalidWhenNotNumber() {
    assertAllInvalidWithPrelude("let mut x = readInt; x += 5; x");
  }

  @Test
  void whileTest() {
    assertAllValidWithPrelude(
        "let mut sum = 0; let mut counter = 0; let amount = readInt(); while (counter < amount) { sum += counter; counter++; } sum",
        "10",
        "45");
  }

  @Test
  void functionTest() {
    assertAllValidWithPrelude("fn get() => readInt(); get()", "100", "100");
  }

  @Test
  void twoFunctions() {
    assertAllValidWithPrelude("fn get() => readInt(); fn get2() => get(); get2()", "100", "100");
  }

  @Test
  void functionsInvalidWithDuplicateNames() {
    assertAllInvalidWithPrelude("fn get() => 1; fn get() => 2; get()");
  }

  @Test
  void functionWithOneParameter() {
    assertAllValidWithPrelude("fn get(x : I32) => x; get(100)", "100", "100");
  }

  @Test
  void functionWithTwoParameters() {
    assertAllValidWithPrelude("fn get(x : I32, y : I32) => x + y; get(100, 200)", "100\r\n200", "300");
  }

  @Test
  void functionCallMissingArguments() {
    assertAllInvalidWithPrelude("fn get(x : I32) => x; get()");
  }

  @Test
  void functionCallInvalidOnNonFunction() {
    assertAllInvalidWithPrelude("fn get(x : I32) => x; 5()");
  }

  @Test
  void functionCallInvalidMismatchedArgumentType() {
    assertAllInvalidWithPrelude("fn get(x : I32) => x; get(true)");
  }

  @Test
  void functionWithExplicitReturnType() {
    assertAllValidWithPrelude("fn get() : I32 => readInt(); get()", "100", "100");
  }

  @Test
  void functionInvalidWhenTwoParamsWithSameName() {
    assertAllInvalidWithPrelude("fn get(x : I32, x : I32) => x; get(100, 200)");
  }

  @Test
  void functionReturnTypeMismatch() {
    assertAllInvalidWithPrelude("fn get() : I32 => 5; let x : Bool = get();");
  }

  @Test
  void functionHasBraces() {
    assertAllValidWithPrelude("fn get() => { readInt() } get()", "100", "100");
  }

  @Test
  void functionHasLetStatementInBody() {
    assertAllValidWithPrelude("fn get() => { let x = readInt(); x } get()", "100", "100");
  }

  @Test
  void structWithOneField() {
    assertAllValidWithPrelude("struct Point { x : I32 } let p = Point { readInt() }; p.x", "100", "100");
  }

  private void assertAllInvalidWithPrelude(String source) {
    assertAllInvalid(PRELUDE + " " + source);
  }

  private void assertAllValidWithPrelude(String source, String stdIn, String stdOut) {
    assertAllValid(PRELUDE + " " + source, stdIn, stdOut);
  }

  private void assertAllInvalid(String source) {
    assertInvalid(new TSExecutor(), source);
    assertInvalid(new CExecutor(), source);
  }

  private void assertInvalid(Executor executor, String source) {
    Result<String, RunError> result = new Runner(executor).run(PRELUDE + " " + source, "");
    if (result instanceof Err(var error)) {
      var maybeCause = error.maybeCause();
      if (maybeCause.isPresent() && maybeCause.get() instanceof CompileError) {
      } else {
        fail("LANG --- " + executor.getTargetLanguage() + ": Expected a compilation error.");
      }
    } else {
      fail("LANG --- " + executor.getTargetLanguage() + ": Expected an invalid case.");
    }
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
