package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertRun;

/**
 * Tests for complex expressions and nested contexts in Magma.
 */
public class ComplexExpressionsTest {
    @Test
    void complexExpressionsWithNegatives() {
        assertRun("2 * -3 + 4 * -5", "-26");
        assertRun("-2 * (3 + -4) * 5", "10");
    }

    @Test
    void mixedOperationsWithPrecedence() {
        assertRun("2 + 3 * 4 - 5 / 5", "13");
        assertRun("(2 + 3) * (4 - 5 / 5)", "15");
    }
}