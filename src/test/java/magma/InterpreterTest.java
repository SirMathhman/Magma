package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void interpretEmptyInputReturnsOkEmpty() {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> expected = Result.ok("");
        Result<String, InterpretError> actual = interpreter.interpret("", "");
        assertEquals(expected, actual);
    }
}
