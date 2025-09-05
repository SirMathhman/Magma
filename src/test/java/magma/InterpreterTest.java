package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
    @Test
    void testInterpretEmptyString() {
        Interpreter interpreter = new Interpreter();
        Result<String, InterpretError> result = interpreter.interpret("");
        assertTrue(result instanceof Ok);
        assertEquals("", ((Ok<String, InterpretError>) result).getValue());
    }
}
