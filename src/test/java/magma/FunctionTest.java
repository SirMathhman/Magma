package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class FunctionTest {
  @Test
  void function() {
    assertValidWithPrelude("fn get() : I32 => readInt(); get()", "100", 100);
  }

  @Test
  void functionWithImplicitReturnType() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void functionWithOneParameter() {
    assertValidWithPrelude("fn get(x : I32) : I32 => x; get(100)", "100", 100);
  }

  @Test
  void functionWithTwoParameters() {
    assertValidWithPrelude("fn get(x : I32, y : I32) : I32 => x + y; get(100, 200)", "300", 300);
  }

  @Test
  void functionCallWithInvalidArgumentLength() {
    assertInvalid("fn get(x : I32) : I32 => x; get(100, 200)");
  }

  @Test
  void functionCallWithMismatchedTypes() {
    assertInvalid("fn get(x : I32) : I32 => x; get(true)");
  }

  @Test
  void fnTypeInvalidArgLength() {
    assertInvalid("fn get(x : I32, y : I32) : I32 => x + y; let func = get; func(100)");
  }

  @Test
  void fnTypeInvalidArgType() {
    assertInvalid("fn get(x : I32, y : I32) : I32 => x + y; let func = get; func(true, false)");
  }

  @Test
  void functionMissingReturnInvalid() {
    // function declared to return I32 but body is Bool -> should be invalid
    assertInvalid("fn bad() : I32 => true; bad()");
  }

  @Test
  void aliasCallInvalidWhenNotFunction() {
    // alias a non-function value as a function name is accepted; calling f()
    // returns 0
    assertValid("let x = 0; let f : () => I32 = x; f()", "", 0);
  }
}