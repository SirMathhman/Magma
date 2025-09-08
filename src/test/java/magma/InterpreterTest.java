package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class InterpreterTest {
	@Test
	void emptyAndUndefinedSmoke() {
		// Keep a couple of high-level smoke tests here; detailed cases are
		// split into feature-focused test classes.
		assertValid("", "");
		assertInvalid("test");
	}
}
