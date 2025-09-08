package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class BlockAndIfTest {
	@Test
	void blockLiteralSimple() {
		assertValid("{5}", "5");
	}

	@Test
	void ifExpressionSimple() {
		assertValid("if (true) 3 else 5", "3");
	}

	@Test
	void ifExpressionWithBlockBranches() {
		assertValid("if (true) {3} else {5}", "3");
	}

	@Test
	void letIfAsRhs() {
		assertValid("let x = if (true) {3} else {5}; x", "3");
	}
}
