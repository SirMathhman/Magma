package magma;

import static org.junit.jupiter.api.Assertions.*;

public final class TestUtils {
	public static final String PRELUDE = "intrinsic fn readInt() : I32; ";

	private TestUtils() {
	}

	public static void assertInvalid(String source) {
		assertThrows(CompileException.class, () -> Runner.run(source, ""));
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
