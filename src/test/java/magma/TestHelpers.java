package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class TestHelpers {

	private TestHelpers() {
	}

	public static void assertInterpEquals(String src, String expected) {
		Result<String, InterpretError> r = new Interpreter().interpret(src);
		if (r instanceof Result.Err) {
			fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
		}
		assertEquals(expected, ((Result.Ok<String, InterpretError>) r).value());
	}
}
