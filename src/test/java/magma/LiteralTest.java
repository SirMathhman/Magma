package magma;

import org.junit.jupiter.api.Test;

public class LiteralTest {
	@Test
	public void typedIntegerSuffix() {
		TestUtils.assertValid("5I32", "5");
	}

	@Test
	public void unsigned8Max() {
		TestUtils.assertValid("255U8", "255");
	}

	@Test
	public void unsigned8Overflow() {
		TestUtils.assertInvalid("256U8");
	}

	@Test
	public void signed8Min() {
		TestUtils.assertValid("-128I8", "-128");
	}

	@Test
	public void unsignedNegativeErr() {
		TestUtils.assertInvalid("-1U8");
	}

	@Test
	public void unsigned64Large() {
		TestUtils.assertValid("18446744073709551615U64", "18446744073709551615");
	}

	@Test
	public void signed64Overflow() {
		TestUtils.assertInvalid("9223372036854775808I64");
	}
}
