package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

	@Test
	void interpretReturnsSameString() {
		assertInterpretsTo("100", "100");
	}

	@Test
	void interpretExtractsLeadingDigits() {
		assertInterpretsTo("100U8", "100");
	}

	@Test
	void interpretNegativeReturnsErr() {
		assertInterpretsErr("-1U8");
	}

	@Test
	void interpretSignedNegativeReturnsOk() {
		assertInterpretsTo("-1I8", "-1");
	}

	@Test
	void interpretUnsignedOverflowReturnsErr() {
		assertInterpretsErr("256U8");
	}

	@Test
	void interpretU6Bounds() {
		assertInterpretsTo("63U6", "63");
		assertInterpretsErr("64U6");
	}

	@Test
	void interpretI8Bounds() {
		assertInterpretsTo("127I8", "127");
		assertInterpretsErr("128I8");
		assertInterpretsTo("-128I8", "-128");
		assertInterpretsErr("-129I8");
	}

	@Test
	void interpretU32Bounds() {
		assertInterpretsTo("4294967295U32", "4294967295");
		assertInterpretsErr("4294967296U32");
	}

	@Test
	void interpretAddition() {
		assertInterpretsTo("1 + 2", "3");
		assertInterpretsTo("1U8 + 2U8", "3");
	}

	@Test
	void interpretMultiplication() {
		assertInterpretsTo("2 * 3", "6");
	}

	@Test
	void interpretDivision() {
		assertInterpretsTo("4 / 2", "2");
	}

	@Test
	void interpretDivisionByZero() {
		assertInterpretsErr("4 / 0");
	}

	@Test
	void interpretDivisionByZeroNested() {
		assertInterpretsErr("4 / (2 - 2)");
	}

	@Test
	void interpretMixedOperators() {
		assertInterpretsTo("2 * 3 + 1", "7");
	}

	@Test
	void interpretParenthesizedAddition() {
		assertInterpretsTo("(2 + 3)", "5");
	}

	@Test
	void interpretParenthesizedMultiplication() {
		assertInterpretsTo("(2 + 3) * 4", "20");
	}

	@Test
	void interpretMixedTypedAddition() {
		assertInterpretsTo("1U8 + 2", "3");
	}

	@Test
	void interpretIncompatibleAddition() {
		assertInterpretsErr("1U8 + 2I8");
	}

	@Test
	void interpretThreeOperandAddition() {
		assertInterpretsTo("1 + 2 + 3", "6");
	}

	@Test
	void interpretUnsignedOverflowAddition() {
		assertInterpretsErr("255U8 + 1");
	}

	@Test
	void interpretSimpleSubtraction() {
		assertInterpretsTo("3 - 2", "1");
	}

	@Test
	void interpretBooleanTrue() {
		assertInterpretsTo("true", "true");
	}

	@Test
	void interpretBooleanFalse() {
		assertInterpretsTo("false", "false");
	}

	@Test
	void interpretEqualsTrue() {
		assertInterpretsTo("3 == 3", "true");
	}

	private static void assertInterpretsTo(String input, String expected) {
		var res = App.interpret(input);
		if (res instanceof Result.Ok) {
			var ok = (Result.Ok<String, String>) res;
			assertEquals(expected, ok.value());
		} else if (res instanceof Result.Err) {
			var err = (Result.Err<String, String>) res;
			fail("Expected Ok but got Err: " + err.error());
		} else {
			fail("Unexpected result variant");
		}
	}

	private static void assertInterpretsErr(String input) {
		var res = App.interpret(input);
		if (res instanceof Result.Err) {
			var err = (Result.Err<String, String>) res;
			assertNotNull(err.error());
		} else if (res instanceof Result.Ok) {
			var ok = (Result.Ok<String, String>) res;
			fail("Expected Err but got Ok: " + ok.value());
		} else {
			fail("Unexpected result variant");
		}
	}
}
