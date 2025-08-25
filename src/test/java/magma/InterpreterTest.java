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
	void additionMixedUnsignedIsInvalid_leftSuffixed() {
		assertInvalid("10U8 + 10");
	}

	@Test
	void additionMixedUnsignedIsInvalid_rightSuffixed() {
		assertInvalid("10 + 10U8");
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