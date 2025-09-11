package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterParamCaptureTest {
	@Test
	public void wrapperParamCapture() {
		String source = "fn Wrapper(result : I32) => {fn get() => result; this} Wrapper(100).get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}
}
