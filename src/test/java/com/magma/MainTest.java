package com.magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testInterpretSimpleValues() {
        final Result<String, String> result1 = Main.interpret("5");
        assertInstanceOf(Ok.class, result1);
        final Ok<String, String> ok1 = (Ok<String, String>) result1;
        assertEquals("5", ok1.getValue());

        final Result<String, String> result2 = Main.interpret("5U8");
        assertInstanceOf(Ok.class, result2);
        final Ok<String, String> ok2 = (Ok<String, String>) result2;
        assertEquals("5", ok2.getValue());
    }

    @Test
    void testInterpretAddition() {
        final Result<String, String> result3 = Main.interpret("2 + 3");
        assertInstanceOf(Ok.class, result3);
        final Ok<String, String> ok3 = (Ok<String, String>) result3;
        assertEquals("5", ok3.getValue());

        final Result<String, String> result4 = Main.interpret("2U8 + 10");
        assertInstanceOf(Ok.class, result4);
        final Ok<String, String> ok4 = (Ok<String, String>) result4;
        assertEquals("12", ok4.getValue());

        final Result<String, String> result6 = Main.interpret("2U8 + 3U8");
        assertInstanceOf(Ok.class, result6);
        final Ok<String, String> ok6 = (Ok<String, String>) result6;
        assertEquals("5", ok6.getValue());

        final Result<String, String> result7 = Main.interpret("2 + 3 + 4U8");
        assertInstanceOf(Ok.class, result7);
        final Ok<String, String> ok7 = (Ok<String, String>) result7;
        assertEquals("9", ok7.getValue());
    }

    @Test
    void testInterpretUnitMismatch() {
        final Result<String, String> result5 = Main.interpret("10U8 + 2U16");
        assertInstanceOf(Err.class, result5);
        final Err<String, String> err1 = (Err<String, String>) result5;
        assertEquals("Cannot operate on values with different units",
                err1.getError());
    }

    @Test
    void testInterpretSubtraction() {
        final Result<String, String> result8 = Main.interpret("3U8 - 2U8");
        assertInstanceOf(Ok.class, result8);
        final Ok<String, String> ok8 = (Ok<String, String>) result8;
        assertEquals("1", ok8.getValue());
    }

    @Test
    void testInterpretMixedArithmetic() {
        final Result<String, String> result9 = Main.interpret("2 + 3 - 1");
        assertInstanceOf(Ok.class, result9);
        final Ok<String, String> ok9 = (Ok<String, String>) result9;
        assertEquals("4", ok9.getValue());
    }

    @Test
    void testInterpretNegativeWithUnits() {
        final Result<String, String> result10 = Main.interpret("-1U8");
        assertInstanceOf(Err.class, result10);
        final Err<String, String> err2 = (Err<String, String>) result10;
        assertEquals("Negative values with units are not allowed",
                err2.getError());
    }
}

