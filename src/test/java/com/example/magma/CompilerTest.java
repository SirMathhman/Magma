package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
  @Test
  void compile_wrapsInput() {
    assertEquals(0, Runner.run(""));
  }
}
