package com.magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.magma.result.Ok;
import com.magma.result.Result;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testInterpret() {
        final Result<String, String> result1 = Main.interpret("5");
        assertInstanceOf(Ok.class, result1);
        final Ok<String, String> ok1 = (Ok<String, String>) result1;
        assertEquals("5", ok1.getValue());

        final Result<String, String> result2 = Main.interpret("5U8");
        assertInstanceOf(Ok.class, result2);
        final Ok<String, String> ok2 = (Ok<String, String>) result2;
        assertEquals("5", ok2.getValue());

        final Result<String, String> result3 = Main.interpret("2 + 3");
        assertInstanceOf(Ok.class, result3);
        final Ok<String, String> ok3 = (Ok<String, String>) result3;
        assertEquals("5", ok3.getValue());

        final Result<String, String> result4 = Main.interpret("2U8 + 10");
        assertInstanceOf(Ok.class, result4);
        final Ok<String, String> ok4 = (Ok<String, String>) result4;
        assertEquals("12", ok4.getValue());
    }
}

