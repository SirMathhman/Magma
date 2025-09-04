package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void interpretEmptyInputReturnsOkEmpty() {
    assertInterpretsToEmpty("");
    }

    @Test
    void interpretEmptyClassDeclarationReturnsOkEmpty() {
    assertInterpretsToEmpty("class fn Empty() => {}");
    }

    @Test
    void interpretDoNothingFunctionReturnsOkEmpty() {
        assertInterpretsToEmpty("fn doNothing() => {}");
    }

    private static void assertInterpretsToEmpty(String program) {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> expected = Result.ok("");
        Result<String, InterpretError> actual = interpreter.interpret(program, "");
        assertEquals(expected, actual);
    }
}
