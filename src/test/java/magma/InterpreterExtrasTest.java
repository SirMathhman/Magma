package magma;

import org.junit.jupiter.api.Test;

public class InterpreterExtrasTest {
    @Test
    void interpretPassWithMultipleParametersReturnsFirstQuotedArg() {
        TestHelper.assertInterpretsTo("fn pass(a : *[U8], b : I32) => a; pass(\"hello\", 3)", "\"hello\"");
    }

    @Test
    void interpretTypedLetWithOtherLetAndAssignmentReturnsValue() {
        TestHelper.assertInterpretsTo("let x : I32; let y = 10; x = 0; x", "0");
    }
}
