package magma;

import org.junit.jupiter.api.Test;

public class ArrowFunctionTest {
	@Test
	public void arrowZeroArgCall() {
		String src = "let func = () => 100; func()";
		TestUtils.assertValid(src, "100");
	}
}
