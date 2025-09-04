package magma;

import org.junit.jupiter.api.Test;

public class InterpreterExtrasTest {
    @Test
    void interpretPassWithMultipleParametersReturnsFirstQuotedArg() {
        TestHelper.assertInterpretsTo("fn pass(a : *[U8], b : I32) => a; pass(\"hello\", 3)", "\"hello\"");
    }
}
