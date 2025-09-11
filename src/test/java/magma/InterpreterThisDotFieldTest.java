package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterThisDotFieldTest {
	@Test
	public void thisDotField() {
		String source = "fn Wrapper() => {let result = 100; fn get() => this.result; this} Wrapper().get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}
}
