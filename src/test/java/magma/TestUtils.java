package magma;

import magma.result.Err;
import magma.result.Ok;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	static void assertValid(String source, String output) {
		switch (Interpreter.interpret(source)) {
			case Ok(var value) -> assertEquals(output, value);
			case Err(var error) -> fail(error.display());
		}
	}

	static void assertInvalid(String input) {
		assertTrue(Interpreter.interpret(input).isErr());
	}
}
