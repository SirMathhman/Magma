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
	void mutableAssignmentWithOtherVars() {
		assertValid("let mut x = 10; let y = 5; x = 20;", "20");
	}

	@Test
	void arrayDeclarationAndIndexing() {
		assertValid("let x : [I32; 3] = [1, 2, 3]; x[1]", "2");
	}

	@Test
	void booleanArrayDeclarationAndIndexing() {
		assertValid("let b = [true, false]; b[0]", "true");
		assertValid("let b = [true, false]; b[1]", "false");
	}

	@Test
	void arrayInitializerArithmetic() {
		// array items may be arithmetic expressions
		assertValid("let a = [1+1, 2*2]; a[0]", "2");
		assertValid("let a = [1+1, 2*2]; a[1]", "4");
	}

	@Test
	void unsignedArrayRejectsNegativeElementLiteral() {
		// declaring an unsigned array type and initializing with a negative element
		// should be invalid
		assertInvalid("let x : [U8; 1] = [-1];");
	}

	@Test
	void plainArrayDefaultsToI32() {
		// plain numeric array literal should default to [I32; N]
		assertValid("let arr = [1, 2, 3]; arr[0]", "1");
		assertValid("let arr = [1, 2, 3]; arr[2]", "3");
	}

	@Test
	void nestedTypedArrayDeclarationAndIndexing() {
		// nested typed arrays like [[I32;2];2] should parse and index correctly
		assertValid("let x : [[I32; 2]; 2] = [[1, 2], [3, 4]]; x[0][1]", "2");
		assertValid("let x : [[I32; 2]; 2] = [[1, 2], [3, 4]]; x[1][0]", "3");
	}

	@Test
	void debugArray() {
		var res = Interpreter.interpret("let x : [I32; 3] = [1, 2, 3]; x[1]");
		System.out.println(res);
		if (res.isErr())
			System.err.println(((magma.result.Err<String, magma.InterpretError>) res).error().display());
	}

	@Test
	void letAssignmentTypeMismatch() {
		// assigning an unsigned literal to a signed typed variable should be invalid
		assertInvalid("let x : I32 = 0U8;");
	}

	@Test
	void letAssignmentUnsignedDeclaredRejectsNegativeLiteral() {
		// declaring an unsigned type and assigning a negative literal should be invalid
		assertInvalid("let x : U32 = -1;");
	}

	@Test
	void letAssignmentTypeMismatchBinary() {
		// result of 0U8 + 20U8 has explicit U8 suffix and should not be assignable to
		// I32
		assertInvalid("let x : I32 = 0U8 + 20U8;");
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

	@Test
	void trueLiteral() {
		assertValid("true", "true");
	}

	@Test
	void falseLiteral() {
		assertValid("false", "false");
	}

	@Test
	void booleanAnd() {
		assertValid("true && true", "true");
		assertValid("true && false", "false");
		assertValid("false && false", "false");
	}

	@Test
	void booleanOr() {
		assertValid("true || true", "true");
		assertValid("true || false", "true");
		assertValid("false || false", "false");
	}

	@Test
	void invalidOrMixedNumericBoolean() {
		// mixing numeric and boolean with || should be invalid
		assertInvalid("1 || true");
		assertInvalid("true || 1");
	}

	@Test
	void invalidOrWithSpacedPipes() {
		// malformed operator with separated pipes should be invalid
		assertInvalid("true | | 1");
	}

	@Test
	void invalidPlusMixedNumericBoolean() {
		// mixing numeric and boolean with + should be invalid
		assertInvalid("1 + true");
		assertInvalid("true + 1");
	}

	@Test
	void integerPrecedenceAndParentheses() {
		// * has higher precedence than +
		assertValid("2 + 3 * 4", "14");
		assertValid("(2 + 3) * 4", "20");
	}

	@Test
	void multiplicationAndDivisionPrecedenceOverAddSub() {
		// multiplication and division should be evaluated before addition/subtraction
		assertValid("8 + 12 / 4", "11"); // 12/4 = 3 -> 8+3 = 11
		assertValid("8 - 12 / 4", "5"); // 12/4 = 3 -> 8-3 = 5
		assertValid("2 + 3 * 4 - 5 / 1", "9"); // 3*4=12, 5/1=5 -> 2+12-5 = 9
		assertValid("2 + 6 / 3 * 4", "10"); // 6/3=2, 2*4=8 -> 2+8 = 10
	}

	@Test
	void numericComparisonsWorkAndRequireSameType() {
		// simple comparisons
		assertValid("3 < 4", "true");
		assertValid("4 < 3", "false");
		assertValid("3 <= 3", "true");
		assertValid("5 >= 2", "true");
		assertValid("5 == 5", "true");
		assertValid("5 != 6", "true");

		// comparisons with suffix-preservation and same type
		assertValid("3I32 < 4I32", "true");
		assertInvalid("3I32 < 4I64"); // mismatched types

		// comparisons must be numeric on both sides
		assertInvalid("true < 1");
		assertInvalid("1 > false");
	}

	@Test
	void booleanPrecedenceAndParentheses() {
		// && has higher precedence than ||
		assertValid("true || false && false", "true");
		assertValid("(true || false) && false", "false");
	}
}