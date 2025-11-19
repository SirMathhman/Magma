package com.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void testMain() {
        assertTrue(true, "Basic test to verify test setup");
    }

    @Test
    void testInterpret() {
        assertEquals("5", Main.interpret("5"));
    }
}

