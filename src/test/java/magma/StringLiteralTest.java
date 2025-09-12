package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

public class StringLiteralTest {

	@Test
	public void doubleQuotedString() {
		// Expect the interpreter to evaluate a double-quoted literal to the
		// string value without surrounding quotes
		String src = "\"hello\"";
		assertValid(src, "hello");
	}
}
