package magma;

import org.junit.jupiter.api.Test;

// ...existing assertions are provided by TestUtils; no direct static imports needed

public class InterpreterFeatureTest {
	@Test
	public void interpretSimpleAddition_returnsSum() {
		TestUtils.assertValid("1 + 2", "3");
	}

	@Test
	public void literalInteger_returnsSameValue() {
		TestUtils.assertValid("5", "5");
	}

	@Test
	public void additionWithTypedOperands_returnsSum() {
		TestUtils.assertValid("1U8 + 2U8", "3");
	}

	@Test
	public void interpretTypedAndUntypedAddition_returnsSum() {
		// New acceptance: untyped literal can be added to a typed literal if it fits the width
		TestUtils.assertValid("1U8 + 2", "3");
	}

	@Test
	public void additionWithMismatchedTypedOperands_isErr() {
		// Mixed unsigned/signed width should be invalid per new acceptance criteria
		TestUtils.assertInvalid("1U8 + 2I32");
	}
}
