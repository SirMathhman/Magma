package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

	@Test
	void messageReturnsHello() {
		assertEquals("Hello, magma!", App.message());
	}
}
