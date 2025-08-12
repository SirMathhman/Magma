package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.annotation.Testable;

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

  @Test
  void testChar() {
    assertValid("let x : U8 = 'c';", "uint8_t x = 'c';");
  }

  @Test
  void arrayEmpty() {
    assertValid("let array : [U8; 0] = [];", "uint8_t array[0] = {};");
  }

  @Test
  void arrayOneElement() {
    assertValid("let array : [U8; 1] = [1];", "uint8_t array[1] = {1};");
  }

  @Test
  void arraySizeMismatch() {
    assertInvalid("let array : [U8; 2] = [1];");
  }

  @Test
  void arrayTypeMismatch() {
    assertInvalid("let array : [U8; 1] = [true];");
  }

  @Test
  void arrayImplicitType() {
    assertValid("let array = [1, 2, 3];", "int32_t array[3] = {1, 2, 3};");
  }

  @Test
  void arrayGetIndex() {
    assertValid("let array = [1, 2, 3]; let value = array[0];",
        "int32_t array[3] = {1, 2, 3}; int32_t value = array[0];");
  }

  @Test
  void arraySetWithMut() {
    assertValid("let mut array = [1, 2, 3]; array[0] = 42;",
        "int32_t array[3] = {1, 2, 3}; array[0] = 42;");
  }

  @Test
  void arraySetWithoutMut() {
    assertInvalid("let array = [1, 2, 3]; array[0] = 1;");
  }

  @Test
  void arraySetTypeMismatch() {
    assertInvalid("let mut array = [1, 2, 3]; array[0] = true;");
  }

  @Test
  void arrayLength() {
    assertValid("let array = [1, 2, 3]; let length = array.length;",
        "int32_t array[3] = {1, 2, 3}; usize_t length = 3;");
  }

  @Test
  void arrayLengthValid() {
    assertValid("let array = [1, 2, 3]; let length : USize = array.length;",
        "int32_t array[3] = {1, 2, 3}; usize_t length = 3;");
  }

  @Test
  void arrayLengthTypeMismatchInvalid() {
    assertInvalid("let array = [1, 2, 3]; let length : I32 = array.length;");
  }

  @Test
  void string() {
    assertValid("let x : [U8; 5] = \"hello\";", "uint8_t x[5] = {104, 101, 108, 108, 111};");
  }

  @Test
  void stringInvalidType() {
    assertInvalid("let x : U64 = \"hello\";");
  }

  @Test
  void orOperator() {
    assertValid("let x : Bool = true || false;", "bool x = true || false;");
  }

  @Test
  void andOperator() {
    assertValid("let x : Bool = true && false;", "bool x = true && false;");
  }

  @Test
  void lessThanOperator() {
    assertValid("let x : Bool = 1 < 2;", "bool x = 1 < 2;");
  }

  @Test
  void lessThanOrEqualOperator() {
    assertValid("let x : Bool = 2 <= 2;", "bool x = 2 <= 2;");
  }

  @Test
  void greaterThanOperator() {
    assertValid("let x : Bool = 3 > 2;", "bool x = 3 > 2;");
  }

  @Test
  void greaterThanOrEqualOperator() {
    assertValid("let x : Bool = 3 >= 2;", "bool x = 3 >= 2;");
  }

  @Test
  void equalOperator() {
    assertValid("let x : Bool = 2 == 2;", "bool x = 2 == 2;");
  }

  @Test
  void notEqualOperator() {
    assertValid("let x : Bool = 2 != 3;", "bool x = 2 != 3;");
  }

  @Test
  void braces() {
    assertValid("{}", "{}");
  }

  @Test
  void bracesContainsLet() {
    assertValid("{let x = 5;}", "{ int32_t x = 5; }");
  }

  @Test
  void validScope() {
    assertValid("let x = 100; {let y = x;}", "int32_t x = 100; { int32_t y = x; }");
  }

  @Test
  void invalidScope() {
    assertInvalid("{ let x = 100; } let y = x;");
  }

  @Test
  void testIf() {
    assertValid("if (true) {}", "if (true) {}");
  }

  @Test
  void testElse() {
    assertValid("if (true) {} else {}", "if (true) {} else {}");
  }

  @Test
  void testWhile() {
    assertValid("while (true) {}", "while (true) {}");
  }

  @Test
  void function() {
    assertValid("fn empty(): Void => {}", "void empty() {}");
  }

  @Test
  void functionImplicitReturnType() {
    assertValid("fn empty() => {}", "void empty() {}");
  }

  @Test
  void functionOneParam() {
    assertValid("fn empty(param: I32): Void => {}", "void empty(int32_t param) {}");
  }

  @Test
  void functionTwoParams() {
    assertValid("fn empty(param1: I32, param2: I32): Void => {}", "void empty(int32_t param1, int32_t param2) {}");
  }

  @Test
  void functionCall() {
    assertValid("fn empty() => {} empty();", "void empty() {} empty();");
  }

  @Test
  void testReturn() {
    assertValid("fn empty() => {return;}", "void empty() {return;}");
  }

  private void assertInvalid(String input) {
    Application app = new Application();
    assertThrows(ApplicationException.class, () -> {
      app.compile(input);
    });
  }
}
