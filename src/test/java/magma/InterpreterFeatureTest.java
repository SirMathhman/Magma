package magma;

import org.junit.jupiter.api.Test;

// ...existing assertions are provided by TestUtils; no direct static imports needed

public class InterpreterFeatureTest {
	@Test
	public void simpleAddition() {
		TestUtils.assertValid("1 + 2", "3");
	}

	@Test
	public void literalInteger() {
		TestUtils.assertValid("5", "5");
	}

	@Test
	public void typedAddition() {
		TestUtils.assertValid("1U8 + 2U8", "3");
	}

	@Test
	public void typedUntypedAdd() {
		// New acceptance: untyped literal can be added to a typed literal if it fits the width
		TestUtils.assertValid("1U8 + 2", "3");
	}

	@Test
	public void untypedTypedAdd() {
		// Symmetric acceptance: typed operand may be on the right-hand side
		TestUtils.assertValid("1 + 2U8", "3");
	}

	@Test
	public void letBindingLookup() {
		// New feature: let binding with type annotation and subsequent lookup
		TestUtils.assertValid("let x : I32 = 3; x", "3");
	}

	@Test
	public void mismatchedTypedErr() {
		// Mixed unsigned/signed width should be invalid per new acceptance criteria
		TestUtils.assertInvalid("1U8 + 2I32");
	}

	@Test
	public void letTypedMismatchErr() {
		// Assigning a typed literal with different signedness/width to a typed let should be invalid
		TestUtils.assertInvalid("let x : U8 = 3I32; x");
	}

	@Test
	public void letOnlyReturnsEmpty() {
		// A program that contains only a let-binding (no final expression) should return an empty string
		TestUtils.assertValid("let x : U8 = 3;", "");
	}
}
