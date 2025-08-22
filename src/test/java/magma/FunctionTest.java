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
  void letFunctionType() {
    assertValidWithPrelude("let func : () => I32 = readInt; func()", "100", 100);
  }

  @Test
  void letFunctionTypeWithOneParameter() {
    assertValidWithPrelude("fn get(x : I32) : I32 => x; let func : (I32) => I32 = get; func(readInt())", "100", 100);
  }

  @Test
  void letFunctionTypeWithTwoParameters() {
    assertValidWithPrelude(
        "fn get(x : I32, y : I32) : I32 => x + y; let func : (I32, I32) => I32 = get; func(100, 200)",
        "300", 300);
  }

  @Test
  void letImplicitFunctionType() {
    assertValidWithPrelude("fn get(x : I32, y : I32) : I32 => x + y; let func = get; func(100, 200)", "300", 300);
  }
}