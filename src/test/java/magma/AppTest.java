package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

	@Test
	void interpretReturnsSameString() {
		String sample = "100";
		assertEquals(sample, App.interpret(sample));
	}

	@Test
	void interpretExtractsLeadingDigits() {
		String sample = "100U8";
		assertEquals("100", App.interpret(sample));
	}
}
