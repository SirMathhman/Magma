package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
    private TestUtils() {}

    public static void assertValid(String source, String expected) {
        Interpreter interp = new Interpreter();
        Result<String, String> res = interp.interpret(source, "");
        assertTrue(res instanceof Result.Ok);
        Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
        assertEquals(expected, ok.value());
    }

    public static void assertInvalid(String source) {
        Interpreter interp = new Interpreter();
        Result<String, String> res = interp.interpret(source, "");
        assertTrue(res instanceof Result.Err);
    }
}
