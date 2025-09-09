package magma;

import org.junit.jupiter.api.Test;

// ...existing assertions are provided by TestUtils; no direct static imports needed

public class InterpreterFeatureTest {
	@Test
	public void interpretSimpleAddition_returnsSum() {
		TestUtils.assertOkOutput("1 + 2", "3");
	}

	@Test
	public void literalInteger_returnsSameValue() {
		TestUtils.assertOkOutput("5", "5");
	}

	@Test
	public void additionWithTypedOperands_returnsSum() {
		TestUtils.assertOkOutput("1U8 + 2U8", "3");
	}

	@Test
	public void additionWithMismatchedTypedOperands_isErr() {
		// Mixed unsigned/signed width should be invalid per new acceptance criteria
		TestUtils.assertErr("1U8 + 2I32");
	}
}
