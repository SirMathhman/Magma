package magma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterTest {
	@Test
	void valid() {
		assertValid("5", "5");
	}

	@Test
	void integerSuffix() {
		assertValid("5I32", "5");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"5U8", "5U16", "5U32", "5U64",
			"5I8", "5I16", "5I32", "5I64"
	})
	void supportedNumericSuffixes(String input) {
		assertValid(input, "5");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"5U7", "5U128", "5X32", "5I7", "5i32"
	})
	void unsupportedNumericSuffixes(String input) {
		// these should be treated as invalid inputs
		assertInvalid(input);
	}

	@ParameterizedTest
	@ValueSource(strings = { "-5", "-5I32", "-5I8", "-5I64" })
	void negativeNumbersSupported(String input) {
		// negative plain integers and signed suffixes should be valid
		assertValid(input, "-5");
	}

	@ParameterizedTest
	@ValueSource(strings = { "-5U8", "-5U16", "-5U32", "-5U64" })
	void negativeWithUnsignedSuffixIsInvalid(String input) {
		// negative numbers with unsigned suffixes should be invalid
		assertInvalid(input);
	}

	@Test
	void additionPlainPlain() {
		assertValid("10 + 10", "20");
	}

	@Test
	void additionPlainSuffixedRight() {
		assertValid("10 + 10I32", "20");
	}

	@Test
	void additionSuffixedLeftPlain() {
		assertValid("10I32 + 10", "20");
	}

	@Test
	void additionMixedUnsigned_leftSuffixed() {
		assertValid("10U8 + 10", "20");
	}

	@Test
	void additionMixedUnsigned_rightSuffixed() {
		assertValid("10 + 10U8", "20");
	}

	@Test
	void additionMismatchedSuffixesInvalid() {
		// different explicit suffixes should be rejected
		assertInvalid("10U8 + 30I64");
	}

	// Subtraction
	@Test
	void subtractionPlainPlain() {
		assertValid("10 - 3", "7");
	}

	@Test
	void subtractionPlainSuffixedRight() {
		assertValid("10 - 3I32", "7");
	}

	@Test
	void subtractionSuffixedLeftPlain() {
		assertValid("10I32 - 3", "7");
	}

	@Test
	void subtractionBothSuffixedPreserve() {
		assertValid("10U8 - 3U8", "7U8");
	}

	@Test
	void subtractionMismatchedSuffixesInvalid() {
		assertInvalid("10U8 - 30I64");
	}

	// Multiplication
	@Test
	void multiplicationPlainPlain() {
		assertValid("3 * 4", "12");
	}

	@Test
	void multiplicationPlainSuffixedRight() {
		assertValid("3 * 4I32", "12");
	}

	@Test
	void multiplicationSuffixedLeftPlain() {
		assertValid("3I32 * 4", "12");
	}

	@Test
	void multiplicationBothSuffixedPreserve() {
		assertValid("3U8 * 4U8", "12U8");
	}

	@Test
	void multiplicationMismatchedSuffixesInvalid() {
		assertInvalid("3U8 * 4I64");
	}

	// Division
	@Test
	void divisionPlainPlain() {
		assertValid("10 / 2", "5");
	}

	@Test
	void divisionPlainSuffixedRight() {
		assertValid("10 / 2I32", "5");
	}

	@Test
	void divisionSuffixedLeftPlain() {
		assertValid("10I32 / 2", "5");
	}

	@Test
	void divisionBothSuffixedPreserve() {
		assertValid("10U8 / 2U8", "5U8");
	}

	@Test
	void divisionMismatchedSuffixesInvalid() {
		assertInvalid("10U8 / 2I64");
	}

	// Modulo
	@Test
	void moduloPlainPlain() {
		assertValid("10 % 3", "1");
	}

	@Test
	void moduloPlainSuffixedRight() {
		assertValid("10 % 3I32", "1");
	}

	@Test
	void moduloSuffixedLeftPlain() {
		assertValid("10I32 % 3", "1");
	}

	@Test
	void moduloBothSuffixedPreserve() {
		assertValid("10U8 % 3U8", "1U8");
	}

	@Test
	void moduloMismatchedSuffixesInvalid() {
		assertInvalid("10U8 % 3I64");
	}

	@Test
	void letAssignmentAndRead() {
		assertValid("let x : I32 = 0; x", "0");
	}

	@Test
	void letWithoutType() {
		assertValid("let x = 10; x", "10");
	}

	@Test
	void letAssignmentTypeMismatch() {
		// assigning an unsigned literal to a signed typed variable should be invalid
		assertInvalid("let x : I32 = 0U8;");
	}

	private void assertValid(String input, String output) {
		Interpreter.interpret(input)
				.consume(result -> Assertions.assertEquals(output, result), error -> Assertions.fail(error.display()));
	}

	@Test
	void invalid() {
		assertInvalid("test");
	}

	private void assertInvalid(String input) {
		assertTrue(Interpreter.interpret(input).isErr());
	}
}