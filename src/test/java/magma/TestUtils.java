package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
	private TestUtils() {
	}

	public static void assertValid(String source, String expected) {
		switch (new Interpreter().interpret(source)) {
			case Result.Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Result.Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	public static void assertInvalid(String source) {
		switch (new Interpreter().interpret(source)) {
			case Result.Ok<String, InterpretError> ok -> fail(ok.value());
			case Result.Err<String, InterpretError>(InterpretError error) -> assertNotNull(error.display());
		}
	}
}
