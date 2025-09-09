package magma;

import org.junit.jupiter.api.Test;

// ...existing imports...

public class InterpreterFeatureTest {
	@Test
	public void literalInteger_returnsSameValue() {
		TestUtils.assertOkOutput("5", "5");
	}
}
