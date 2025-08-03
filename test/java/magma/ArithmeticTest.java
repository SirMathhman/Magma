package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.TestUtils.assertRun;

/**
 * Tests for basic arithmetic operations in Magma.
 */
public class ArithmeticTest {
    @ParameterizedTest
    @ValueSource(strings = {"-10", "-1", "0", "1", "10"})
    void digit(String input) {
        assertRun(input, input);
    }

    @Test
    void add() {
        assertRun("1 + 2", "3");
    }

    @Test
    void addTwice() {
        assertRun("1 + 2 + 3", "6");
    }

    @Test
    void addThrice() {
        assertRun("1 + 2 + 3 + 4", "10");
    }

    @Test
    void parentheses() {
        assertRun("(1 + 2) * 3", "9");
    }

    @Test
    void subtract() {
        assertRun("2 - 1", "1");
    }

    @Test
    void multiply() {
        assertRun("4 * 5", "20");
    }

    @Test
    void divide() {
        assertRun("10 / 2", "5");
    }

    @Test
    void whitespace() {
        assertRun("2+3+4", "9");
    }

    @Test
    void complexArithmetic() {
        assertRun("2 * 3 + 4 * 5", "26");
    }

    @Test
    void complexArithmeticWithParentheses() {
        assertRun("2 * (3 + 4) * 5", "70");
    }

    @Test
    void nestedParentheses() {
        assertRun("((1 + 2) * (3 + 4))", "21");
    }

    @Test
    void divisionByZero() {
        // This test expects an ArithmeticException or a specific error message
        // The implementation should handle division by zero appropriately
        assertRun("10 / 0", "Error: Division by zero");
    }
}