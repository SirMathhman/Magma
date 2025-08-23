package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
	public static final String PRELUDE = "intrinsic fn readInt() : I32; ";

	private TestUtils() {
	}

	public static void assertInvalid(String source) {
		try {
			Runner.run(source, "");
			fail("Expected CompileException or RunException for invalid source: '" + source + "'");
		} catch (CompileException | RunException e) {
			// expected
		}
	}

	public static void assertValid(String source, String input, int expected) {
		try {
			assertEquals(expected, Runner.run(source, input));
		} catch (CompileException | RunException e) {
			fail(e);
		}
	}

	public static void assertValidWithPrelude(String source, String input, int expected) {
		assertValid(PRELUDE + source, input, expected);
	}
}
