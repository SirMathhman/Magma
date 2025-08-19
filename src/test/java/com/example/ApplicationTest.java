package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ApplicationTest {
  @Test
  void empty() {
    Assertions.assertTrue(Application.run("").isEmpty());
  }
}
