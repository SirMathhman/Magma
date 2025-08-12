package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

public class ApplicationTest {
  static Stream<Object[]> typeProvider() {
    return Stream.of(
        new Object[] { "I8", "int8_t" },
        new Object[] { "I16", "int16_t" },
        new Object[] { "I32", "int32_t" },
        new Object[] { "I64", "int64_t" },
        new Object[] { "U8", "uint8_t" },
        new Object[] { "U16", "uint16_t" },
        new Object[] { "U32", "uint32_t" },
        new Object[] { "U64", "uint64_t" });
  }

  @ParameterizedTest
  @MethodSource("typeProvider")
  void letTypeParameterized(String magmaType, String cType) {
    assertValid("let a : " + magmaType + " = 42;", cType + " a = 42;");
  }

  static Stream<Object[]> annotatedProvider() {
    return Stream.of(
        new Object[] { "I8", "int8_t" },
        new Object[] { "I16", "int16_t" },
        new Object[] { "I32", "int32_t" },
        new Object[] { "I64", "int64_t" },
        new Object[] { "U8", "uint8_t" },
        new Object[] { "U16", "uint16_t" },
        new Object[] { "U32", "uint32_t" },
        new Object[] { "U64", "uint64_t" });
  }

  @ParameterizedTest
  @MethodSource("annotatedProvider")
  void annotatedNumberParameterized(String magmaType, String cType) {
    assertValid("let b = 99" + magmaType + ";", cType + " b = 99;");
  }

  private void assertValid(String input, String expected) {
    Application app = new Application();
    try {
      String result = app.compile(input);
      assertEquals(expected, result);
    } catch (Exception e) {
      org.junit.jupiter.api.Assertions.fail("Unexpected exception: " + e.getMessage());
    }
  }

  @Test
  void valid() {
    assertValid("", "");
  }

  @Test
  void let() {
    assertValid("let x = 100;", "int32_t x = 100;");
  }

  @Test
  void letName() {
    assertValid("let y = 100;", "int32_t y = 100;");
  }

  @Test
  void letValue() {
    assertValid("let z = 200;", "int32_t z = 200;");
  }

  @Test
  void letType() {
    assertValid("let z : I32 = 200;", "int32_t z = 200;");
  }

  @Test
  void annotatedNumber() {
    assertValid("let z = 200I32;", "int32_t z = 200;");
  }

  @Test
  void testTrue() {
    assertValid("let x : Bool = true;", "bool x = true;");
  }

  @Test
  void testFalse() {
    assertValid("let x : Bool = false;", "bool x = false;");
  }

  @Test
  void implicitBool() {
    assertValid("let x = true;", "bool x = true;");
  }

  @Test
  void invalid() {
    assertInvalid("not empty");
  }

  @Test
  void letTypeMismatch() {
    assertInvalid("let x : I32 = true;");
  }

  @Test
  void validMut() {
    assertValid("let mut x = 100; x = 200;", "int32_t x = 100; x = 200;");
  }

  @Test
  void invalidMut() {
    assertInvalid("let x = 100; x = 200;");
  }

  @Test
  void assignmentTypeMismatch() {
    assertInvalid("let mut x = 100; x = true;");
  }

  private void assertInvalid(String input) {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> {
      app.compile(input);
    });
  }
}
