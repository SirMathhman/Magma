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

	private static void assertInterpretsTo(String input, String expected) {
		var res = App.interpret(input);
		switch (res) {
			case Result.Ok<?, ?> ok -> assertEquals(expected, ok.value());
			case Result.Err<?, ?> err -> fail("Expected Ok but got Err: " + err.error());
		}
	}

	private static void assertInterpretsErr(String input) {
		var res = App.interpret(input);
		switch (res) {
			case Result.Err<?, ?> err -> assertNotNull(err.error());
			case Result.Ok<?, ?> ok -> fail("Expected Err but got Ok: " + ok.value());
		}
	}
}
