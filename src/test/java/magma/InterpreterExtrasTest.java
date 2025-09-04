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
    
    @Test
    public void interpretLetMutAssignment() {
        TestHelper.assertInterpretsTo("let mut x = 0; x = 10; x", "10");
    }

    @Test
    public void interpretLetAssignmentNoFinalReferenceIsErr() {
        TestHelper.assertInterpretsToErr("let x = 0; x = 10;");
    }

    @Test
    public void interpretLetAssignmentWithAnotherLetAndNoFinalReferenceIsErr() {
        TestHelper.assertInterpretsToErr("let x = 0; let y = 0; x = 10;");
    }

    @Test
    public void interpretLetMutAndOtherLetWithFinalReferenceReturnsAssignedValue() {
        TestHelper.assertInterpretsTo("let mut x = 0; let y = 0; x = 10; x", "10");
    }

    @Test
    public void interpretLetMutPlusEqualsAssignment() {
        TestHelper.assertInterpretsTo("let mut x = 0; x += 10; x", "10");
    }

    @Test
    public void interpretLetPlusEqualsOnImmutableIsErr() {
        TestHelper.assertInterpretsToErr("let x = 0; x += 10;");
    }
}
