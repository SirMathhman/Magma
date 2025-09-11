package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

public class ClassTest {
	@Test
	public void wrapperReturnThis() {
		TestUtils.assertValid(
				"class fn Wrapper() => {let result = 100; fn get() => this.result; return this;} Wrapper().get()", "100");
	}

	@Test
	public void wrapperThisResult() {
		String src = "class fn Wrapper() => {let result = 100; fn get() => this.result; this} Wrapper().get()";
		assertValid(src, "100");
	}

	@Test
	public void wrapperInvalidReturn() {
		TestUtils
				.assertInvalid("class fn Wrapper() => {let result = 100; fn get() => this.result; return 100} Wrapper().get()");
	}
}
