package magma;

import org.junit.jupiter.api.Test;

public class InterpreterWrapperInvalidReturnTest {
	@Test
	public void wrapperInvalidReturn() {
		TestUtils
				.assertInvalid("class fn Wrapper() => {let result = 100; fn get() => this.result; return 100} Wrapper().get()");
	}
}
