package magma;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	// Helper methods
	static void assertValid(String input, String expected) {
		try {
			assertEquals(expected, Compiler.compile(input));
		} catch (CompileException e) {
			fail("Expected valid input but got CompileException: " + input);
		}
	}

	static void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}
}
