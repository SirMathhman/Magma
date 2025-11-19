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
    }

    @Test
    void testInterpretMixedArithmetic() {
        assertValid("2 + 3 - 1", "4");
    }

    @Test
    void testInterpretNegativeWithUnits() {
        assertInvalid("-1U8");
    }

    @Test
    void testInterpretMultiplication() {
        assertValid("2 * 3", "6");
    }
}
