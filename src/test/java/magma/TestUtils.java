package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
    private TestUtils() {
    }

    public static void assertValid(String source, String expected) {
        Interpreter interp = new Interpreter();
        Result<String, InterpretError> res = interp.interpret(source, "");
        switch (res) {
            case Result.Ok<String, InterpretError> ok -> {
                assertEquals(expected, ok.value());
            }
            case Result.Err<String, InterpretError>(InterpretError error) -> {
                fail(error.display());
            }
        }
    }

    public static void assertInvalid(String source) {
        Interpreter interp = new Interpreter();
        Result<String, InterpretError> res = interp.interpret(source, "");
        switch (res) {
            case Result.Ok<String, InterpretError> ok -> {
                fail(ok.value());
            }
            case Result.Err<String, InterpretError>(InterpretError error) -> {
                assertNotNull(error.display());
            }
        }
    }
}
