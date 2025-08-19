import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "intrinsic fn readInt() : I32; ";

  @Test
  void pass() {
    assertValidWithPrelude("readInt()", "5", 5);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "3\r\n4", 7);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "5\r\n3", 2);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "5\r\n3", 15);
  }

  @Test
  void equals() {
    assertValidWithPrelude("readInt() == readInt()", "5\r\n5", 1);
  }

  @Test
  void notEquals() {
    assertValidWithPrelude("readInt() != readInt()", "5\r\n4", 1);
  }

  @Test
  void lessThan() {
    assertValidWithPrelude("readInt() < readInt()", "3\r\n4", 1);
  }

  @Test
  void lessThanEqual() {
    assertValidWithPrelude("readInt() <= readInt()", "3\r\n4", 1);
  }

  @Test
  void greaterThan() {
    assertValidWithPrelude("readInt() > readInt()", "4\r\n3", 1);
  }

  @Test
  void greaterThanEqual() {
    assertValidWithPrelude("readInt() >= readInt()", "4\r\n4", 1);
  }

  @Test
  void let() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letBool() {
    assertValidWithPrelude("let x = readInt() == 5; x", "5", 1);
  }

  @Test
  void letWithType() {
    assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithTypeBool() {
    assertValidWithPrelude("let x: Bool = readInt() == 5; x", "5", 1);
  }

  @Test
  void letMismatchedType() {
    assertInvalidWithPrelude("let x: Bool = 5;");
  }

  @Test
  void letMismatchedTypeOther() {
    assertInvalidWithPrelude("let x: I32 = true;");
  }

  @Test
  void undefinedIdentifier() {
    assertInvalid("readInt");
  }

  @Test
  void undefinedCaller() {
    assertInvalid("readInt()");
  }

  @Test
  void unknownReturn() {
    assertInvalid("intrinsic fn readInt() : Bool; readInt()");
  }

  @Test
  void invalidArgument() {
    assertInvalid("intrinsic fn readInt() : I32; readInt(5)");
  }

  @Test
  void function() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void twoFunctionsWithSameName() {
    assertInvalidWithPrelude("fn get() => readInt(); fn get() => readInt();");
  }

  @Test
  void functionWithOneParameter() {
    assertValidWithPrelude("fn get(x: I32) => x; get(readInt())", "100", 100);
  }

  @Test
  void functionWithTwoParameters() {
    assertValidWithPrelude("fn add(x: I32, y: I32) => x + y; add(readInt(), readInt())", "3\r\n4", 7);
  }

  @Test
  void functionWithMismatchedParameterAndArgumentTypes() {
    assertInvalid("fn add(x: I32, y: I32) => x + y; add(true, 5)");
  }

  @Test
  void notIntrinsic() {
    assertValid("fn readInt() => 100; readInt();", "", 100);
  }

  @Test
  void intrinsicFunctionsHaveNoBody() {
    assertInvalid("intrinsic fn readInt() => {}");
  }

  @Test
  void functionsMustHaveBodies() {
    assertInvalid("fn get() : Void;");
  }

  @Test
  void innerFunction() {
    assertValidWithPrelude("fn outer() => { fn inner() => readInt(); inner() }; outer()", "42", 42);
  }

  @Test
  void innerFunctionWhereInnerFunctionHasOneParameter() {
    assertValidWithPrelude("fn outer() => { fn inner(x: I32) => x; inner(readInt()) }; outer()", "42", 42);
  }

  @Test
  void innerFunctionWhereInnerFunctionHasTwoParameters() {
    assertValidWithPrelude("fn outer() => { fn inner(x: I32, y: I32) => x + y; inner(readInt(), readInt()) }; outer()",
        "42\r\n58", 100);
  }

  @Test
  void innerFunctionWhereOuterFunctionHasOneParameter() {
    assertValidWithPrelude("fn outer(x: I32) => { fn inner() => x; inner() }; outer(readInt())", "42", 42);
  }

  @Test
  void innerFunctionWhereOuterFunctionHasTwoParameters() {
    assertValidWithPrelude("fn outer(x: I32, y: I32) => { fn inner() => x + y; inner() }; outer(readInt(), readInt())",
        "42\r\n58", 100);
  }

  @Test
  void letFunctionType() {
    assertValidWithPrelude("let func : () => I32 = readInt; func()", "100", 100);
  }

  @Test
  void letImplicitFunctionType() {
    assertValidWithPrelude("let func = readInt; func()", "100", 100);
  }

  @Test
  void closure() {
    assertValidWithPrelude("let func : () => I32 = () => readInt(); func()", "100", 100);
  }

  @Test
  void struct() {
    assertValidWithPrelude("struct Wrapper {value : I32} let instance = Wrapper {readInt()}; instance.value", "100",
        100);
  }

  @Test
  void twoStructs() {
    assertValidWithPrelude(
        "struct Wrapper {value : I32} struct Other {value : I32} let instance = Wrapper {readInt()}; instance.value",
        "100",
        100);
  }

  @Test
  void template() {
    assertValidWithPrelude("struct Wrapper<T> {value : T} let instance = Wrapper {readInt()}; instance.value",
        "100",
        100);
  }

  @Test
  void template2() {
    assertValidWithPrelude(
        "struct Wrapper<T> {field : T} let instance = Wrapper {readInt()}; let result = Wrapper {instance.field == 5}; result.field",
        "5",
        1);
  }

  private void assertInvalidWithPrelude(String input) {
    assertInvalid(BEFORE_INPUT + input);
  }

  private void assertInvalid(String input) {
    assertThrows(CompileException.class, () -> Runner.run(input, ""));
  }

  private void assertValidWithPrelude(String input, String stdin, int expected) {
    assertValid(BEFORE_INPUT + input, stdin, expected);
  }

  private void assertValid(String input, String stdin, int expected) {
    try {
      int exit = Runner.run(input, stdin);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}