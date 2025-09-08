package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class ArithmeticTest {
	@Test
	void numberLiteral() {
		assertValid("5", "5");
	}

	@Test
	void numberLiteralWithTrailing() {
		assertValid("5I32", "5");
	}

	@Test
	void additionSimple() {
		assertValid("2 + 3", "5");
	}

	@Test
	void additionWithTrailingType() {
		assertValid("2 + 3I32", "5");
	}

	@Test
	void subtractionSimple() {
		assertValid("5 - 3", "2");
	}

	@Test
	void multiplicationSimple() {
		assertValid("5 * 3", "15");
	}

	@Test
	void chainedAddition() {
		assertValid("1 + 2 + 3", "6");
	}

	@Test
	void additionMixedUnsignedAndSigned() {
		assertInvalid("2U8 + 3I32");
	}

	@Test
	void mixedSuffixesInChain() {
		assertInvalid("0I8 + 0 + 0U32");
	}
}
