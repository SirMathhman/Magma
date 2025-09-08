package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class LiteralTest {
	@Test
	void booleanLiteral() {
		assertValid("true", "true");
	}

	@Test
	void letBoolAssignedNumberShouldBeInvalid() {
		assertInvalid("let x : Bool = 0;");
	}
}
