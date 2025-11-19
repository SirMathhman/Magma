package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

	@Test
	void interpretReturnsSameString() {
		String sample = "100";
		var res = App.interpret(sample);
		assertTrue(res instanceof Result.Ok);
		assertEquals(sample, ((Result.Ok<String, String>) res).value());
	}

	@Test
	void interpretExtractsLeadingDigits() {
		String sample = "100U8";
		var res = App.interpret(sample);
		assertTrue(res instanceof Result.Ok);
		assertEquals("100", ((Result.Ok<String, String>) res).value());
	}
}
