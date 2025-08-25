package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

class IntrepreterBlockScopeTest {

  @Test
  void outerVarVisibleInsideBlock() {
    assertValid("let x = 5; { x }", "5");
  }

  @Test
  void innerVarInvisibleOutsideBlock() {
    assertInvalid("let x = { let y = 2; y }; y");
  }

  @Test
  void nestedBlocksCanReadOuterVar() {
    assertValid("let x : I32; x = 5; { { x } }", "5");
  }

  @Test
  void blockInitializerWithInnerLetDoesNotLeak() {
    assertValid("let x : I32; x = { let y = 3; y }; x", "3");
  }
}
