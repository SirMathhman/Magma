package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
	@Test
	void digit() {
		assertEquals("5", Main.run("5"));
	}
}
