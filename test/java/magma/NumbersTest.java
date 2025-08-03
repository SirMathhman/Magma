package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertRun;

/**
 * Tests for number handling in Magma (negative numbers, zero, large numbers).
 */
public class NumbersTest {
    // Tests for negative numbers in different contexts
    @Test
    void negativeNumbersInArithmetic() {
        assertRun("-5 + -3", "-8");
        assertRun("-5 - -3", "-2");
        assertRun("-5 * -3", "15");
        assertRun("-15 / -3", "5");
    }

    @Test
    void negativeNumbersAsClassFields() {
        assertRun("class fn Wrapper() => {let x = -50;}\nWrapper().x", "-50");
    }

    @Test
    void negativeNumbersAsFunctionParameters() {
        assertRun("fn test(a: I32) => { a * 2 }\ntest(-5)", "-10");
    }

    @Test
    void negativeNumbersAsClassMethodParameters() {
        assertRun("class fn Calculator() => { fn multiply(a: I32, b: I32) => { a * b } }\nCalculator().multiply(-3, 4)", "-12");
    }

    // Tests for zero in different contexts
    @Test
    void zeroInArithmetic() {
        assertRun("0 + 5", "5");
        assertRun("5 - 0", "5");
        assertRun("0 * 5", "0");
        assertRun("0 / 5", "0");
    }

    @Test
    void zeroAsClassFields() {
        assertRun("class fn Wrapper() => {let x = 0;}\nWrapper().x", "0");
    }

    @Test
    void zeroAsFunctionParameters() {
        assertRun("fn test(a: I32) => { a + 10 }\ntest(0)", "10");
    }

    @Test
    void zeroAsClassMethodParameters() {
        assertRun("class fn Calculator() => { fn add(a: I32, b: I32) => { a + b } }\nCalculator().add(0, 7)", "7");
    }

    // Tests for large numbers
    @Test
    void largeNumbers() {
        assertRun("2147483647", "2147483647"); // Max int value
        assertRun("-2147483648", "-2147483648"); // Min int value
    }

    @Test
    void largeNumbersInArithmetic() {
        assertRun("2147483647 + 1", "-2147483648"); // Overflow to min int
        assertRun("-2147483648 - 1", "2147483647"); // Underflow to max int
    }

    @Test
    void largeNumbersAsClassFields() {
        assertRun("class fn Wrapper() => {let x = 2147483647;}\nWrapper().x", "2147483647");
    }

    @Test
    void largeNumbersAsFunctionParameters() {
        assertRun("fn test(a: I32) => { a }\ntest(2147483647)", "2147483647");
    }
}