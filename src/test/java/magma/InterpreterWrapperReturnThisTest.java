package magma;

import org.junit.jupiter.api.Test;

public class InterpreterWrapperReturnThisTest {
	@Test
	public void wrapperReturnThis() {
		TestUtils.assertValid(
				"class fn Wrapper() => {let result = 100; fn get() => this.result; return this;} Wrapper().get()", "100");
	}
}
