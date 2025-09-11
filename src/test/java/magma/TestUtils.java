package magma;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
	private TestUtils() {
	}

	public static void assertValid(String source, String expected) {
		assertTimeout(Duration.ofSeconds(1), () -> {
			switch (new Interpreter().interpret(source)) {
				case Result.Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
				case Result.Err<String, InterpretError>(InterpretError error) -> fail(error.display());
			}
		});
	}

	public static void assertInvalid(String source) {
		assertTimeout(Duration.ofSeconds(1), () -> {
			switch (new Interpreter().interpret(source)) {
				case Result.Ok<String, InterpretError> ok -> fail(ok.value());
				case Result.Err<String, InterpretError>(InterpretError error) -> assertNotNull(error.display());
			}
		});
	}
}
