package com.magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;
import org.junit.jupiter.api.Test;

class MainTest {
    /**
     * Asserts that interpreting the input produces a valid (Ok) result with
     * the expected value.
     *
     * @param input         The input string to interpret
     * @param expectedValue The expected value
     */
    private static void assertValid(final String input,
            final String expectedValue) {
        final Result<String, String> result = Main.interpret(input);
        switch (result) {
            case Ok<String, String> ok -> assertEquals(expectedValue,
                    ok.getValue());
            case Err<String, String> err -> fail(
                    "Expected Ok but got Err: " + err.getError());
        }
    }

    /**
     * Asserts that interpreting the input produces an invalid (Err) result.
     *
     * @param input The input string to interpret
     */
    private static void assertInvalid(final String input) {
        final Result<String, String> result = Main.interpret(input);
        switch (result) {
            case Ok<String, String> ok -> fail(
                    "Expected Err but got Ok: " + ok.getValue());
            case Err<String, String> err -> {
                // Expected case - err variable is required by pattern matching
                // but not used in this assertion
                err.getClass();
            }
        }
    }

    @Test
    void testInterpretSimpleValues() {
        assertValid("5", "5");
        assertValid("5U8", "5");
        assertValid("true", "true");
        assertValid("false", "false");
    }

    @Test
    void testInterpretAddition() {
        assertValid("2 + 3", "5");
        assertValid("2U8 + 10", "12");
        assertValid("2U8 + 3U8", "5");
        assertValid("2 + 3 + 4U8", "9");
    }

    @Test
    void testInterpretUnitMismatch() {
        assertInvalid("10U8 + 2U16");
    }

    @Test
    void testInterpretSubtraction() {
        assertValid("3U8 - 2U8", "1");
        assertInvalid("100U8 - 200U8");
    }

    @Test
    void testInterpretMixedArithmetic() {
        assertValid("2 + 3 - 1", "4");
    }

    @Test
    void testInterpretNegativeWithUnits() {
        assertValid("-1U8", "-1");
        assertValid("-1I8", "-1");
        assertValid("-1U8 + 2", "1");
        assertValid("2 + -1U8", "1");
    }

    @Test
    void testInterpretMultiplication() {
        assertValid("2 * 3", "6");
    }

    @Test
    void testInterpretVariables() {
        assertValid("let x : 1U8 = 1U8; x", "1");
        assertInvalid("let x : 5U8 = 1U8; x");
        assertValid("let x : U8 = 10; x", "10");
        assertValid("let x : U8 = 0;", "");
        assertInvalid("let x : U8 = -1;");
        assertInvalid("let x : U8 = 256;");
        assertValid("extern let x : U8;", "");
    }

    @Test
    void testInterpretValueRange() {
        // U8
        assertValid("255U8", "255");
        assertInvalid("256U8");
        assertInvalid("255U8 + 1U8");
        // U16
        assertValid("65535U16", "65535");
        assertInvalid("65536U16");
        // U32
        assertValid("4294967295U32", "4294967295");
        assertInvalid("4294967296U32");
        // U64
        assertValid("18446744073709551615U64", "18446744073709551615");
        assertInvalid("18446744073709551616U64");
        // I8
        assertValid("127I8", "127");
        assertValid("-128I8", "-128");
        assertInvalid("128I8");
        assertInvalid("-129I8");
        // I16
        assertValid("32767I16", "32767");
        assertValid("-32768I16", "-32768");
        assertInvalid("32768I16");
        assertInvalid("-32769I16");
        // I32
        assertValid("2147483647I32", "2147483647");
        assertValid("-2147483648I32", "-2147483648");
        assertInvalid("2147483648I32");
        assertInvalid("-2147483649I32");
        // I64
        assertValid("9223372036854775807I64", "9223372036854775807");
        assertValid("-9223372036854775808I64", "-9223372036854775808");
    }
}
