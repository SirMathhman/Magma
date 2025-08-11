package magma;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTestBase {
	protected void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	protected void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	protected void assertPrettyValid(String input, String output) {
		try {
			String compactC = Compiler.compile(input);
			String prettyC = CPrettyPrinter.prettyPrint(compactC);
			assertEquals(output, prettyC);
		} catch (CompileException e) {
			fail(e);
		}
	}
}