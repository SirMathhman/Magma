package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterThisLetCaptureTest {
	@Test
	public void wrapperThisMethodCap() {
		String source = "fn Wrapper() => {let result = 100; fn get() => result; this} Wrapper().get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}
}
