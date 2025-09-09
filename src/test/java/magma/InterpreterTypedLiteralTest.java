package magma;

import org.junit.jupiter.api.Test;

// ...existing imports...
public class InterpreterTypedLiteralTest {
	@Test
	public void typedIntegerSuffix_returnsBaseValue() {
		TestUtils.assertValid("5I32", "5");
	}

	@Test
	public void unsigned8_maxValue_ok() {
		TestUtils.assertValid("255U8", "255");
	}

	@Test
	public void unsigned8_overflow_err() {
		TestUtils.assertInvalid("256U8");
	}

	@Test
	public void signed8_minValue_ok() {
		TestUtils.assertValid("-128I8", "-128");
	}

	@Test
	public void unsignedNegative_err() {
		TestUtils.assertInvalid("-1U8");
	}

	@Test
	public void unsigned64_large_ok() {
		// max U64 is 18446744073709551615
		TestUtils.assertValid("18446744073709551615U64", "18446744073709551615");
	}

	@Test
	public void signed64_overflow_err() {
		// one more than I64 max (9223372036854775807)
		TestUtils.assertInvalid("9223372036854775808I64");
	}
}
