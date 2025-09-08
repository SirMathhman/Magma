package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class AssignmentTest {
	@Test
	void bareAssignmentIsInvalid() {
		assertInvalid("x = 10;");
	}

	@Test
	void assignToImmutableShouldBeInvalid() {
		assertInvalid("let x = 0; x = 100; x");
	}
}
