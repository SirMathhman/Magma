package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

	@Test
	void interpretReturnsSameString() {
		String sample = "Hello, Magma!";
		assertEquals(sample, App.interpret(sample));
	}
}
