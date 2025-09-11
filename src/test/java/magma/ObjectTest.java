package magma;

import org.junit.jupiter.api.Test;

public class ObjectTest {
	@Test
	public void objTempValueIs100() {
		String src = "object Temp { let value = 100; } Temp.value";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void objectMethodWithArgs() {
		// Define an object with a method that adds two parameters and returns the sum (compact body)
		String src = "object Adder { fn add(a : I32, b : I32) => a + b; } Adder.add(3, 4)";
		TestUtils.assertValid(src, "7");
	}
}
