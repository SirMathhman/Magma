package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterWrapperThisTest {
	@Test
	public void wrapperThisMethod() {
		String source = "fn Wrapper() => {fn get() => 100; this} Wrapper().get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}
}
