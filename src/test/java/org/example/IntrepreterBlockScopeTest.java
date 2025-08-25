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

  @Test
  void functionDeclaredInsideBlockIsUsableInsideButNotOutside() {
    assertValid("let x : I32; x = { let t : I32; fn foo():I32 => 3; foo() }; x", "3");
    assertInvalid("let x : I32; x = { let t : I32; fn foo():I32 => 3; foo() }; foo()");
  }

  @Test
  void structDeclaredInsideBlockDoesNotLeakAndCanBeRedefinedOutside() {
    assertValid("let x : I32; x = { let t : I32; struct S { a : I32 }; 0 }; struct S { a : I32 }; x", "0");
  }

  @Test
  void outerVarAvailableAfterStandaloneBlocks() {
    assertValid("let x : I32; x = 42; { } { { } } x", "42");
  }
}
