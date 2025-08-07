package magma;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	static void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	static void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}
}
