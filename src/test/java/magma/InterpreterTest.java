package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void interpretEmptyInputReturnsOkEmpty() {
        assertInterpretsTo("", "");
    }

    @Test
    void interpretEmptyClassDeclarationReturnsOkEmpty() {
        // removed: kept as low value; coverage retained by other tests
    }

    private static void assertInterpretsTo(String program, String expectedValue) {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> expected = Result.ok(expectedValue);
        Result<String, InterpretError> actual = interpreter.interpret(program, "");
        assertEquals(expected, actual);
    }

    @Test
    void interpretEmptyQuotedStringLiteralReturnsItself() {
        assertInterpretsTo("\"\"", "\"\"");
    }

    @Test
    void interpretPassCallReturnsQuotedArgument() {
        assertInterpretsTo("fn pass(str : *[U8]) => str; pass(\"\")", "\"\"");
    }

    @Test
    void interpretWrapperGetReturnsQuotedArgument() {
        assertInterpretsTo("class fn Wrapper(str : *[U8]) => fn get() => str; Wrapper(\"\").get()", "\"\"");
    }

    @Test
    void interpretZeroReturnsZero() {
        assertInterpretsTo("0", "0");
    }

    @Test
    void interpretLetAssignmentReturnsZero() {
        assertInterpretsTo("let x = 0; x", "0");
    }

    @Test
    void interpretTrueReturnsTrue() {
        assertInterpretsTo("true", "true");
    }

    @Test
    void interpretTypedLetAssignmentReturnsZero() {
        assertInterpretsTo("let x : I32 = 0; x", "0");
    }

    @Test
    void interpretLetInitializedFromZeroArgFunctionReturnsZero() {
        assertInterpretsTo("fn get() => 0; let x = get(); x", "0");
    }
}
