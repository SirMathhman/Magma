package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
	@Test
	public void testAdd() {
		assertEquals(3, App.add(1, 2));
	}

	@Test
	public void testInterpret() {
		String in = "echo";
		assertEquals(in, App.interpret(in));
	}
}
