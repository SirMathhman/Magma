package magma;

import org.junit.jupiter.api.Test;

// ...existing imports...
public class InterpreterTypedLiteralTest {
	@Test
	public void typedIntegerSuffix_returnsBaseValue() {
		TestUtils.assertOkOutput("5I32", "5");
	}

	@Test
	public void unsigned8_maxValue_ok() {
		TestUtils.assertOkOutput("255U8", "255");
	}

	@Test
	public void unsigned8_overflow_err() {
		TestUtils.assertErr("256U8");
	}

	@Test
	public void signed8_minValue_ok() {
		TestUtils.assertOkOutput("-128I8", "-128");
	}

	@Test
	public void unsignedNegative_err() {
		TestUtils.assertErr("-1U8");
	}

	@Test
	public void unsigned64_large_ok() {
		// max U64 is 18446744073709551615
		TestUtils.assertOkOutput("18446744073709551615U64", "18446744073709551615");
	}

	@Test
	public void signed64_overflow_err() {
		// one more than I64 max (9223372036854775807)
		TestUtils.assertErr("9223372036854775808I64");
	}
}
