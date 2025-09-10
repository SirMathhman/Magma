package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
    private TestUtils() {
    }

    public static void assertValid(String source, String expected) {
        Interpreter interp = new Interpreter();
        Result<String, InterpretError> res = interp.interpret(source, "");
        assertTrue(res instanceof Result.Ok);
        Result.Ok<String, InterpretError> ok = (Result.Ok<String, InterpretError>) res;
        assertEquals(expected, ok.value());
    }

    public static void assertInvalid(String source) {
        Interpreter interp = new Interpreter();
        Result<String, InterpretError> res = interp.interpret(source, "");
        assertTrue(res instanceof Result.Err);
    }
}
