package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {
	@Test
	public void testInterpret() {
		String in = "echo";
		assertEquals(in, App.interpret(in));
	}

	@Test
	public void testInterpretNumericSuffix() {
		assertEquals("100", App.interpret("100U8"));
	}

	@Test
	public void testInterpretNegativeU8Throws() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("-1U8"));
	}
}
