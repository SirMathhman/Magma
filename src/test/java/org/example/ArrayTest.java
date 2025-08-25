package org.example;

import org.junit.jupiter.api.Test;

public class ArrayTest {
  @Test
  public void arrayLiteralDeclaration() {
    // let x : [I32; 3] = [1, 2, 3]; x
    TestUtils.assertValid("let x : [I32; 3] = [1, 2, 3]; x[1]", "2");
  }
}
