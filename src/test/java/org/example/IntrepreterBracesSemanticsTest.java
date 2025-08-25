package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

class IntrepreterBracesSemanticsTest {

  @Test
  void emptyBlockAsValueIsInvalid() {
    assertInvalid("{}");
  }

  @Test
  void blockWithOnlyLetNoFinalExprIsInvalid() {
    assertInvalid("{ let x = 1; }");
  }

  @Test
  void blockWithOnlyTypedLetNoFinalExprIsInvalid() {
    assertInvalid("{ let x : I32; }");
  }

  @Test
  void nestedBlocksReturnInnerValue() {
    assertValid("{ { 7 } }", "7");
  }

  @Test
  void blockWithStatementsThenExprIsValid() {
    assertValid("{ let x = 1; x }", "1");
  }

  @Test
  void standaloneBlocksAsStatementsAreAllowed() {
    assertValid("let x : I32; x = 5; { } { { } } x", "5");
  }
}
