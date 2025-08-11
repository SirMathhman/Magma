package magma;

import org.junit.jupiter.api.Test;

class CompilerBasicTest extends CompilerTestBase {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void invalid() {
		assertInvalid("nvalid");
	}

	@Test
	void identifier() {
		assertValid("let x = 100; let y = x;", "int32_t x = 100; int32_t y = x;");
	}
}