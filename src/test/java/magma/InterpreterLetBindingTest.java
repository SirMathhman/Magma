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

	@Test
	public void unannotatedBoolLet() {
		TestUtils.assertValid("let x = true; x", "true");
	}

	@Test
	public void mutBoolAssignInvalid() {
		TestUtils.assertInvalid("let mut x = true; x = 100; x");
	}
}
