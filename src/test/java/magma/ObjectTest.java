package magma;

import org.junit.jupiter.api.Test;

public class ObjectTest {
	@Test
	public void objTempValueIs100() {
		String src = "object Temp { let value = 100; } Temp.value";
		TestUtils.assertValid(src, "100");
	}
}
