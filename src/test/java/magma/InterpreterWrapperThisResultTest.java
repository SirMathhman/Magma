package magma;

import static magma.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class InterpreterWrapperThisResultTest {

	@Test
	public void wrapperThisResult() {
		String src = "class fn Wrapper() => {let result = 100; fn get() => this.result; this} Wrapper().get()";
		assertValid(src, "100");
	}
}