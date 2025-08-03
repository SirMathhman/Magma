package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertRun;

/**
 * Tests for control flow statements in Magma.
 */
public class ControlFlowTest {
    @Test
    void ifStatement() {
        assertRun("fn test(x: I32) => { if (x > 5) { 10 } else { 5 } }\ntest(10)", "10");
    }

    @Test
    void ifStatementFalseCondition() {
        assertRun("fn test(x: I32) => { if (x > 5) { 10 } else { 5 } }\ntest(3)", "5");
    }
}