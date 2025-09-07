package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
	@Test
	void testMessage() {
		assertEquals("Hello, Magma!", App.message());
	}
}
