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
        assertInterpretsTo("class fn Empty() => {}", "");
    }

    @Test
    void interpretDoNothingFunctionReturnsOkEmpty() {
        assertInterpretsTo("fn doNothing() => {}", "");
    }

    @Test
    void interpretTypeAliasReturnsOkEmpty() {
        assertInterpretsTo("type Temp = I32;", "");
    }

    @Test
    void interpretTwoEmptyClassesReturnsOkEmpty() {
        assertInterpretsTo("class fn Ok() => {} class fn Err() => {}", "");
    }

    @Test
    void interpretTwoClassesAndTypeAliasReturnsOkEmpty() {
        assertInterpretsTo("class fn Ok() => {} class fn Err() => {} type Result = Ok | Err;", "");
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
}
