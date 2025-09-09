package magma;

import org.junit.jupiter.api.Test;

public class InterpreterLetBindingTest {
	@Test
	public void unannotatedLet() {
		TestUtils.assertValid("let x = 3; x", "3");
	}

	@Test
	public void annotatedLetTyped() {
		TestUtils.assertValid("let x : U8 = 3U8; x", "3");
	}

	@Test
	public void annotatedLetBoolTrue() {
		TestUtils.assertValid("let x : Bool = true; x", "true");
	}
}
