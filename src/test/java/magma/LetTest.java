package magma;

import org.junit.jupiter.api.Test;

public class LetTest {
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

	@Test
	public void letBindingLookup() {
		TestUtils.assertValid("let x : I32 = 3; x", "3");
	}

	@Test
	public void letExprReturns5() {
		TestUtils.assertValid("let x = 3 + 2; x", "5");
	}

	@Test
	public void letMutAssign100() {
		TestUtils.assertValid("let mut x = 0; x = 100; x", "100");
	}

	@Test
	public void assignImmutableErr() {
		TestUtils.assertInvalid("let x = 0; x = 100; x");
	}

	@Test
	public void letOnlyReturnsEmpty() {
		TestUtils.assertValid("let x : U8 = 3;", "");
	}

	@Test
	public void mutLetTypedMismatch() {
		TestUtils.assertInvalid("let mut x : U8 = 0; x = 100I32; x");
	}

	@Test
	public void declareThenAssign() {
		TestUtils.assertValid("let x : I32; x = 100; x", "100");
	}

	@Test
	public void declareReadInvalid() {
		TestUtils.assertInvalid("let x : I32; x");
	}

	@Test
	public void letArrayIndex() {
		TestUtils.assertValid("let x = [1]; x[0]", "1");
	}

	@Test
	public void indexedAssign1() {
		TestUtils.assertValid("let mut x = [0]; x[0] = 1; x[0]", "1");
	}

	@Test
	public void indexedAssignLet() {
		TestUtils.assertInvalid("let x = [0]; x[0] = 1; x[0]");
	}
}
