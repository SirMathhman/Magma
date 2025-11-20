package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
