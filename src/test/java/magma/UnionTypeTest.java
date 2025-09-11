package magma;

import org.junit.jupiter.api.Test;

public class UnionTypeTest {

	@Test
	public void unionIsPositive() {
		String src = "type MyUnion = I32 | Bool; let x : MyUnion = 100; x is I32";
		TestUtils.assertValid(src, "true");
	}

	@Test
	public void unionIsNegative() {
		String src = "type MyUnion = I32 | Bool; let x : MyUnion = true; x is I32";
		TestUtils.assertValid(src, "false");
	}
}
